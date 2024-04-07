package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.databinding.FragmentSearchYoutubeSongsBinding
import br.com.listennow.ui.recyclerview.ListSongsYTAdapter
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchYoutubeSongsFragment : Fragment() {
    private lateinit var binding: FragmentSearchYoutubeSongsBinding
    private lateinit var adapter: ListSongsYTAdapter

    private val songWebClient by lazy {
        SongWebClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchYoutubeSongsBinding.inflate(inflater, container, false)
        adapter = ListSongsYTAdapter(requireContext(), "341176e2-e00e-4b35-af24-5516fcaa6956", emptyList())

        configRecyclerView()
        configSearchSongsFilter()
        configDownloadSong()

        return binding.root
    }

    private fun configSearchSongsFilter() {
        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                lifecycleScope.launch {
                    val songs = songWebClient.getYTSongs(p0.toString())
                    songs?.let {
                        updateSongsOnScreen(songs)
                    }
                }
                return true
            }

        })
    }

    private fun configRecyclerView() {
        val listSongs = binding.listSongsYT

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    private fun updateSongsOnScreen(songs: List<SearchYTSongResponse>) {
        adapter.update(songs)
    }

    private fun configDownloadSong() {
        adapter.onDownloadClicked = { song ->
            Toast.makeText(requireContext(), R.string.download_started, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                songWebClient.downloadSong(song.videoId, "341176e2-e00e-4b35-af24-5516fcaa6956")
            }
        }
    }
}