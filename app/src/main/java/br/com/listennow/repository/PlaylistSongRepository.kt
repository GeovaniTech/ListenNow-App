package br.com.listennow.repository

import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.database.dao.PlaylistSongDao
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

class PlaylistSongRepository(private val playlistSongDao: PlaylistSongDao) {
    fun save(playlistSong: PlaylistSong) {
        playlistSongDao.save(playlistSong)
    }

    fun getSongsFromPlaylist(playlistId: String): Flow<PlaylistWithSongs> {
        return playlistSongDao.getSongsFromPlaylist(playlistId)
    }
}