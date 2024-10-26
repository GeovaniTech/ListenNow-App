package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val songRepository: SongRepository
) : ViewModel() {
    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    private var _filteredSongs: MutableLiveData<List<Song>> = MutableLiveData()
    val filteredSongs: LiveData<List<Song>> get() = _filteredSongs

    private var _actualSong: MutableLiveData<Song?> = MutableLiveData()
    val actualSong: LiveData<Song?> get() = _actualSong

    private var _syncing: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val syncing: LiveData<AtomicBoolean> get() = _syncing

    suspend fun loadSongs() {
        _songs.postValue(songRepository.getAll())
    }

    fun loadActualSong() {
        _actualSong.postValue(_actualSong.value)
    }

    suspend fun loadSongsFiltering(filter: String) {
        _filteredSongs.postValue(songRepository.getAllFiltering(filter))
    }

    suspend fun updateAll(userId: String) {
        songRepository.updateAll(userId)
    }

    fun updateActualSong(song: Song) {
        _actualSong.postValue(song)
    }

    suspend fun syncSongs(userId: String) {
        _syncing.postValue(AtomicBoolean(true))
        songRepository.updateAll(userId)
        loadSongs()
        _syncing.postValue(AtomicBoolean(false))
    }
}
