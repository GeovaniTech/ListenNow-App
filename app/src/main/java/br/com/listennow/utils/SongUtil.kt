package br.com.listennow.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import br.com.listennow.model.Song

object SongUtil {
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    var songs = emptyList<Song>()
    var actualSong: Song? = null
    var onNextSong: ((Song) -> Unit)? = null

    fun readSong(context: Context, song : Song) {
        try {
            clear()

            actualSong = song

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }

            mediaPlayer.setDataSource(context, song.path.toUri())

            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }

            mediaPlayer.setOnCompletionListener {
                onNextSong?.invoke(getRandomSong())
            }
        } catch (e: Exception) {
            Log.e("SongUtil", "readSong: Failed to read song ${e.message}")
            onNextSong?.invoke(getRandomSong())
        }
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun clear() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    private fun getRandomSong(): Song {
        lateinit var song: Song

        do {
            val position = (0 until (songs.size)).random()
            song = songs[position]
        } while (song == actualSong && songs.size > 1)

        return song
    }

    fun playRandomSong(){
        if(songs.isNotEmpty()) {
            val song = getRandomSong()
            onNextSong?.invoke(song)
        }
    }
}