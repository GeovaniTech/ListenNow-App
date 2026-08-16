package br.com.listennow.fragments

import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SearchYoutubeSongsAdapter
import br.com.listennow.databinding.FragmentSearchYoutubeSongsBinding
import br.com.listennow.databinding.FragmentSearchYoutubeSongsItemBinding
import br.com.listennow.foreground.DownloadYoutubeSongService
import br.com.listennow.foreground.DownloadYoutubeSongsActions
import br.com.listennow.utils.NetworkUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SearchYoutubeSongsViewModel
import br.com.listennow.viewmodel.SearchYoutubeSongsViewModel.Companion.YOUTUBE_BASE_URL
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchYoutubeSongsFragment : CommonFragment<SearchYoutubeSongsViewModel, FragmentSearchYoutubeSongsBinding>(), IControllerItemsAdapter {
    private lateinit var adapter: SearchYoutubeSongsAdapter

    override val viewModel: SearchYoutubeSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_search_youtube_songs

    private fun createAdapter() {
        adapter = SearchYoutubeSongsAdapter(
            variableId = BR.ytSong,
            this
        )
    }

    override fun loadNavParams() = Unit

    override fun applyInsetsEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.cardSearchSongs
        ) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars()
                                                              or WindowInsetsCompat.Type.displayCutout()
                                                              or WindowInsetsCompat.Type.ime())

            v.setPadding(
                v.paddingLeft,
                statusBarInsets.top,
                v.paddingRight,
                v.paddingBottom
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.containerUnderSearchSongsYoutube
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
                        or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                systemBars.bottom
            )
            insets
        }
    }

    override fun setViewListeners() {
        configSearchSongs()
        binding.fragmentSearchYoutubeSongsEmptyState.hideAction()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(SearchYoutubeSongsFragmentDirections.actionSearchNewSongsFragmentSongDetailsFragment(SongUtil.actualSong!!.videoId))
            }
        }
    }

    private fun configSearchSongs() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        createAdapter()
        showSoftKeyboard(binding.searchYtSongs)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    filter?.let {
                        viewModel.viewModelScope.launch {
                            viewModel.loadYoutubeSongs(it)
                        }
                    }
                }, 700)
                return true
            }
        })
    }

    private fun startShimmer() {
        binding.listSongsYT.visibility = View.GONE
        binding.fragmentSearchYoutubeSongsEmptyState.isVisible = false
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) {songs ->
            binding.shimmerList.stopShimmer()
            binding.shimmerList.visibility = View.GONE

            if (songs.isNullOrEmpty()) {
                binding.fragmentSearchYoutubeSongsEmptyState.isVisible = true
                binding.listSongsYT.visibility = View.GONE
            } else {
                adapter.loadItems(songs)

                binding.fragmentSearchYoutubeSongsEmptyState.isVisible = false
                binding.listSongsYT.visibility = View.VISIBLE
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

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as SearchYTSongResponse
        dataBinding as FragmentSearchYoutubeSongsItemBinding

        dataBinding.listSongsSearchYoutube.setOnClickListener {
            if (NetworkUtil.isInternetAvailable(requireContext())) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = (YOUTUBE_BASE_URL + item.videoId).toUri()
                    }

                    startActivity(intent)

                    viewModel.updateExceptionMessage()
                } catch (e: Exception) {
                    viewModel.updateExceptionResMessage(R.string.failed_to_open_song)
                }
            } else {
                viewModel.updateExceptionResMessage(R.string.check_internet_connection)
            }
        }

        dataBinding.listSongsSearchSync.setOnClickListener {
            startDownloadYoutubeSongService(item)
        }
    }

    private fun startDownloadYoutubeSongService(item: SearchYTSongResponse) {
        Intent().also {
            it.setClass(requireActivity(), DownloadYoutubeSongService::class.java)
            it.action = DownloadYoutubeSongsActions.DOWNLOAD_SONG.toString()
            it.putExtra(DownloadYoutubeSongService.VIDEO_ID, item.videoId)
            it.putExtra(DownloadYoutubeSongService.USER_ID, viewModel.user!!.id)
            it.putExtra(DownloadYoutubeSongService.SONG_NAME, item.title)
            it.putExtra(DownloadYoutubeSongService.ARTIST, item.artist)

            ContextCompat.startForegroundService(requireContext(), it)
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