package br.com.listennow.webclient.song.service

import android.util.Log
import br.com.listennow.model.Song
import br.com.listennow.service.SongService
import br.com.listennow.webclient.enums.StatusMessage
import br.com.listennow.webclient.song.model.DeleteSongRequest
import br.com.listennow.webclient.song.model.SearchDownloadSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongRequest
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.song.model.SongCopyRequest
import br.com.listennow.webclient.song.model.SongDownloadRequest
import br.com.listennow.webclient.song.model.SongDownloadResponse
import br.com.listennow.webclient.song.model.SongIdsRequest
import br.com.listennow.webclient.song.model.SongRequest
import br.com.listennow.webclient.song.model.SongResponse

class SongWebClient(
    private val songService: SongService
) {
    companion object {
        const val TAG = "SongWebClient"
    }

    suspend fun getAll(userId: String, ignoreIds: List<String> = emptyList()): List<Song>? {
        return try {

            Log.i(TAG, "getAll: ${ignoreIds}")
            val songResponse = songService.getAll(SongRequest(userId, ignoreIds))

            songResponse.map {
                it.song
            }
        } catch (e: Exception) {

            Log.e(TAG, "Error trying to get songs from API ${e.message}")
            null
        }
    }

    suspend fun getDownloadedSong(songId: String): SongDownloadResponse? {
        return try {
            songService.getSongFile(SongDownloadRequest(songId, ""))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get song ${e.message}")
            null
        }
    }

    suspend fun getYTSongs(searchFor: String): List<SearchYTSongResponse>? {
        return try {
            return songService.getYTSongs(SearchYTSongRequest(searchFor))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get songs from YT: ${e.message}" )
            null
        }
    }

    suspend fun downloadSong(videoId: String, userId: String) {
        try {
            songService.downloadSong(SearchDownloadSongRequest(videoId, userId))
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to start downloading on Server ${e.message}")
        }
    }

    suspend fun findSongById(videoId: String, userId: String): SongResponse? {
        return songService.findSongById(SongDownloadRequest(videoId, userId))
    }

    suspend fun getSongIdsByUser(userReceiver: String, userWithSongs: String): List<String>? {
        return try {
            val response = songService.getSongIdsByUserId(SongIdsRequest(userReceiver, userWithSongs))
            response.songsIds
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to get qtde songs by user. UserReceiver: $userReceiver UserWithSongs: $userWithSongs, Error: ${e.message}")
            null
        }
    }

    suspend fun copySongsFromAnotherUser(userReceiver: String, songs: List<String>): Boolean {
        return try {
            val response = songService.copySongsFromOtherUser(SongCopyRequest(userReceiver, songs))
            response.message == StatusMessage.SONGS_COPIED_SUCCESSFULLY.message
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to copy songs from another user. UserReceiverId: $userReceiver. Error: ${e.message}")
            false
        }
    }

    suspend fun deleteSongFromUserAccount(videoId: String, userId: String): Boolean {
        try {
            val response = songService.deleteSongFromUserAccount(DeleteSongRequest(videoId, userId))

            return response.message == StatusMessage.SONG_DELETED_FROM_USER_ACCOUNT_SUCCESSFULLY.message
        } catch (e: Exception) {
            Log.e(TAG, "Error trying to delete song from user account on Web Server. Error: ${e.message}")
        }

        return false
    }
}