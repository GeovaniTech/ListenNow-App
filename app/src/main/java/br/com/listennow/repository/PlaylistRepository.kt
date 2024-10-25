package br.com.listennow.repository

import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {
    fun save(playlist: Playlist) {
        playlistDao.save(playlist)
    }

    fun getAll(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists()
    }

    fun findById(id: String): Playlist? {
        return playlistDao.findById(id)
    }
}