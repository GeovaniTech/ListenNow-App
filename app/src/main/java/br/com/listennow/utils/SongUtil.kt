package br.com.listennow.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import br.com.listennow.model.Song
import br.com.listennow.view.HomeActivity

class SongUtil {
    companion object {
        private var mediaPlayer: MediaPlayer = MediaPlayer()
        var songs = emptyList<Song>()
        lateinit var actualSong: Song
        var onNextSong: ((Song) -> Unit)? = null

        fun readSong(context: Context, song : Song) {
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
    }
}