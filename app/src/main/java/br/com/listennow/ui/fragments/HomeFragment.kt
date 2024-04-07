package br.com.listennow.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.model.Song
import br.com.listennow.repository.song.SongRepository
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.webclient.user.service.SongWebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ListSongsAdapter

    private val mainActivity by lazy {
        activity as MainActivity
    }

    private val repository by lazy {
        SongRepository(AppDatabase.getInstance(requireContext()).songDao(), SongWebClient())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = ListSongsAdapter(emptyList(), requireContext())

        lifecycleScope.launch {
            launch {
                updateSongsOnScreen()
                playRandomSong()
            }

            syncSongs()
        }

        configRecyclerSongs()
        configOnNextSongAutomatically()
        configSongClicked()
        configPlayPause()
        configSwipeRefresh()
        configToolbarClicked()
        configSearchSongsFilter()

        return binding.root
    }

    private fun configSearchSongsFilter() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                lifecycleScope.launch {
                    val songs = repository.getAllFiltering(p0.toString())
                    songs.collect {songsDB ->
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(Runnable {
                            adapter.update(songsDB)
                        }, 400)
                    }
                }
                return true
            }
        })
    }

    private fun configSwipeRefresh() {
        val swipeRefresh = binding.refreshSongs
        swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                syncSongs()
            }
        }
    }

    private fun playRandomSong() {
        if(SongUtil.songs.isNotEmpty()) {
            val song = getRandomSong()

            SongUtil.readSong(requireContext(), song)
            configSongToolbar(song)
        }
    }

    private fun configOnNextSongAutomatically() {
        SongUtil.onNextSong = { song ->
            SongUtil.readSong(requireContext(), song)
            configSongToolbar(song)
        }
    }

    private fun configSongClicked() {
        adapter.onItemClick = { song ->
            SongUtil.readSong(requireContext(), song)
            configSongToolbar(song)
        }
    }

    private fun configToolbarClicked() {
        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.isPlaying()) {
                val bundle = Bundle()
                bundle.putString("song", SongUtil.actualSong.id)
                findNavController().navigate(R.id.songDetailsFragment, bundle)
            }
        }
    }

    private fun getRandomSong(): Song {
        val position = (0 until (SongUtil.songs.size)).random()
        return SongUtil.songs[position]
    }

    private suspend fun updateSongsOnScreen() {
        repository.getAll().collect {songs ->
            adapter.update(songs)
            SongUtil.songs = songs
        }
    }

    private fun configRecyclerSongs() {
        val listSongs = binding.songs

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    private fun configSongToolbar(song: Song) {
        mainActivity.binding.listSongsTitle.text = song.name
        mainActivity.binding.listSongsArtist.text = song.artist
        mainActivity.binding.play.setBackgroundResource(R.drawable.ic_pause)

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = ImageUtil.getBitmapImage(song.smallThumb, 120, 120, requireContext())

            withContext(Dispatchers.Main) {
                mainActivity.binding.homeThumbSongDetails.setImageBitmap(bitmap)
            }
        }
    }
    private suspend fun syncSongs() {
        repository.updateAll("341176e2-e00e-4b35-af24-5516fcaa6956")
        binding.refreshSongs.isRefreshing = false
        updateSongsOnScreen()
    }

    private fun configPlayPause() {
        mainActivity.binding.play.setOnClickListener {
            if(SongUtil.isPlaying()) {
                SongUtil.pause()
                mainActivity.binding.play.setBackgroundResource(R.drawable.ic_play)
            } else {
                SongUtil.play()
                mainActivity.binding.play.setBackgroundResource(R.drawable.ic_pause)
            }
        }
    }
}
