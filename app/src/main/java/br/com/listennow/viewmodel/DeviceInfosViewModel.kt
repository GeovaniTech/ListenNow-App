package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.webclient.playlist.model.PlaylistCopyRequest
import br.com.listennow.webclient.playlist.model.PlaylistCountRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceInfosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
) : CommonViewModel(userRepository) {

    private var _userId: MutableLiveData<String> = MutableLiveData()
    val userId: LiveData<String> get() = _userId

    suspend fun loadUserId() {
        _userId.postValue(userRepository.findUser()!!.id)
    }

    suspend fun getSongIdsSongsByUser(userReceiver: String, userWithSongs: String): List<String>? {
        return songRepository.getIdsSongsFromAnotherUser(userReceiver, userWithSongs)
    }

    suspend fun getCountPlaylistsToImport(userWithData: String): Int {
        return playlistRepository.getCountPlaylistsToImport(
            PlaylistCountRequest(
                clientReceiverId = user!!.id,
                clientWithPlaylistsId = userWithData
            )
        )
    }
}