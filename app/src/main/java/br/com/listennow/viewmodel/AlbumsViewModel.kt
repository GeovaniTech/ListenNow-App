package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.decorator.AlbumItemDecorator
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val songRepository: SongRepository
) : CommonViewModel(userRepository){

    private var _albums: MutableLiveData<List<AlbumItemDecorator>> = MutableLiveData()
    val albums: LiveData<List<AlbumItemDecorator>> get() = _albums

    var searchFilter: String? = null

    fun loadData() {
        viewModelScope.launch {
            _albums.postValue(songRepository.getAlbums(searchFilter))
        }
    }
}