package br.com.listennow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.adapter.SearchYoutubeSongsAdapter
import br.com.listennow.databinding.FragmentSearchYoutubeSongsBinding
import br.com.listennow.viewmodel.SearchYoutubeSongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchYoutubeSongsFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentSearchYoutubeSongsBinding
    private lateinit var adapter: SearchYoutubeSongsAdapter

    private val viewModel: SearchYoutubeSongsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchYoutubeSongsBinding.inflate(inflater, container, false)
        adapter = SearchYoutubeSongsAdapter(requireContext(), "341176e2-e00e-4b35-af24-5516fcaa6956", emptyList())

        return binding.root
    }

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                lifecycleScope.launch {
                    filter?.let {
                        viewModel.loadYoutubeSongs(it)
                    }
                }
                return true
            }
        })

        adapter.onDownloadClicked = { song ->
            Toast.makeText(requireContext(), R.string.download_started, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                viewModel.downloadSong(song.videoId, "341176e2-e00e-4b35-af24-5516fcaa6956")
            }
        }
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) {songs ->
            songs?.let {
                adapter.update(it)
            }
        }
    }

    override fun loadData() {
        configRecyclerView()
    }

    private fun configRecyclerView() {
        val listSongs = binding.listSongsYT

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }
}