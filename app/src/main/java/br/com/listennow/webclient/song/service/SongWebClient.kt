package br.com.listennow.webclient.song.service

import android.util.Log
import br.com.listennow.model.Song
import br.com.listennow.webclient.RetrofitInitializer
import br.com.listennow.webclient.song.model.SearchDownloadSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.song.model.SongDownloadRequest
import br.com.listennow.webclient.song.model.SongDownloadResponse
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

    suspend fun getDownloadedSong(songId: String): SongDownloadResponse? {
        return try {
            songService.getSongFile(SongDownloadRequest(songId))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get song $e")
            null
        }
    }

    suspend fun getYTSongs(searchFor: String): List<SearchYTSongResponse>? {
        return try {
            return songService.getYTSongs(SearchYTSongRequest(searchFor))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get songs from YT: $e" )
            null
        }
    }

    suspend fun downloadSong(videoId: String, userId: String) {
        try {
            songService.downloadSong(SearchDownloadSongRequest(videoId, userId))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to start downloading on Server $e")
        }
    }
}