package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.decorator.PlaylistItemDecorator
import br.com.listennow.enums.EnumPlaylistActionStatus
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val playlistDao: PlaylistDao
): CommonViewModel(userRepository) {
    private var _playlists: MutableLiveData<List<PlaylistItemDecorator>> = MutableLiveData()
    val playlists: LiveData<List<PlaylistItemDecorator>> get() = _playlists

    private var _statusCallback: MutableLiveData<EnumPlaylistActionStatus> = MutableLiveData()
    val statusCallback: LiveData<EnumPlaylistActionStatus> get() = _statusCallback

    fun loadData() = viewModelScope.launch {
        _playlists.postValue(playlistDao.getPlaylists())
    }

    fun deletePlaylist(playlistId: String) = viewModelScope.launch {
        playlistDao.delete(playlistId)
        _statusCallback.postValue(EnumPlaylistActionStatus.PLAYLIST_DELETED)
    }
}