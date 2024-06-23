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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(song: List<Song>)

    @Query("SELECT * FROM Song")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM Song WHERE NOT EXISTS (SELECT 1 FROM Song SongSub JOIN PlaylistSong on SongSub.songId = PlaylistSong.songId WHERE PlaylistSong.playlistId = :playlistId AND Song.songId = SongSub.songId)")
    fun getSongsToAdd(playlistId: String): List<Song>

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM Song WHERE songId = :id")
    fun findById(id: String): Song?

    @Query("SELECT * FROM Song WHERE LOWER(name) LIKE  '%' || LOWER(:text) || '%' OR artist LIKE '%' || LOWER(:text) || '%'")
    fun listByFilters(text: String): List<Song>
}