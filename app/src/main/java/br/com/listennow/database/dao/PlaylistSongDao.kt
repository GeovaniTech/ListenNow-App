package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playlistSong: PlaylistSong)

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlistId = :playlistId")
    fun getSongsFromPlaylist(playlistId: String): Flow<PlaylistWithSongs>
}