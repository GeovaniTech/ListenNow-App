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
class HomeViewModel @Inject constructor (
    private val songRepository: SongRepository,
    userRepository: UserRepository
) : CommonViewModel(userRepository) {
    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    private var _filteredSongs: MutableLiveData<List<Song>> = MutableLiveData()
    val filteredSongs: LiveData<List<Song>> get() = _filteredSongs

    private var _syncing: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val syncing: LiveData<AtomicBoolean> get() = _syncing

    var songFilter: String? = null

    private var _songDeleted: MutableLiveData<Pair<Song, AtomicBoolean>?> = MutableLiveData()
    val songDeleted: LiveData<Pair<Song, AtomicBoolean>?> get() = _songDeleted

    suspend fun loadSongs() {
        _songs.postValue(songRepository.getAll())
    }

    suspend fun loadSongsFiltering(filter: String) {
        _filteredSongs.postValue(songRepository.getAllFiltering(filter))
    }

    suspend fun syncSongs() {
        _syncing.postValue(AtomicBoolean(true))
        songRepository.updateAll(user?.id)
        _syncing.postValue(AtomicBoolean(false))
    }

    fun deleteSong(song: Song) = viewModelScope.launch {
        if (songRepository.deleteSong(song, user!!.id)) {
            _songDeleted.postValue(Pair(song, AtomicBoolean(true)))
            return@launch
        }

        _songDeleted.postValue(Pair(song, AtomicBoolean(false)))
    }

    fun updateSongDeletedCallback(value: Pair<Song, AtomicBoolean>? = null) {
        _songDeleted.postValue(value)
    }
}
