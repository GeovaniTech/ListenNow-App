package br.com.listennow.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentPlaylistBinding
import br.com.listennow.repository.playlist.PlaylistRepository
import br.com.listennow.ui.recyclerview.ListPlaylistsAdapter
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var adapter: ListPlaylistsAdapter

    private val repository by lazy {
        PlaylistRepository(AppDatabase.getInstance(requireContext()).playlistDao())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        adapter = ListPlaylistsAdapter(emptyList(), requireContext())

        configRecyclerPlaylists()
        onNewPlaylistClicked()
        onPlaylistClicked()

        lifecycleScope.launch {
            updatePlaylistsOnScreen()
        }

        return binding.root
    }

    private fun onNewPlaylistClicked() {
        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    private fun onPlaylistClicked() {
        adapter.onItemClick =  {playlist ->
            val bundle = Bundle()
            bundle.putString("playlistId", playlist.playlistId)
            findNavController().navigate(R.id.homePlaylistSongs, bundle)
        }
    }

    private fun configRecyclerPlaylists() {
        val listPlaylists = binding.playlists

        listPlaylists.layoutManager = LinearLayoutManager(requireContext())
        listPlaylists.setHasFixedSize(true)
        listPlaylists.adapter = adapter
    }

    private suspend fun updatePlaylistsOnScreen() {
        repository.getAll().collect {playlists ->
            adapter.update(playlists)
        }
    }
}