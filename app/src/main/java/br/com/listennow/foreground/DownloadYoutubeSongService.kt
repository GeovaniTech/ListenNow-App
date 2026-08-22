package br.com.listennow.foreground

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.listennow.R
import br.com.listennow.fragments.MainActivity
import br.com.listennow.model.Song
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Start the download on API and then sync the song
 * on the mobile device.
 */
@AndroidEntryPoint
class DownloadYoutubeSongService : Service() {
    @Inject
    lateinit var songRepository: SongRepository

    @Inject
    lateinit var playlistRepository: PlaylistRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val activeDownloads = ConcurrentHashMap.newKeySet<String>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(this, MainActivity.DOWNLOAD_SONG_NOTIFICATION_CHANNEl)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.downloading_songs))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(RUNNING_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val videoId = intent.getStringExtra(VIDEO_ID) ?: return@let
            val userId = intent.getStringExtra(USER_ID) ?: return@let
            val playlistId = intent.getStringExtra(PLAYLIST_ID) ?: ""

            val songName = intent.getStringExtra(SONG_NAME) ?: ""
            val artist = intent.getStringExtra(ARTIST) ?: ""
            val notificationId = videoId.hashCode()

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            when (intent.action) {
                DownloadYoutubeSongsActions.DOWNLOAD_SONG.toString() -> {
                    activeDownloads.add(videoId)

                    val initialBuilder = getNotificationBuilder(songName, artist)
                        .setProgress(0, 0, true)
                    notificationManager.notify(notificationId, initialBuilder.build())

                    serviceScope.launch {
                        try {
                            songRepository.downloadSong(videoId, userId)

                            val retries = 10
                            var attempts = 0
                            var song: Song? = null

                            do {
                                val songResponse = songRepository.findSongByIdOnServer(videoId, userId)
                                if (songResponse == null) {
                                    attempts++
                                    delay(5000)
                                } else {
                                    song = songResponse.song
                                }
                            } while (song == null && attempts < retries)

                            if (song != null) {
                                songRepository.handleSongFromServer(song)
                                songRepository.saveSong(song)

                                if (playlistId.isNotEmpty()) {
                                    playlistRepository.insertSongsIntoPlaylist(playlistId, listOf(song.videoId))
                                }

                                val finishIntent = createActionIntent(videoId, userId, songName, artist, DownloadYoutubeSongsActions.FINISH_SERVICE)
                                val pendingFinishIntent = PendingIntent.getService(
                                    this@DownloadYoutubeSongService, notificationId, finishIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )

                                val successBuilder = getNotificationBuilder(songName, artist)
                                    .setContentText(getString(R.string.download_completed))
                                    .setAutoCancel(true)
                                    .addAction(R.drawable.ic_notification_icon, getString(R.string.lable_close), pendingFinishIntent)

                                notificationManager.notify(notificationId, successBuilder.build())
                            } else {
                                handleFailure(notificationId, videoId, userId, songName, artist, notificationManager)
                            }
                        } catch (e: Exception) {
                            Log.i(TAG, "onStartCommand: ${e.message}")
                            handleFailure(notificationId, videoId, userId, songName, artist, notificationManager)
                        } finally {
                            activeDownloads.remove(videoId)
                            checkAndStopService()
                        }
                    }
                }
                DownloadYoutubeSongsActions.FINISH_SERVICE.toString() -> {
                    notificationManager.cancel(notificationId)
                    activeDownloads.remove(videoId)
                    checkAndStopService()
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun handleFailure(notificationId: Int, videoId: String, userId: String, songName: String, artist: String, notificationManager: NotificationManager) {
        val retryIntent = createActionIntent(videoId, userId, songName, artist, DownloadYoutubeSongsActions.DOWNLOAD_SONG)
        val pendingRetryIntent = PendingIntent.getService(
            this, notificationId + 1, retryIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val failBuilder = getNotificationBuilder(songName, artist)
            .setContentText(getString(R.string.download_has_failed))
            .setAutoCancel(true)
            .addAction(R.drawable.ic_notification_icon, getString(R.string.label_retry), pendingRetryIntent)

        notificationManager.notify(notificationId, failBuilder.build())
    }

    private fun createActionIntent(videoId: String, userId: String, songName: String, artist: String, action: DownloadYoutubeSongsActions): Intent {
        return Intent(this, DownloadYoutubeSongService::class.java).apply {
            putExtra(VIDEO_ID, videoId)
            putExtra(USER_ID, userId)
            putExtra(SONG_NAME, songName)
            putExtra(ARTIST, artist)
            this.action = action.toString()
        }
    }

    private fun getNotificationBuilder(songName: String, artist: String) =
        NotificationCompat.Builder(applicationContext, MainActivity.DOWNLOAD_SONG_NOTIFICATION_CHANNEl)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle("$songName - $artist")
            .setContentText(getString(R.string.download_resquest_has_started))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun checkAndStopService() {
        if (activeDownloads.isEmpty()) {
            val intent = Intent(IntentEnums.INTENT_IMPORT_PLAYLIST_SONGS_FINISHED.toString()).apply {
                setPackage(packageName)
            }

            sendBroadcast(intent)

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    companion object {
        const val RUNNING_NOTIFICATION_ID = 9999
        const val VIDEO_ID: String = "VIDEO_ID"
        const val USER_ID: String = "USER_ID"
        const val PLAYLIST_ID: String = "PLAYLIST_ID"
        const val SONG_NAME: String = "SONG_NAME"
        const val ARTIST: String = "ARTIST"
        const val TAG = "DownloadYoutubeSongService"
    }
}

enum class DownloadYoutubeSongsActions {
    DOWNLOAD_SONG,
    FINISH_SERVICE
}
