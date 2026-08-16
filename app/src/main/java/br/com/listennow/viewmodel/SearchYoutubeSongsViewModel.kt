package br.com.listennow.viewmodel

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

    suspend fun loadYoutubeSongs(filter: String) {
        _songs.postValue(songRepository.getYTSongs(filter))
    }

    companion object {
        const val YOUTUBE_BASE_URL = "https://youtube.com/watch?v="
    }
}