package br.com.listennow.repository

import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.webclient.playlist.PlaylistWebClient
import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest
import br.com.listennow.webclient.playlist.model.PlaylistInsertSongsRequest

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

    suspend fun insertSongsIntoPlaylist(playlistId: String, songs: List<String>) {
        val songsInsertedOnServer = playlistWebClient.insertSongsIntoPlaylist(
            PlaylistInsertSongsRequest(
                playlistId = playlistId,
                songs = songs
            )
        )

        if (songsInsertedOnServer) {
            playlistDao.addSongsToPlaylist(
                songs.map { videoId ->
                    PlaylistSong(
                        playlistId = playlistId,
                        videoId = videoId
                    )
                }
            )
        }
    }
}