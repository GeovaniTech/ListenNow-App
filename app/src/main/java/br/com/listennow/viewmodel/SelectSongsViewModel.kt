package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.Selection
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jspecify.annotations.NonNull
import javax.inject.Inject

@HiltViewModel
class SelectSongsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val songRepository: SongRepository
): CommonViewModel(userRepository) {
    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    var query: String = ""

    fun loadData() = viewModelScope.launch {
        _songs.postValue(songRepository.getAllFiltering(query))
    }
}