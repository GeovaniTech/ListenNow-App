package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.listennow.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    // OnConflict, if the Id exists on database, room will do a update on product automatically
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(song: Song)

    @Query("SELECT * FROM Song")
    fun getSongs(): Flow<List<Song>>

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM Song WHERE id = :id")
    fun findById(id: Long): Song?

    @Query("SELECT * FROM Song WHERE LOWER(name) LIKE  '%' || LOWER(:text) || '%' OR artist LIKE '%' || LOWER(:text) || '%'")
    fun listByFilters(text: String): Flow<List<Song>>
}