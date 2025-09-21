package br.com.listennow.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.model.Song
import br.com.listennow.navparams.PlaylistSongsNavParams
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistDao: PlaylistDao
): CommonViewModel(userRepository) {
    lateinit var navParams: PlaylistSongsNavParams
    var query: String? = null

    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    private var _onSongsAddedCallback: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val onSongsAddedCallback: LiveData<AtomicBoolean> get() = _onSongsAddedCallback

    fun loadData() = viewModelScope.launch {
        _songs.postValue(songRepository.getSongsFromPlaylist(navParams.playlistId, query))
    }

    fun addSongsToPlaylist(songsIds: ArrayList<String>?) = viewModelScope.launch {
        songsIds?.let {
            val playlistSongs = songsIds.map { PlaylistSong(navParams.playlistId, it) }
            playlistDao.addSongsToPlaylist(playlistSongs)
            postSongsAddedCallback(true)
        }
    }

    fun postSongsAddedCallback(value: Boolean) {
        _onSongsAddedCallback.postValue(AtomicBoolean(value))
    }
}