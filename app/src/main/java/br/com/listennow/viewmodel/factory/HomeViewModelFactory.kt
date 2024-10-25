package br.com.listennow.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.listennow.repository.SongRepository
import br.com.listennow.viewmodel.HomeViewModel

class HomeViewModelFactory(private val repository: SongRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}