package br.com.listennow.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.listennow.database.dao.LogDao
import br.com.listennow.model.Log
import br.com.listennow.webclient.log.model.InsertLogRequest
import br.com.listennow.webclient.log.model.LogRequest
import br.com.listennow.webclient.log.service.LogWebClient
import br.com.listennow.workmanager.SyncLogsWorker

class LogRepository(
    private val logDao: LogDao,
    private val logWebClient: LogWebClient,
    private val workManager: WorkManager
) {
    suspend fun upsertLog(log: Log) {
        logDao.save(log)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncLogsWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            SyncLogsWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    suspend fun syncPendingLogs(deviceId: String) {
        val pendingLogs = logDao.getPendingLogs()

        pendingLogs.windowed(
            size = 20,
            step = 20,
            partialWindows = true
        ).forEachIndexed { _, chunk ->
            val mappedLogs = chunk.map { pendingLog ->
                LogRequest(
                    id = pendingLog.id,
                    level = pendingLog.level,
                    tag = pendingLog.tag,
                    message = pendingLog.message,
                    createdAt = pendingLog.createAt
                )
            }

            val success = logWebClient.createLog(
                createLogRequest = InsertLogRequest(
                    logs = mappedLogs,
                    deviceId = deviceId
                )
            )

            if (success) {
                chunk.forEach { log ->
                    log.isSynced = true
                }

                logDao.save(chunk)
            }
        }
    }
}