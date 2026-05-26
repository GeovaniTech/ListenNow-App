package br.com.listennow.webclient.playlist

import br.com.listennow.service.PlaylistService
import br.com.listennow.webclient.playlist.exception.PlaylistRestException
import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest

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

    companion object {
        const val TAG = "PlaylistWebClient"
    }
}