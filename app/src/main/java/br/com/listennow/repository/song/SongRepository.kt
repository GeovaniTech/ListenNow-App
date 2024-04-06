package br.com.listennow.repository.song

import android.os.Environment
import br.com.listennow.database.dao.SongDao
import br.com.listennow.model.Song
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.flow.Flow
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

                    val base64String = song.file
                    val decodedBytes = Base64.getDecoder().decode(base64String)

                    val fileOutputStream = FileOutputStream(path)
                    fileOutputStream.write(decodedBytes)
                    fileOutputStream.close()

                    song.file = ""

                    songDao.save(songs)
                }
            }
        }
    }
}