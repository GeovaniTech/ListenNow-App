package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.listennow.model.Song
import br.com.listennow.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val songRepository: SongRepository
) : ViewModel() {
    val actualSong = MutableLiveData<Song>()

    fun getSongs(): LiveData<List<Song>> {
        return songRepository.getAll()
    }

    fun getSongsFiltering(filter: String): List<Song> {
        return songRepository.getAllFiltering(filter)
    }

    suspend fun updateAll(userId: String) {
        songRepository.updateAll(userId)
    }
}
