package br.com.listennow.webclient.user.service

import android.util.Log
import br.com.listennow.model.Song
import br.com.listennow.webclient.RetrofitInitializer
import br.com.listennow.webclient.song.model.SongRequest

class SongWebClient {
    companion object {
        const val TAG = "SongWebClient"
    }
    private val songService by lazy {
        RetrofitInitializer().songService
    }

    suspend fun getAll(userId: String): List<Song>? {
        return try {
            val songResponse = songService.getAll(SongRequest(userId))

            songResponse.map {
                it.song
            }
        } catch (e: Exception) {

            Log.e(TAG, "Error trying to get songs from API $e")
            null
        }
    }
}