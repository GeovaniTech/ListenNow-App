package br.com.listennow.fragments

import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SongsAdapter
import br.com.listennow.databinding.FragmentPlaylistSongsBinding
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.foreground.Actions
import br.com.listennow.model.Song
import br.com.listennow.navparams.SelectSongsNavParams
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.PlaylistSongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistSongsFragment : CommonFragment<PlaylistSongsViewModel, FragmentPlaylistSongsBinding>(), IControllerItemsAdapter {
    private lateinit var _adapter: SongsAdapter

    override val viewModel: PlaylistSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_playlist_songs

    override fun loadNavParams() {
        viewModel.navParams = PlaylistSongsFragmentArgs.fromBundle(requireArguments()).navParams
    }

    override fun applyInsetsEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.playlistSongsSearchBar
        ) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())

            v.setPadding(
                v.paddingLeft,
                statusBarInsets.top,
                v.paddingRight,
                v.paddingBottom
            )

            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.containerUnderSearchSongsPlaylists
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

    override fun configView() {
        configRecyclerView()
        configSearchView()
        binding.playlistsSongsEmptyState.hideAction()
    }

    private fun configSearchView() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.playlistSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    filter?.let {
                        viewModel.query = filter
                        viewModel.loadData()
                    }
                }, 700)

                return true
            }
        })
    }

    private fun startShimmer() {
        binding.playlistSongsRecyclerview.visibility = View.GONE
        binding.playlistsSongsEmptyState.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    private fun configRecyclerView() {
        _adapter = SongsAdapter(BR.songItem, this)

        binding.playlistSongsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistSongsRecyclerview.setHasFixedSize(true)
        binding.playlistSongsRecyclerview.adapter = _adapter

        binding.playlistSongsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !binding.playlistSongsAddSong.isShown) {
                    binding.playlistSongsAddSong.show()
                } else if (dy > 0 && binding.playlistSongsAddSong.isShown) {
                    binding.playlistSongsAddSong.hide()
                }
            }
        })
    }

    override fun setViewListeners() {
        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(PlaylistSongsFragmentDirections.actionPlaylistSongsFragmentToSongDetailsFragment(
                    SongUtil.actualSong!!.videoId
                ))
            }
        }

        binding.playlistSongsAddSong.setOnClickListener {
            viewModel.viewModelScope.launch {
                setFragmentResultListener(SelectSongsFragment.SELECT_SONGS_FRAGMENT_KEY) { _, bundle ->
                    val songsIds = bundle.getStringArrayList(SelectSongsFragment.SELECT_SONGS_FRAGMENT_RESULT)
                    viewModel.addSongsToPlaylist(songsIds)
                }

                val songsIdsToIgnore = viewModel.getSongsIdsFromPlaylist()

                findNavController().navigate(PlaylistSongsFragmentDirections.actionPlaylistSongsFragmentToSelectSongsFragment(
                    SelectSongsNavParams(
                        songsIdsToIgnore
                    )
                ))
            }

        }
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            SongUtil.songs = songs
            _adapter.loadItems(songs)

            setViewState(songs)
        }

        viewModel.onSongsAddedCallback.observe(viewLifecycleOwner) { success ->
            if (success.get()) {
                viewModel.loadData()
                viewModel.postSongsAddedCallback(false)

                showSnackBar(R.string.playlist_songs_songs_added_success)
            }
        }

        viewModel.onSongsDeletedCallback.observe(viewLifecycleOwner) { success ->
            if (success.get()) {
                viewModel.loadData()
                viewModel.postSongsDeletedCallback(false)

                showSnackBar(R.string.playlist_songs_song_deleted_message)
            }
        }
    }

    private fun setViewState(songs: List<Song>) {
        binding.shimmerList.stopShimmer()
        binding.shimmerList.visibility = View.GONE

        binding.playlistSongsRecyclerview.isVisible = songs.isNotEmpty()
        binding.playlistsSongsEmptyState.isVisible = songs.isEmpty()
    }

    override fun loadData() {
        binding.shimmerList.startShimmer()
        viewModel.loadData()
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
        dataBinding as FragmentSongItemBinding

        dataBinding.deleteSongButton.visibility = View.VISIBLE
    }
}