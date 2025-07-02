package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.decorator.PlaylistDecorator
import br.com.listennow.enums.EnumPlaylistActionStatus
import br.com.listennow.model.Playlist
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewPlaylistViewModel @Inject constructor(
    userRepository: UserRepository,
    private val playlistDao: PlaylistDao
): CommonViewModel(userRepository) {
    val playlist: PlaylistDecorator = PlaylistDecorator()

    private var _statusCallback: MutableLiveData<EnumPlaylistActionStatus> = MutableLiveData()
    val statusCallback: LiveData<EnumPlaylistActionStatus> get() = _statusCallback

    fun createPlaylist() {
        if (playlist.title.isNullOrEmpty()) {
            _statusCallback.postValue(EnumPlaylistActionStatus.ERROR_TITLE_IS_REQUIRED)
            return
        }

        viewModelScope.launch {
            playlistDao.save(Playlist(
                playlistId = UUID.randomUUID().toString(),
                name = playlist.title!!
            ))

            _statusCallback.postValue(EnumPlaylistActionStatus.PLAYLIST_SAVED_SUCCESSFULLY)
        }
    }
}