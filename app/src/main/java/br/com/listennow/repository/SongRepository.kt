package br.com.listennow.repository

import android.util.Log
import br.com.listennow.database.dao.SongDao
import br.com.listennow.decorator.AlbumItemDecorator
import br.com.listennow.model.Song
import br.com.listennow.utils.MediaStoreUtil
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.song.model.SongResponse
import br.com.listennow.webclient.song.service.SongWebClient
import java.util.Base64
import javax.inject.Inject

class SongRepository @Inject constructor (
    private val songDao: SongDao,
    private val songWebClient: SongWebClient,
    private val mediaStore: MediaStoreUtil
) {
    suspend fun getAll(): List<Song> {
        return songDao.getSongs()
    }

    suspend fun findSongById(id: String): Song {
        return songDao.findById(id)
    }

    suspend fun findSongByIdOnServer(id: String, userId: String): SongResponse? {
        return songWebClient.findSongById(id, userId)
    }

    suspend fun getAllFiltering(searchFor: String, ignoreIds: List<String> = emptyList()): List<Song> {
        return songDao.listByFilters(searchFor, ignoreIds)
    }

    suspend fun updateAll(userId: String?) {
        userId?.let {
            songWebClient.getAll(it, songDao.getUserSongsIds())?.let { songs ->
                songs.map { song ->
                    handleSongFromServer(song)
                }
                songDao.save(songs.filter { song ->
                    song.path.isNotEmpty()
                })
            }
        }
    }

    suspend fun downloadSong(videoId: String, userId: String) {
        songWebClient.downloadSong(videoId, userId)
    }

    suspend fun getYTSongs(query: String): List<SearchYTSongResponse>? {
        return songWebClient.getYTSongs(query)
    }

    @Throws(Exception::class)
    suspend fun handleSongFromServer(song: Song) {
        val regexNoSpecialCharacters = Regex("[^A-Za-z0-9 ]")

        val songNameWithoutSpecialCharacters = regexNoSpecialCharacters.replace(song.name, "")
        val artistNameWithoutSpecialCharacters = regexNoSpecialCharacters.replace(song.artist, "")

        val fileName = "$songNameWithoutSpecialCharacters - $artistNameWithoutSpecialCharacters.mp3"

        if (!mediaStore.existsSong(fileName)) {
            val response = songWebClient.getDownloadedSong(song.videoId)

            response?.let { songDownload ->
                val result = runCatching {
                    val decodedBytes = Base64.getDecoder().decode(songDownload.file)
                    val uri = mediaStore.writeSong(fileName, decodedBytes)

                    song.path = uri
                }

                if (result.isFailure) {
                    Log.e(
                        TAG,
                        "updateAll: Failed to download song, verify the song name ${result.exceptionOrNull()?.message}}",
                    )
                }
            }
        } else {
            song.path = mediaStore.getSongUri(fileName).toString()
            Log.i(TAG, "handleSongFromServer: Song already exists on device: $fileName")
        }
    }

    /**
     * Returns the ids of the songs from another user that the receiver does not have. Songs that he already has were
     * ignored in the query api.
     */
    suspend fun getIdsSongsFromAnotherUser(userReceiver: String, userWithSongs: String): List<String>? {
        return songWebClient.getSongIdsByUser(userReceiver, userWithSongs)
    }

    /**
     * True if all songs were saved successfully on server
     */
    suspend fun copySongsFromAnotherDevice(userReceiver: String, songs: List<String>): Boolean {
        return songWebClient.copySongsFromAnotherUser(userReceiver, songs)
    }

    /**
     * Return all distinct albums from Songs
     */
    suspend fun getAlbums(query: String?): List<AlbumItemDecorator> {
        if (query.isNullOrEmpty()) {
            return songDao.getAlbums()
        }

        return songDao.getAlbumsFiltering(query)
    }

    /**
     * Return all songs from an Album of an Artist
     */
    suspend fun getSongsFromAlbum(album: String, artist: String, query: String?): List<Song> {
        if (query.isNullOrEmpty()) {
            return songDao.getSongsFromAlbum(album, artist)
        }

        return songDao.getSongsFromAlbumFiltering(album, artist, query)
    }

    /**
     * Save the song on the Database
     */
    suspend fun saveSong(song: Song) {
        songDao.save(song)
    }

    /**
     * Return all songs from an specific playlist
     */
    suspend fun getSongsFromPlaylist(playlistId: String, query: String?): List<Song> {
        if (query.isNullOrEmpty()) {
            return songDao.getSongsFromPlaylist(playlistId)?.songs ?: emptyList()
        }

        return songDao.getSongsFromPlaylist(playlistId)?.songs?.filter { song ->  song.name.lowercase().contains(query.lowercase()) || song.artist.lowercase().contains(query.lowercase()) } ?: emptyList()
    }

    /**
     * Delete a specific song from API and then from the device
     */
    @Throws(Exception::class)
    suspend fun deleteSong(song: Song, userId: String): Boolean {
        if (songWebClient.deleteSongFromUserAccount(song.videoId, userId)) {
            songDao.delete(song)
            return true
        }

        return false
    }

    companion object {
        const val TAG = "SongRepository"
    }
}