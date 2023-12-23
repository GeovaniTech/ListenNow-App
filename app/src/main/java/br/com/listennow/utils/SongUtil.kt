package br.com.listennow.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import br.com.listennow.model.Song

class SongUtil {
    companion object {
        private var mediaPlayer: MediaPlayer = MediaPlayer()
        var songs = emptyList<Song>()
        var index = 0
        private const val shuffle = true
        lateinit var actualSong: Song

        fun readSong(context: Context, song : Song) {
            if(isPlaying()) {
                clear()
            }

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
                clear()

                if (shuffle) {
                    index = (0 until (songs.size)).random()
                } else {
                    index++
                }

                if (index > songs.size) {
                    index = 0
                }

                readSong(context, songs[index])
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
    }
}