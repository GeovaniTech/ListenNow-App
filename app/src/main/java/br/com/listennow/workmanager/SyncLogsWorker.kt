package br.com.listennow.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.listennow.enums.EnumLevelLog
import br.com.listennow.model.Log
import br.com.listennow.repository.LogRepository
import br.com.listennow.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncLogsWorker  @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val logRepository: LogRepository,
    private val userRepository: UserRepository
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        try {
            val user = userRepository.findUser()

            logRepository.syncPendingLogs(
                deviceId = user!!.id
            )
            return Result.success()

        }  catch (e: Exception) {
            logRepository.upsertLog(
                Log(
                    level = EnumLevelLog.ERROR.name,
                    tag = WORK_NAME,
                    message = e.message ?: "Something happened while sending logs to server."
                )
            )
        }

        return Result.failure()
    }

    companion object {
        const val WORK_NAME: String = "SyncLogsWorker"
    }
}