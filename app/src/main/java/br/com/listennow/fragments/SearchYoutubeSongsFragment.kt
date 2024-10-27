package br.com.listennow.fragments

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.adapter.SearchYoutubeSongsAdapter
import br.com.listennow.databinding.FragmentSearchYoutubeSongsBinding
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SearchYoutubeSongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchYoutubeSongsFragment : CommonFragment<SearchYoutubeSongsViewModel>() {
    private lateinit var binding: FragmentSearchYoutubeSongsBinding
    private lateinit var adapter: SearchYoutubeSongsAdapter

    override val viewModel: SearchYoutubeSongsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchYoutubeSongsBinding.inflate(inflater, container, false)
        adapter = SearchYoutubeSongsAdapter(emptyList())

        return binding.root
    }

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        configSearchSongs()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.songId.isNotEmpty()) {
                findNavController().navigate(SearchYoutubeSongsFragmentDirections.actionSearchNewSongsFragmentSongDetailsFragment(SongUtil.actualSong!!.songId))
            }
        }

        adapter.onDownloadClicked = { song ->
            Toast.makeText(requireContext(), R.string.download_started, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                viewModel.downloadSong(song.videoId)
            }
        }
    }

    private fun configSearchSongs() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(Runnable {
                    filter?.let {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.loadYoutubeSongs(it)
                        }
                    }
                }, 500)

                return true
            }
        })
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