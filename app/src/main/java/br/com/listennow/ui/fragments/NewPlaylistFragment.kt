package br.com.listennow.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentNewPlaylistBinding
import br.com.listennow.databinding.FragmentPlaylistBinding
import br.com.listennow.model.Playlist
import br.com.listennow.repository.playlist.PlaylistRepository
import br.com.listennow.ui.viewmodel.NewPlaylistViewModel
import br.com.listennow.ui.viewmodel.factory.NewPlaylistViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID

class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding

    // Factory is created because we need to pass the repository, but the default constructor is clean,
    // without any other classes, that's why we need a Factory, all of this because in viewModel class we cannot use context.
    private val viewModel: NewPlaylistViewModel by viewModels {
        NewPlaylistViewModelFactory(PlaylistRepository(AppDatabase.getInstance(requireContext()).playlistDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        onSaveNewPlaylist()

        return binding.root
    }

    private fun onSaveNewPlaylist() {
        binding.savePlaylist.setOnClickListener {
            lifecycleScope.launch {
                viewModel.save(Playlist(UUID.randomUUID().toString(), binding.inputPlaylistName.text.toString()))
                findNavController().navigate(R.id.playlistFragment)
            }
        }
    }
}