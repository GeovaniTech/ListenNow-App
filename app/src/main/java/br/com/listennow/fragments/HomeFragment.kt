package br.com.listennow.fragments

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.SongsAdapter
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.databinding.FragmentHomeBinding
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.foreground.Actions
import br.com.listennow.listener.OnSwipeTouchListener
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.notify

@AndroidEntryPoint
class HomeFragment : CommonFragment<HomeViewModel, FragmentHomeBinding>(), IControllerItemsAdapter {
    private lateinit var adapter: SongsAdapter

    override val viewModel: HomeViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_home

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        mainActivity.configToolbar()
        configSearchView()
        configCarMode()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSongDetailsFragment(SongUtil.actualSong!!.videoId))
            }
        }

        binding.refreshSongs.setOnRefreshListener {
            syncSongs()
        }

        binding.fragmentHomeButtonFindNewSong.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchNewSongs())
        }
    }

    private fun configCarMode() {
        configCarModeStyle()

        binding.carModeButton.setOnClickListener {
            if (mainActivity.speechRecognizer != null) {
                mainActivity.endSpeechRecognizer()
            } else {
                mainActivity.startSpeechRecognizer()
            }

            configCarModeStyle()
        }
    }

    private fun configCarModeStyle() {
        if (mainActivity.speechRecognizer != null) {
            binding.carModeButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        } else {
            binding.carModeButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.esmerald)
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

        viewModel.syncing.observe(viewLifecycleOwner) {
            binding.refreshSongs.isRefreshing = it.get()

            if(!it.get()) {
                loadData()
            }
        }

        viewModel.songDeleted.observe(viewLifecycleOwner) { deleted ->
            deleted?.let {
                if (it.second.get()) {
                    val removeSong = it.first

                    val songs = adapter.items!!.toMutableList()
                    val songIndex = songs.indexOf(removeSong)

                    (binding.songs.adapter as SongsAdapter).removeAt(songIndex)

                    if (SongUtil.songs.isEmpty()) {
                        setViewState(emptyList())

                        mainActivity.stopNotificationService()
                        mainActivity.configEmptyToolbar()
                    }

                    showSnackBar(getString(R.string.song_deleted_successfully, it.first.name))
                } else {
                    showSnackBar(getString(R.string.failed_to_delete_song, it.first.name))
                }
            }

            viewModel.updateSongDeletedCallback(null)
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
        createAdapter()

        binding.songs.layoutManager = LinearLayoutManager(requireContext())
        binding.songs.setHasFixedSize(true)
        binding.songs.adapter = adapter

        binding.songs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !binding.carModeButton.isShown) {
                    binding.carModeButton.show()
                } else if (dy > 0 && binding.carModeButton.isShown) {
                    binding.carModeButton.hide()
                }
            }
        })
    }

    private fun createAdapter() {
        adapter = SongsAdapter(BR.songItem, this)
    }

    private fun syncSongs() {
        startShimmer()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.syncSongs()
        }
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as Song
        dataBinding as FragmentSongItemBinding

        view.setOnClickListener {
            SongUtil.readSong(requireContext(), item)
            mainActivity.startNotificationService(Actions.PLAY_SPECIFIC)
        }

        dataBinding.deleteSongButton.setOnClickListener {
            viewModel.deleteSong(item)
        }
    }

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as Song
        dataBinding as FragmentSongItemBinding

        dataBinding.deleteSongButton.visibility = View.VISIBLE
    }
}
