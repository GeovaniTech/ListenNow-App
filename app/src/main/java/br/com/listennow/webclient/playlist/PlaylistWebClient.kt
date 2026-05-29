package br.com.listennow.webclient.playlist

import br.com.listennow.service.PlaylistService
import br.com.listennow.webclient.playlist.exception.PlaylistRestException
import br.com.listennow.webclient.playlist.model.PlaylistCopyRequest
import br.com.listennow.webclient.playlist.model.PlaylistCountRequest
import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest
import br.com.listennow.webclient.playlist.model.PlaylistDeleteRequest
import br.com.listennow.webclient.playlist.model.PlaylistDeleteSongsRequest
import br.com.listennow.webclient.playlist.model.PlaylistGetRequest
import br.com.listennow.webclient.playlist.model.PlaylistGetResponse
import br.com.listennow.webclient.playlist.model.PlaylistInsertSongsRequest

class PlaylistWebClient(
    private val playlistService: PlaylistService
) {
    @Throws(PlaylistRestException::class, Exception::class)
    suspend fun create(playlistCreateRequest: PlaylistCreateRequest): String {
        val createResponse = playlistService.create(playlistCreateRequest)

        if (createResponse.isSuccessful) {
            return createResponse.body()!!.playlistId
        }

        throw PlaylistRestException("Error trying to create playlist on Server: ${createResponse.message()}")
    }

    suspend fun insertSongsIntoPlaylist(playlistInsertSongsRequest: PlaylistInsertSongsRequest): Boolean {
        val insertSongsResponse = playlistService.insertSongsIntoPlaylist(playlistInsertSongsRequest)

        return insertSongsResponse.isSuccessful
    }

    suspend fun deleteSongsFromPlaylist(playlistDeleteSongsRequest: PlaylistDeleteSongsRequest): Boolean {
        val deleteSongsResponse = playlistService.deleteSongsFromPlaylist(playlistDeleteSongsRequest)

        return deleteSongsResponse.isSuccessful
    }

    suspend fun delete(playlistDeleteRequest: PlaylistDeleteRequest): Boolean {
        val deleteResponse = playlistService.delete(playlistDeleteRequest)

        return deleteResponse.isSuccessful
    }

    suspend fun getUserPlaylists(getUserPlaylistGetRequest: PlaylistGetRequest): List<PlaylistGetResponse> {
        val playlistsResponse = playlistService.getPlaylistsFromUser(getUserPlaylistGetRequest)

        if (playlistsResponse.isSuccessful) {
            return playlistsResponse.body()!!
        }

        throw PlaylistRestException("Error trying to get all playlists from user.")
    }

    suspend fun copyPlaylistFromAnotherDevice(copyRequest: PlaylistCopyRequest): Boolean {
        return playlistService.copyPlaylistsFromAnotherDevice(copyRequest).isSuccessful
    }

    suspend fun countPlaylistsToImport(countRequest: PlaylistCountRequest): Int {
        val countResponse = playlistService.getCountPlaylistsToImport(countRequest)

        if (countResponse.isSuccessful) {
            return countResponse.body()!!.count
        }

        throw PlaylistRestException("Error trying to count playlists to import")
    }
}