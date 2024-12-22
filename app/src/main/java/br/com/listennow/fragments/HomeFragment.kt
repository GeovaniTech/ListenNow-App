package br.com.listennow.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.HomeSongsAdapter
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.listener.OnSwipeTouchListener
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : CommonFragment<HomeViewModel>(), IControllerItemsAdapter {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeSongsAdapter

    override val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = HomeSongsAdapter(BR.songItem, this)

        return binding.root
    }

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        configToolbar()
        configSearchView()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSongDetailsFragment(SongUtil.actualSong!!.videoId))
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

        binding.fragmentHomeButtonFindNewSong.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchNewSongs())
        }

        SongUtil.onNextSong = { song ->
            SongUtil.readSong(requireContext(), song)
            viewModel.updateActualSong(song)
            configSongToolbar(song)
        }
    }

    private fun configSearchView() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(Runnable {
                    filter?.let {
                        viewModel.songFilter = filter
                        viewModel.viewModelScope.launch {
                            viewModel.loadSongsFiltering(it)
                        }
                    }
                }, 700)

                return true
            }
        })
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            SongUtil.songs = songs
            setViewState(songs)
        }

        viewModel.filteredSongs.observe(viewLifecycleOwner) { songs ->
            setViewState(songs)
        }

        viewModel.actualSong.observe(viewLifecycleOwner) { song ->
            song?.let {
                configSongToolbar(it)
            }
        }

        viewModel.syncing.observe(viewLifecycleOwner) {
            binding.refreshSongs.isRefreshing = it.get()

            if(!it.get()) {
                loadData()
            }
        }
    }

    private fun setViewState(songs: List<Song>) {
        binding.shimmerList.stopShimmer()
        binding.shimmerList.visibility = View.GONE

        if (songs.isEmpty()) {
            binding.fragmentHomeEmptyImage.visibility = View.VISIBLE
            binding.fragmentHomeEmptyText.visibility = View.VISIBLE
            binding.fragmentHomeButtonFindNewSong.visibility = View.VISIBLE
            binding.songs.visibility = View.GONE
        } else {
            updateSongsOnScreen(songs)

            binding.fragmentHomeEmptyImage.visibility = View.GONE
            binding.fragmentHomeEmptyText.visibility = View.GONE
            binding.fragmentHomeButtonFindNewSong.visibility = View.GONE
            binding.songs.visibility = View.VISIBLE
        }
    }

    override fun loadData() {
        configRecyclerSongs()
        startShimmer()

        viewModel.viewModelScope.launch {
            if (viewModel.songFilter.isNullOrEmpty()) {
                viewModel.loadSongs()
            } else {
                viewModel.loadSongsFiltering(viewModel.songFilter!!)
            }
            viewModel.loadActualSong()
        }
    }

    private fun updateSongsOnScreen(songs: List<Song>) {
        adapter.loadItems(songs)
    }

    private fun startShimmer() {
        binding.songs.visibility = View.GONE
        binding.fragmentHomeEmptyImage.visibility = View.GONE
        binding.fragmentHomeEmptyText.visibility = View.GONE
        binding.fragmentHomeButtonFindNewSong.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
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
        Glide.with(mainActivity.binding.homeThumbSongDetails).load(song.thumb).into(mainActivity.binding.homeThumbSongDetails)
    }

    private fun syncSongs() {
        startShimmer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.syncSongs()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configToolbar() {
        val buttons = mainActivity.binding.playBackButtons

        buttons.setOnTouchListener(object: OnSwipeTouchListener(requireContext()) {
            // Here I could create a feature to play the previous and next songs depending of the swipe
            override fun onSwipeRight() {
                SongUtil.playRandomSong()
            }
        })
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as Song

        view.setOnClickListener {
            SongUtil.readSong(requireContext(), item)
            viewModel.updateActualSong(item)
            configSongToolbar(item)
        }
    }

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {}
}
