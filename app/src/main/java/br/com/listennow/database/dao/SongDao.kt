package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.listennow.decorator.AlbumItemDecorator
import br.com.listennow.model.Song

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(song: Song)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(song: List<Song>)

    @Query("SELECT * FROM Song")
    suspend fun getSongs(): List<Song>

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * FROM Song WHERE videoId = :id")
    suspend fun findById(id: String): Song

    @Query("SELECT * FROM Song WHERE LOWER(name) LIKE  '%' || LOWER(:text) || '%' OR artist LIKE '%' || LOWER(:text) || '%'")
    suspend fun listByFilters(text: String): List<Song>

    @Query(" SELECT album as name, artist, thumb FROM Song GROUP BY album, artist")
    suspend fun getAlbums(): List<AlbumItemDecorator>

    @Query(" SELECT album as name, artist, thumb FROM Song WHERE LOWER(name) LIKE  '%' || LOWER(:query) || '%' OR artist LIKE '%' || LOWER(:query) || '%' GROUP BY album, artist")
    suspend fun getAlbumsFiltering(query: String): List<AlbumItemDecorator>
}