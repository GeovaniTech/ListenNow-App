package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.listennow.repository.UserRepository
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.song.service.SongWebClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchYoutubeSongsViewModel @Inject constructor (
    private val songWebClient: SongWebClient,
    userRepository: UserRepository
): CommonViewModel(userRepository) {

    private var _songs: MutableLiveData<List<SearchYTSongResponse>?> = MutableLiveData()
    val songs: LiveData<List<SearchYTSongResponse>?> get() = _songs

    suspend fun downloadSong(songId: String) {
        songWebClient.downloadSong(songId, user.id)
    }

    suspend fun loadYoutubeSongs(filter: String) {
        _songs.postValue(songWebClient.getYTSongs(filter))
    }
}