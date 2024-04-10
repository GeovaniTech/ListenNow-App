package br.com.listennow.ui.viewmodel

import androidx.lifecycle.ViewModel
import br.com.listennow.model.Playlist
import br.com.listennow.repository.playlist.PlaylistRepository

class NewPlaylistViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {
    fun save(playlist: Playlist) {
        playlistRepository.save(playlist)
    }
}