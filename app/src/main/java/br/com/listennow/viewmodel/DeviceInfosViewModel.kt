package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceInfosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository
) : CommonViewModel(userRepository) {

    private var _userId: MutableLiveData<String> = MutableLiveData()
    val userId: LiveData<String> get() = _userId

    suspend fun loadUserId() {
        _userId.postValue(userRepository.findUser()!!.id)
    }

    suspend fun getSongIdsSongsByUser(userReceiver: String, userWithSongs: String): List<String>? {
        return songRepository.getIdsSongsFromAnotherUser(userReceiver, userWithSongs)
    }
}