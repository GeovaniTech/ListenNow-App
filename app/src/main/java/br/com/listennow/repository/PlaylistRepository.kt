package br.com.listennow.repository

import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.decorator.PlaylistItemDecorator
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.webclient.playlist.PlaylistWebClient
import br.com.listennow.webclient.playlist.model.PlaylistCopyRequest
import br.com.listennow.webclient.playlist.model.PlaylistCountRequest
import br.com.listennow.webclient.playlist.model.PlaylistCreateRequest
import br.com.listennow.webclient.playlist.model.PlaylistDeleteRequest
import br.com.listennow.webclient.playlist.model.PlaylistDeleteSongsRequest
import br.com.listennow.webclient.playlist.model.PlaylistGetRequest
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

    suspend fun deleteSongsFromPlaylist(playlistId: String, songs: List<String>) {
        val songsDeleteFromServer = playlistWebClient.deleteSongsFromPlaylist(
            PlaylistDeleteSongsRequest(
                playlistId = playlistId,
                songs = songs
            )
        )

        if (songsDeleteFromServer) {
            songs.forEach { videoId ->
                playlistDao.deleteSongFromPlaylist(videoId, playlistId)
            }
        }
    }

    suspend fun delete(playlistId: String, userId: String) {
        val playlistDeletedOnServer = playlistWebClient.delete(
            PlaylistDeleteRequest(
                playlistId = playlistId,
                clientId = userId
            )
        )

        if (playlistDeletedOnServer) {
            playlistDao.delete(playlistId)
        }
    }

    suspend fun getLocalPlaylistItemDecorator(query: String): List<PlaylistItemDecorator> {
        return playlistDao.getPlaylists(query)
    }

    suspend fun syncUserPlaylists(clientId: String) {
        val playlistsIgnore = playlistDao.getPlaylists("").map { it.playlistId }

        val userPlaylistsOnServer = playlistWebClient.getUserPlaylists(
            PlaylistGetRequest(
                clientId = clientId,
                ignoreIds = playlistsIgnore,
            )
        )

        val playlists = userPlaylistsOnServer.map { serverPlaylist ->
            Playlist(
                playlistId = serverPlaylist.id,
                name = serverPlaylist.name
            )
        }

        playlistDao.save(playlists)

        playlists.forEach { playlist ->
            val songs = userPlaylistsOnServer.first { p -> p.id == playlist.playlistId }.songs

            playlistDao.addSongsToPlaylist(
                songs.map { videoId ->
                    PlaylistSong(
                        playlistId = playlist.playlistId,
                        videoId = videoId
                    )
                }
            )
        }
    }

    suspend fun copyPlaylistsFromAnotherDevice(clientReceiverId: String, clientCopyFromId: String) {
        val playlistsCopiedSuccessfully = playlistWebClient.copyPlaylistFromAnotherDevice(
            copyRequest = PlaylistCopyRequest(
                clientReceiverId = clientReceiverId,
                clientWithPlaylistsId = clientCopyFromId
            )
        )

        if (playlistsCopiedSuccessfully) {
            syncUserPlaylists(
                clientId = clientReceiverId
            )
        }
    }

    suspend fun getCountPlaylistsToImport(countRequest: PlaylistCountRequest): Int {
        return playlistWebClient.countPlaylistsToImport(countRequest)
    }
}