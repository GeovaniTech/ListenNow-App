package br.com.listennow.repository

import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.model.Playlist
import br.com.listennow.webclient.playlist.PlaylistWebClient
import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest

class PlaylistRepository(
    val playlistDao: PlaylistDao,
    val playlistWebClient: PlaylistWebClient
) {
    suspend fun create(playlistName: String, clientId: String) {
        val playlistId = playlistWebClient.create(
            PlaylistCreateRequest(
                playlistName = playlistName,
                clientId = clientId
            )
        )

        playlistDao.save(
            Playlist(
                playlistId = playlistId,
                name = playlistName
            )
        )
    }
}