package br.com.listennow.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.listennow.repository.song.SongRepository
import br.com.listennow.ui.viewmodel.HomeViewModel

class HomeViewModelFactory(private val repository: SongRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}