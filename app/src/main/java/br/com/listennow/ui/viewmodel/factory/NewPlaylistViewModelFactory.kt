package br.com.listennow.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.listennow.repository.playlist.PlaylistRepository
import br.com.listennow.ui.viewmodel.NewPlaylistViewModel

class NewPlaylistViewModelFactory(private val repository: PlaylistRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewPlaylistViewModel(repository) as T
    }
}