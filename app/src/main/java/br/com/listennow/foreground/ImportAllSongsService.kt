package br.com.listennow.foreground

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.listennow.R
import br.com.listennow.fragments.MainActivity
import br.com.listennow.repository.SongRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Used to import songs from another device
 */
@AndroidEntryPoint
class ImportAllSongsService: Service() {
    @Inject
    lateinit var songRepository: SongRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val userReceiver = intent.getStringExtra(ImportAllSongsData.USER_RECEIVER.value)
        val songsIds = intent.getStringArrayListExtra(ImportAllSongsData.SONGS_IDS.value)!!

        val title = getString(R.string.importing_songs)
        val description = getString(R.string.importing_all_songs_from_another_device, songsIds.size)

        val notification = notificationBuilder(title, description).run {
            setProgress(0, 0, true)
            build()
        }

        startForeground(1, notification)

        CoroutineScope(Dispatchers.IO).launch {
            songsIds.windowed(20, 20, partialWindows = true).forEachIndexed{ index, chunk ->
                if (songRepository.copySongsFromAnotherDevice(userReceiver!!, chunk)) {
                    songRepository.updateAll(userReceiver)
                }
            }

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_STICKY
    }

    private fun notificationBuilder(
        title: String,
        description: String
    ) =
        NotificationCompat.Builder(
            this,
            MainActivity.IMPORT_ALL_SONGS_FOREGROUND_SERVICE_NOTIFICATION_CHANNEl
        )
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_notification_icon)

    enum class ImportAllSongsData(val value: String) {
        USER_RECEIVER("USER_RECEIVER"),
        SONGS_IDS("SONGS_IDS")
    }
}