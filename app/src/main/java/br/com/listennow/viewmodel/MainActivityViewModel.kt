package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.listennow.model.Song
import br.com.listennow.repository.UserRepository
import br.com.listennow.utils.SongUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userRepository: UserRepository
) : CommonViewModel(userRepository) {
    private var _actualSong: MutableLiveData<Song?> = MutableLiveData()
    val actualSong: LiveData<Song?> get() = _actualSong

    fun loadActualSong() {
        _actualSong.postValue(SongUtil.actualSong)
    }
}