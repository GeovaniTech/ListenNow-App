package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.listennow.decorator.AlbumItemDecorator
import br.com.listennow.model.PlaylistWithSongs
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

    @Query("SELECT * FROM Song WHERE videoId NOT IN (:ignoreIds) AND (LOWER(name) LIKE  '%' || LOWER(:text) || '%' OR artist LIKE '%' || LOWER(:text) || '%')")
    suspend fun listByFilters(text: String, ignoreIds: List<String> = emptyList()): List<Song>

    @Query("SELECT album as name, artist, thumb FROM Song GROUP BY album, artist")
    suspend fun getAlbums(): List<AlbumItemDecorator>

    @Query("SELECT album as name, artist, thumb FROM Song WHERE LOWER(name) LIKE  '%' || LOWER(:query) || '%' OR artist LIKE '%' || LOWER(:query) || '%' GROUP BY album, artist")
    suspend fun getAlbumsFiltering(query: String): List<AlbumItemDecorator>

    @Query("SELECT * FROM Song WHERE album = :album AND artist = :artist")
    suspend fun getSongsFromAlbum(album: String, artist: String): List<Song>

    @Query("SELECT * FROM Song WHERE album = :album AND artist = :artist AND (LOWER(name) LIKE  '%' || LOWER(:query) || '%' OR artist LIKE '%' || LOWER(:query) || '%')")
    suspend fun getSongsFromAlbumFiltering(album: String, artist: String, query: String): List<Song>

    @Query("""
        SELECT 
            * 
        FROM 
            PlaylistSong AS playlistSong 
        INNER JOIN 
            Song as song ON song.videoId = playlistSong.videoId
        WHERE 
            playlistSong.playlistId = :playlistId 
    """)
    suspend fun getSongsFromPlaylist(playlistId: String): PlaylistWithSongs?

    @Query("""
        SELECT 
            * 
        FROM 
            PlaylistSong AS playlistSong 
        INNER JOIN 
            Song as song ON song.videoId = playlistSong.videoId
        WHERE 
            playlistSong.playlistId = :playlistId 
        AND 
            (LOWER(name) LIKE  '%' || LOWER(:query) || '%' OR artist LIKE '%' || LOWER(:query) || '%')
    """)
    suspend fun getSongsFromPlaylistFiltering(playlistId: String, query: String?): PlaylistWithSongs?
}