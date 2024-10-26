package br.com.listennow.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.adapter.HomeSongsAdapter
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeSongsAdapter

    private val mainActivity by lazy {
        activity as MainActivity
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = HomeSongsAdapter(emptyList(), requireContext())

        return binding.root
    }

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        onToolbarRightSwiped()
        configSearchSongsFilterListener()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && !SongUtil.actualSong!!.songId.isNullOrEmpty()) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSongDetailsFragment(SongUtil.actualSong!!.songId))
            }
        }

        binding.refreshSongs.setOnRefreshListener {
            syncSongs()
        }

        mainActivity.binding.play.setOnClickListener {
            if(SongUtil.isPlaying()) {
                SongUtil.pause()
                mainActivity.binding.play.setBackgroundResource(R.drawable.ic_play)
            } else {
                SongUtil.play()
                mainActivity.binding.play.setBackgroundResource(R.drawable.ic_pause)
            }
        }

        SongUtil.onNextSong = { song ->
            SongUtil.readSong(requireContext(), song)
            viewModel.updateActualSong(song)
        }

        adapter.onItemClick = { song ->
            SongUtil.readSong(requireContext(), song)
            viewModel.updateActualSong(song)
        }
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            SongUtil.songs = songs
            updateSongsOnScreen(songs)

        }

        viewModel.filteredSongs.observe(viewLifecycleOwner) { songs ->
            updateSongsOnScreen(songs)
        }

        viewModel.actualSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                configSongToolbar(it)
            }
        }

        viewModel.syncing.observe(viewLifecycleOwner) {
            binding.refreshSongs.isRefreshing = it.get()
        }
    }

    override fun loadData() {
        configRecyclerSongs()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadSongs()
            viewModel.loadActualSong()
        }
    }

    private fun updateSongsOnScreen(songs: List<Song>) {
        adapter.update(songs)
    }

    private fun configSearchSongsFilterListener() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(Runnable {
                        p0?.let {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.loadSongsFiltering(p0)
                            }
                        }
                    }, 400)

                return true
            }
        })
    }

    private fun configRecyclerSongs() {
        binding.songs.layoutManager = LinearLayoutManager(requireContext())
        binding.songs.setHasFixedSize(true)
        binding.songs.adapter = adapter
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

    private fun syncSongs() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.syncSongs(
                userId = "341176e2-e00e-4b35-af24-5516fcaa6956"
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onToolbarRightSwiped() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        val buttons = mainActivity.binding.playBackButtons

        buttons.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(Runnable {
                         SongUtil.playRandomSong()
                    }, 200)
                }
            }

            v?.onTouchEvent(event) ?: true
        }
    }
}
