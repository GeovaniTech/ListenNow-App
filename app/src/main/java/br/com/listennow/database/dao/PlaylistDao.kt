package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.listennow.decorator.PlaylistItemDecorator
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong


@Dao
interface PlaylistDao {
    @Insert
    suspend fun save(playlist: Playlist)

    @Query("DELETE FROM Playlist WHERE playlistId = :playlistId")
    suspend fun delete(playlistId: String)

    @Query("""
        SELECT  playlist.name as title,
                playlist.playlistId,
                (
                    SELECT COUNT(song.videoId) FROM PlaylistSong as pl
                    INNER JOIN Song as song ON song.videoId = pl.videoId
                    WHERE playlistId = playlist.playlistId
                ) as totalSongs,
                (
                    SELECT group_concat(song.artist, ', ') FROM PlaylistSong as pl
                    INNER JOIN Song as song ON song.videoId = pl.videoId
                    WHERE playlistId = playlist.playlistId
                ) as artists
        FROM Playlist as playlist
    """)
    suspend fun getPlaylists(): List<PlaylistItemDecorator>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongsToPlaylist(songs: List<PlaylistSong>)
}