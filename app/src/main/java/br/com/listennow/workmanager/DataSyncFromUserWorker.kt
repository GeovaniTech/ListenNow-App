package br.com.listennow.workmanager

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.listennow.R
import br.com.listennow.fragments.MainActivity
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import br.com.listennow.utils.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Import songs and playlists from another user's device.
 * Trigger by the user on DeviceInfosFragment.
 */
@HiltWorker
class DataSyncFromUserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val idUserReceiver = inputData.getString(ID_USER_RECEIVER) ?: return Result.failure(
            workDataOf(INVALID_VALUE_FOR_PARAMETER to "userReceiver is null")
        )

        val idUserWithData = inputData.getString(ID_USER_WITH_DATA) ?: return Result.failure(
            workDataOf(INVALID_VALUE_FOR_PARAMETER to "userWithData is null")
        )

        val arraySongsIds = inputData.getStringArray(SONGS_IDS) ?: return Result.failure(
            workDataOf(INVALID_VALUE_FOR_PARAMETER to "songsIds is null")
        )

        val songsIds = arraySongsIds.toList()
        val notificationId = NotificationUtil.getUniqueNotificationId()

        importSongs(
            notificationId = notificationId,
            idUserReceiver = idUserReceiver,
            songsIds = songsIds
        )

        importPlaylists(
            notificationId = notificationId,
            idUserReceiver = idUserReceiver,
            idUserWithData = idUserWithData
        )

        return Result.success()
    }

    private suspend fun importSongs(
        notificationId: Int,
        idUserReceiver: String,
        songsIds: List<String>
    ) {
        withContext(Dispatchers.IO) {
            val title = applicationContext.getString(R.string.importing_songs)
            var description = applicationContext.getString(R.string.importing_songs_from_another_device, songsIds.size)

            var notification = notificationBuilder(title, description).run {
                setProgress(songsIds.size, 0, false)
                build()
            }

            createForegroundInfo(notificationId, notification)

            val importedSongs = mutableListOf<String>()

            songsIds.windowed(20, 20, partialWindows = true).forEachIndexed{ _, chunk ->
                if (songRepository.copySongsFromAnotherDevice(idUserReceiver, chunk)) {
                    songRepository.updateAll(idUserReceiver)

                    importedSongs.addAll(chunk)

                    description = applicationContext.getString(R.string.importing_songs_from_another_device, songsIds.size - importedSongs.size)

                    notification = notificationBuilder(title, description).run {
                        setProgress(songsIds.size, importedSongs.size, false)
                        build()
                    }

                    createForegroundInfo(notificationId, notification)
                }
            }

            notification = notificationBuilder(
                title = applicationContext.getString(R.string.importing_songs_has_finished),
                description = applicationContext.getString(R.string.get_ready_to_listen_to_your_songs)
            ).build()

            createForegroundInfo(notificationId, notification)
        }
    }

    private suspend fun importPlaylists(
        notificationId: Int,
        idUserReceiver: String,
        idUserWithData: String
    ) {
        withContext(Dispatchers.IO) {
           var notification = notificationBuilder(
                title = applicationContext.getString(R.string.importing_playlists),
                description = applicationContext.getString(R.string.finishing_importing_playlists)
            ).run {
                setProgress(0,0, true)
                build()
            }

            createForegroundInfo(notificationId, notification)

            playlistRepository.copyPlaylistsFromAnotherDevice(
                clientReceiverId = idUserReceiver,
                clientCopyFromId = idUserWithData
            )

            notification = notificationBuilder(
                title = applicationContext.getString(R.string.importing_playlists_has_finished),
                description = applicationContext.getString(R.string.what_mood_are_you_in)
            ).build()

            createForegroundInfo(notificationId, notification)
        }
    }

    private fun notificationBuilder(
        title: String,
        description: String
    ) =
        NotificationCompat.Builder(
            applicationContext,
            MainActivity.IMPORT_ALL_SONGS_FOREGROUND_SERVICE_NOTIFICATION_CHANNEl
        )
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_notification_icon)

    private suspend fun createForegroundInfo(
        notificationId: Int,
        notification: Notification
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForeground(ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC))
        } else {
            setForeground(ForegroundInfo(notificationId, notification))
        }
    }

    companion object {
        const val ID_USER_RECEIVER = "ID_USER_RECEIVER"
        const val ID_USER_WITH_DATA = "ID_USER_WITH_DATA"
        const val SONGS_IDS = "SONGS_IDS"
        const val INVALID_VALUE_FOR_PARAMETER = "INVALID_VALUE_FOR_PARAMETER"
    }
}
