package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class SongDetailsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    userRepository: UserRepository
): CommonViewModel(userRepository) {
    private var _song: MutableLiveData<Song?> = MutableLiveData()
    val song: LiveData<Song?> get() = _song

    lateinit var songId: String

    private var _songDeleted: MutableLiveData<Pair<Song, AtomicBoolean>?> = MutableLiveData()
    val songDeleted: LiveData<Pair<Song, AtomicBoolean>?> get() = _songDeleted

    suspend fun loadSong() {
        _song.value = songRepository.findSongById(songId)
        _song.postValue(_song.value)
    }

    fun  deleteSong(song: Song) = viewModelScope.launch {
        if (songRepository.deleteSong(song, user!!.id)) {
            _songDeleted.postValue(Pair(song, AtomicBoolean(true)))
            return@launch
        }

        _songDeleted.postValue(Pair(song, AtomicBoolean(false)))
    }
}