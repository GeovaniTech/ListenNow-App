package br.com.listennow.repository.song

import android.os.Environment
import android.util.Log
import br.com.listennow.database.dao.SongDao
import br.com.listennow.model.Song
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.flow.Flow
import okhttp3.internal.notify
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Base64

class SongRepository(private val songDao: SongDao, private val songWebClient: SongWebClient) {
    suspend fun getAll(): Flow<List<Song>> {
        return songDao.getSongs()
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
                        val response = songWebClient.getDownloadedSong(song.id)

                        response?.let { songDownload ->
                            val decodedBytes = Base64.getDecoder().decode(songDownload.file)
                            val fileOutputStream = FileOutputStream(path)
                            fileOutputStream.write(decodedBytes)
                            fileOutputStream.close()
                        }
                    }
                }

                songDao.save(songs)
            }
        }
    }
}