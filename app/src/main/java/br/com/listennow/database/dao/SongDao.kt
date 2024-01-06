package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.listennow.model.Song

@Dao
interface SongDao {
    @Insert
    fun save(song: Song)

    @Query("SELECT * FROM Song")
    fun getSongs(): List<Song>

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM Song WHERE id = :id")
    fun findById(id: Long): Song?
}