package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.BuildConfig
import br.com.listennow.model.Song
import br.com.listennow.repository.UserRepository
import br.com.listennow.utils.SongUtil
import br.com.listennow.webclient.appversion.model.LastVersionAvailableAppResponse
import br.com.listennow.webclient.appversion.service.AppVersionWebClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appVersionWebClient: AppVersionWebClient
) : CommonViewModel(userRepository) {
    private var _actualSong: MutableLiveData<Song?> = MutableLiveData()
    val actualSong: LiveData<Song?> get() = _actualSong

    private var _newVersionAvailable: MutableLiveData<Pair<AtomicBoolean, LastVersionAvailableAppResponse?>> = MutableLiveData()
    val newVersionAvailable: LiveData<Pair<AtomicBoolean, LastVersionAvailableAppResponse?>> = _newVersionAvailable

    fun loadActualSong() {
        _actualSong.postValue(SongUtil.actualSong)
    }

    fun checkAppUpdate() = viewModelScope.launch {
        val lastVersion = appVersionWebClient.getLatestVersion()

        lastVersion?.let {

            if (BuildConfig.VERSION_CODE < lastVersion.code) {
                _newVersionAvailable.postValue(Pair(AtomicBoolean(true), lastVersion))
            }
        }
    }
}