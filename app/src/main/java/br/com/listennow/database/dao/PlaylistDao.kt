package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playlists: List<Playlist>)

    @Query("SELECT * FROM Playlist")
    fun getPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getPlaylistWithSongs(playlistId: String): List<PlaylistWithSongs>

    @Delete
    fun delete(playlist: Playlist)

    @Query("SELECT * FROM Playlist WHERE playlistId = :id")
    fun findById(id: String): Playlist?
}