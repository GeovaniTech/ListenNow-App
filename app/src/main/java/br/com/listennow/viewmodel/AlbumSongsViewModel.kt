package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.model.Song
import br.com.listennow.navparams.AlbumSongsNavParams
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumSongsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val songRepository: SongRepository
) : CommonViewModel(userRepository){
    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    lateinit var navParams: AlbumSongsNavParams
    var searchFilter: String? = null

    fun loadData() {
        viewModelScope.launch {
            _songs.postValue(songRepository.getSongsFromAlbum(navParams.album, navParams.artist, searchFilter))
        }
    }
}