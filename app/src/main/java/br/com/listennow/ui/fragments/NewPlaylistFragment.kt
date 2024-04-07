package br.com.listennow.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentNewPlaylistBinding
import br.com.listennow.databinding.FragmentPlaylistBinding
import br.com.listennow.model.Playlist
import br.com.listennow.repository.playlist.PlaylistRepository
import kotlinx.coroutines.launch
import java.util.UUID

class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding

    private val repository by lazy {
        PlaylistRepository(AppDatabase.getInstance(requireContext()).playlistDao())
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
                repository.save(Playlist(UUID.randomUUID().toString(), binding.inputPlaylistName.text.toString()))
                findNavController().navigate(R.id.playlistFragment)
            }
        }
    }
}