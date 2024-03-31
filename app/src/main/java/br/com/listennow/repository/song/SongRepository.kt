package br.com.listennow.repository.song

import br.com.listennow.database.dao.SongDao
import br.com.listennow.model.Song
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.flow.Flow

class SongRepository(private val songDao: SongDao, private val songWebClient: SongWebClient) {
    suspend fun getAll(): Flow<List<Song>> {
        return songDao.getSongs()
    }

    suspend fun updateAll(userId: String?) {
        userId?.let {
            songWebClient.getAll(it)?.let { songs ->
                songDao.save(songs)
            }
        }
    }
}