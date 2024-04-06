package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.model.Song
import br.com.listennow.repository.song.SongRepository
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import br.com.listennow.utils.SongUtil
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.launch

class HomeFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: ListSongsAdapter

    private val repository by lazy {
        SongRepository(AppDatabase.getInstance(requireContext()).songDao(), SongWebClient())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListSongsAdapter(emptyList(), requireContext())

        lifecycleScope.launch {
            repository.updateAll("341176e2-e00e-4b35-af24-5516fcaa6956")
            updateSongs()
        }

        configRecyclerSongs()
        configSongsDetails()
    }

    private fun playRandomSong() {
        if(SongUtil.songs.isNotEmpty()) {
            val song = getRandomSong()

            SongUtil.readSong(requireContext(), song)
        }
    }

    private fun configSongsDetails() {
        adapter.onItemClick = { song ->
            try {
                SongUtil.readSong(requireContext(), song)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun configOnNextSongAutomatically() {
        SongUtil.onNextSong = { song ->
            SongUtil.readSong(requireContext(), song)
        }
    }

    private fun getRandomSong(): Song {
        val position = (0 until (SongUtil.songs.size)).random()
        return SongUtil.songs[position]
    }

    private suspend fun updateSongs() {
        repository.getAll().collect {songs ->
            adapter.update(songs)
            SongUtil.songs = songs
            playRandomSong()
        }
    }

    private fun configRecyclerSongs() {
        val listSongs = binding.songs

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }
}