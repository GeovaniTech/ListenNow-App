package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import br.com.listennow.model.Playlist


@Dao
interface PlaylistDao {
    @Insert
    suspend fun save(playlist: Playlist)
}