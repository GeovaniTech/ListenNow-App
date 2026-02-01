package br.com.listennow.foreground

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import br.com.listennow.R
import br.com.listennow.fragments.MainActivity
import br.com.listennow.fragments.SongDetailsFragmentDirections
import br.com.listennow.receiver.enums.IntentEnums
import br.com.listennow.repository.SongRepository
import br.com.listennow.utils.SongUtil
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SongPlayerService: Service() {
    @Inject
    lateinit var songRepository: SongRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val stopIntent = Intent().also {
            it.setClass(this, SongPlayerService::class.java)
            it.action = Actions.STOP.toString()
        }

        val playIntent = Intent().also {
            it.setClass(this, SongPlayerService::class.java)
            it.action = Actions.PLAY.toString()
        }

        val nextIntent = Intent().also {
            it.setClass(this, SongPlayerService::class.java)
            it.action = Actions.NEXT.toString()
        }

        val pendingIntentNext = PendingIntent.getService(this, 1, nextIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val pendingIntentStop = PendingIntent.getService(this, 1, stopIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val pendingIntentPlay: PendingIntent = PendingIntent.getService(this, 1, playIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        configureNextSongAutomatically(pendingIntentStop, pendingIntentNext)

        when (intent.action.toString()) {
            Actions.PLAY.toString() -> play(pendingIntentStop, pendingIntentNext)
            Actions.NEXT.toString() -> next(pendingIntentStop, pendingIntentNext)
            Actions.STOP.toString() -> stop(pendingIntentPlay, pendingIntentNext)
            Actions.PLAY_SPECIFIC.toString() -> playSpecific(pendingIntentStop, pendingIntentNext)
        }

        return START_STICKY
    }

    private fun configureNextSongAutomatically(pendingIntentStop: PendingIntent, pendingIntentNext: PendingIntent) {
        SongUtil.onNextSong = { song ->
            SongUtil.readSong(this, song)
            playSpecific(pendingIntentStop, pendingIntentNext)
            callUpdateSongReceiver()
        }
    }

    private fun stop(
        pendingIntentPlay: PendingIntent?,
        pendingIntentNext: PendingIntent?
    ): Int {
        CoroutineScope(Dispatchers.IO).launch {
            SongUtil.pause()

            val futureTarget = Glide.with(applicationContext)
                .asBitmap()
                .load(SongUtil.actualSong!!.thumb)
                .submit()

            val bitmap = withContext(Dispatchers.IO) {
                futureTarget.get()
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                MainActivity.SONG_PLAYER_NOTIFICATION_CHANNEL
            )
                .setContentTitle(SongUtil.actualSong!!.name)
                .setContentText(SongUtil.actualSong!!.artist)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(getSongDetailPendingIntent())
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.play),
                    pendingIntentPlay
                )
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.next),
                    pendingIntentNext
                )
                .setLargeIcon(bitmap)
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            Glide.with(applicationContext).clear(futureTarget)

            startForeground(1, notification)
            callUpdateSongReceiver(isPlaying = false)
        }

        return START_STICKY
    }

    private fun next(
        pendingIntentStop: PendingIntent?,
        pendingIntentNext: PendingIntent?
    ): Int {
        SongUtil.playRandomSong()

        CoroutineScope(Dispatchers.IO).launch {
            val futureTarget = Glide.with(applicationContext)
                .asBitmap()
                .load(SongUtil.actualSong!!.thumb)
                .submit()

            val bitmap = withContext(Dispatchers.IO) {
                futureTarget.get()
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                MainActivity.SONG_PLAYER_NOTIFICATION_CHANNEL
            )
                .setContentTitle(SongUtil.actualSong!!.name)
                .setContentText(SongUtil.actualSong!!.artist)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(getSongDetailPendingIntent())
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.stop),
                    pendingIntentStop
                )
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.next),
                    pendingIntentNext
                )
                .setLargeIcon(bitmap)
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()

            Glide.with(applicationContext).clear(futureTarget)

            startForeground(1, notification)
            callUpdateSongReceiver()
        }
        return START_STICKY
    }

    private fun play(
        pendingIntentStop: PendingIntent?,
        pendingIntentNext: PendingIntent?
    ): Int {
        SongUtil.play()
        return playSpecific(pendingIntentStop, pendingIntentNext)
    }

    private fun playSpecific(
        pendingIntentStop: PendingIntent?,
        pendingIntentNext: PendingIntent?
    ): Int {
        CoroutineScope(Dispatchers.IO).launch {
            val futureTarget = Glide.with(applicationContext)
                .asBitmap()
                .load(SongUtil.actualSong!!.thumb)
                .submit()

            val bitmap = withContext(Dispatchers.IO) {
                futureTarget.get()
            }

            val notification = NotificationCompat.Builder(
                applicationContext,
                MainActivity.SONG_PLAYER_NOTIFICATION_CHANNEL
            )
                .setContentTitle(SongUtil.actualSong!!.name)
                .setContentText(SongUtil.actualSong!!.artist)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentIntent(getSongDetailPendingIntent())
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.stop),
                    pendingIntentStop
                )
                .addAction(
                    R.drawable.ic_notification_icon,
                    getString(R.string.next),
                    pendingIntentNext
                )
                .setLargeIcon(bitmap)
                .build()

            Glide.with(applicationContext).clear(futureTarget)

            startForeground(1, notification)
            callUpdateSongReceiver()
        }
        return START_STICKY
    }

    private fun getSongDetailPendingIntent(): PendingIntent {
        val bundle = Bundle()
        bundle.putString("songId", SongUtil.actualSong!!.videoId)

        return NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.songDetailsFragment)
            .setArguments(bundle)
            .createPendingIntent()
    }

    private fun callUpdateSongReceiver(isPlaying: Boolean = true) {
        val intent = Intent(IntentEnums.INTENT_UPDATE_SONG.toString())
        intent.putExtra(Actions.IS_PLAYING.toString(), isPlaying)
        intent.setPackage(packageName)
        sendBroadcast(intent)
    }
}

enum class Actions {
    PLAY,
    PLAY_SPECIFIC,
    STOP,
    NEXT,
    IS_PLAYING
}