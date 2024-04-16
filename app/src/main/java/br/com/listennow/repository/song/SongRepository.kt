package br.com.listennow.repository.song

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.database.dao.SongDao
import br.com.listennow.model.Song
import br.com.listennow.webclient.song.service.SongWebClient
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import java.util.Base64

class SongRepository(private val songDao: SongDao, private val songWebClient: SongWebClient) {
    private val listSongs = MutableLiveData<List<Song>>()

    fun getAll(): LiveData<List<Song>> {
        listSongs.value = songDao.getSongs()
        return listSongs
    }

    fun findSongById(id: String): Song? {
        return songDao.findById(id)
    }

    fun getAllFiltering(searchFor: String): LiveData<List<Song>> {
        listSongs.value = songDao.listByFilters(searchFor)
        return listSongs
    }

    suspend fun updateAll(userId: String?) {
        userId?.let {
            songWebClient.getAll(it)?.let { songs ->
                songs.map {song ->
                    val path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path + "/" + song.name + ".mp3"

                    song.path = path

                    val file = File(path)

                    if(!file.exists()) {
                        val response = songWebClient.getDownloadedSong(song.songId)

                        response?.let { songDownload ->
                            val result = runCatching {
                                val decodedBytes = Base64.getDecoder().decode(songDownload.file)
                                val fileOutputStream = FileOutputStream(path)
                                fileOutputStream.write(decodedBytes)
                                fileOutputStream.close()
                            }

                            if(result.isFailure) {
                                Log.e("SongRepository", "updateAll: Failed to download song, verify the song name", )
                            }
                        }
                    }
                }

                songDao.save(songs)
            }
        }
    }
}