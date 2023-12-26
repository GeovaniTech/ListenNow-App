package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.listennow.model.Song

@Dao
interface SongDao {
    @Insert
    fun save(song: Song)

    @Query("SELECT * FROM Song")
    fun getSongs(): List<Song>


    @Query("DELETE FROM Song WHERE id = :songId")
    fun delete(songId: Long)
}