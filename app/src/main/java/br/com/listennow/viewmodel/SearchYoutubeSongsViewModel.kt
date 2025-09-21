package br.com.listennow.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchYoutubeSongsViewModel @Inject constructor (
    private val songRepository: SongRepository,
    userRepository: UserRepository
): CommonViewModel(userRepository) {
    private var _songs: MutableLiveData<List<SearchYTSongResponse>?> = MutableLiveData()
    val songs: LiveData<List<SearchYTSongResponse>?> get() = _songs

    suspend fun downloadSong(songId: String) {
        songRepository.downloadSong(songId, user?.id!!)
    }

    suspend fun loadYoutubeSongs(filter: String) {
        _songs.postValue(songRepository.getYTSongs(filter))
    }

    suspend fun songSynchronizedSuccessfully(videoId: String): Boolean {
        try {
            val songResponse = songRepository.findSongByIdOnServer(videoId, user?.id!!)

            songResponse?.let {
                val song = songResponse.song
                songRepository.handleSongFromServer(song)
                songRepository.saveSong(song)
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "songSynchronizedSuccessfully: Error trying to sync song after immediately download ${e.stackTrace}", )
        }

        return false
    }

    companion object {
        const val TAG = "SearchYoutubeSongsViewModel"
    }
}