package br.com.listennow.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
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

            val myUri = Uri.parse(song.path)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }

            mediaPlayer.setDataSource(context, myUri)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }

            mediaPlayer.setOnCompletionListener {
                onNextSong?.invoke(getRandomSong())
            }
        } catch (e: Exception) {
            Log.e("SongUtil", "readSong: Failed to read song", )
            onNextSong?.invoke(getRandomSong())
        }
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    private fun clear() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    private fun getRandomSong(): Song {
        val position = (0 until (songs.size)).random()
        return songs[position]
    }

    fun playRandomSong(){
        if(songs.isNotEmpty()) {
            val song = getRandomSong()
            onNextSong?.invoke(song)
        }
    }
}