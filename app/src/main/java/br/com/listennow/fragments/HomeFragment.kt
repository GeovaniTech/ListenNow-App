package br.com.listennow.fragments

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.media.AudioManager
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
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.model.Song
import br.com.listennow.receiver.HeadsetStateReceiver
import br.com.listennow.repository.SongRepository
import br.com.listennow.adapter.HomeSongsAdapter
import br.com.listennow.viewmodel.HomeViewModel
import br.com.listennow.viewmodel.factory.HomeViewModelFactory
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.webclient.song.service.SongWebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : AbstractUserFragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeSongsAdapter

    private val mainActivity by lazy {
        activity as MainActivity
    }

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(SongRepository(AppDatabase.getInstance(requireContext()).songDao(), SongWebClient()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = HomeSongsAdapter(emptyList(), requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSongs()
        configSelectedSong()
        configRecyclerSongs()
        configOnNextSongAutomatically()
        configSongClicked()
        configPlayPause()
        configSwipeRefresh()
        configToolbarClicked()
        configSearchSongsFilter()
        onToolbarRightSwiped()
        configPhoneDisconnectedReceiver()
    }

    private fun configSelectedSong() {
        viewModel.actualSong.value?.let {
            configSongToolbar(it)
        }
    }

    private fun loadSongs() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                updateSongsOnScreen()
            }

            syncSongs()
        }
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
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(Runnable {
                        p0?.let {
                            adapter.update(viewModel.getSongsFiltering(p0))
                        }
                    }, 400)
                }

                return true
            }
        })
    }

    private fun configSwipeRefresh() {
        val swipeRefresh = binding.refreshSongs
        swipeRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                syncSongs()
            }
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
                bundle.putString("song", SongUtil.actualSong.songId)
                findNavController().navigate(R.id.songDetailsFragment, bundle)
            }
        }
    }

    private fun updateSongsOnScreen() {
        viewModel.getSongs().observe(viewLifecycleOwner) {songs ->
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
        viewModel.actualSong.value = song
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
        viewModel.updateAll("341176e2-e00e-4b35-af24-5516fcaa6956")
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
                    playRandomSong()?.let {
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed(Runnable {
                            lifecycleScope.launch {
                                withContext(Dispatchers.Main) {
                                    configSongToolbar(it)
                                }
                            }
                        }, 200)
                    }
                }
            }

            v?.onTouchEvent(event) ?: true
        }
    }

    private fun playRandomSong(): Song? {
        if(SongUtil.songs.isNotEmpty()) {
            val song = getRandomSong()

            SongUtil.readSong(requireContext(), song)

            return song
        }

        return null
    }

    private fun getRandomSong(): Song {
        val position = (0 until (SongUtil.songs.size)).random()
        return SongUtil.songs[position]
    }


    private fun configPhoneDisconnectedReceiver() {
        val receiver = HeadsetStateReceiver()
        val receiverFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

        receiver.button = mainActivity.binding.play

        mainActivity.registerReceiver(receiver, receiverFilter)
    }
}
