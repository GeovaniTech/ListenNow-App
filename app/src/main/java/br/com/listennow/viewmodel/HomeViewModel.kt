package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.utils.SongUtil
import dagger.hilt.android.lifecycle.HiltViewModel
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
}
