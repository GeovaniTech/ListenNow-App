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
                    SELECT group_concat(artist, ', ') FROM (
                        SELECT DISTINCT song.artist as artist FROM PlaylistSong as pl
                        INNER JOIN Song as song ON song.videoId = pl.videoId
                        WHERE playlistId = playlist.playlistId
                        GROUP BY song.artist
                    )
                ) as artists
        FROM Playlist as playlist
        WHERE LOWER(playlist.name) LIKE  '%' || LOWER(:query) || '%'
    """)
    suspend fun getPlaylists(query: String): List<PlaylistItemDecorator>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongsToPlaylist(songs: List<PlaylistSong>)

    @Query("DELETE FROM PlaylistSong where playlistId = :playlistId AND videoId = :videoId")
    suspend fun deleteSongFromPlaylist(videoId: String, playlistId: String)
}