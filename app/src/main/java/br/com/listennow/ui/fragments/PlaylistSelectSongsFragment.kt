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
import br.com.listennow.databinding.FragmentPlaylistSongsBinding
import br.com.listennow.model.PlaylistSong
import br.com.listennow.repository.playlistsong.PlaylistSongRepository
import br.com.listennow.repository.song.SongRepository
import br.com.listennow.to.TOSong
import br.com.listennow.ui.recyclerview.ListSelectSongsToPlaylistAdapter
import br.com.listennow.webclient.song.service.SongWebClient
import kotlinx.coroutines.launch

class PlaylistSelectSongsFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistSongsBinding
    private lateinit var adapter: ListSelectSongsToPlaylistAdapter
    private lateinit var playlistId: String

    private val songsRepository by lazy {
        SongRepository(AppDatabase.getInstance(requireContext()).songDao(), SongWebClient())
    }

    private val playlistSongRepository by lazy {
        PlaylistSongRepository(AppDatabase.getInstance(requireContext()).playlistSongDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistSongsBinding.inflate(inflater, container, false)
        adapter = ListSelectSongsToPlaylistAdapter(emptyList(), requireContext())

        arguments?.getString("playlistId").let { playlist ->
            playlist?.let {
                playlistId = playlist
            }
        }

        configSelectSongsRecyclerView()
        onSaveSongsClicked()
        updateSongsOnScreen()
        return binding.root
    }

    private fun configSelectSongsRecyclerView() {
        val listSongs = binding.songs

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }
    private fun onSaveSongsClicked() {
        binding.saveSelectedSongs.setOnClickListener {
            lifecycleScope.launch {
                adapter.getSelectedSongs().forEach {song ->
                    val playlistSong = PlaylistSong(song.id, playlistId)
                    playlistSongRepository.save(playlistSong)
                }

                val bundle = Bundle()
                bundle.putString("playlistId", playlistId)
                findNavController().navigate(R.id.homePlaylistSongs, bundle)
            }
        }
    }

    private fun updateSongsOnScreen() {
        lifecycleScope.launch {
            songsRepository.getAllToAdd(playlistId).observe(viewLifecycleOwner) {songs ->
                val convertedSongs = mutableListOf<TOSong>()

                songs.forEach { song ->
                    val toSong = TOSong(song.songId, song.name, false)
                    convertedSongs.add(toSong)
                }

                adapter.update(convertedSongs)
            }
        }
    }
}