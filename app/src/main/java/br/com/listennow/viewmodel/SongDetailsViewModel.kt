package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongDetailsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    userRepository: UserRepository
): CommonViewModel(userRepository) {
    private var _song: MutableLiveData<Song?> = MutableLiveData()
    val song: LiveData<Song?> get() = _song

    lateinit var songId: String

    suspend fun loadSong() {
        _song.postValue(songRepository.findSongById(songId))
    }
}