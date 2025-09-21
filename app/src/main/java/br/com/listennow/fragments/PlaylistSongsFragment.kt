package br.com.listennow.fragments

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SongsAdapter
import br.com.listennow.databinding.FragmentPlaylistSongsBinding
import br.com.listennow.foreground.Actions
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.PlaylistSongsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistSongsFragment : CommonFragment<PlaylistSongsViewModel, FragmentPlaylistSongsBinding>(), IControllerItemsAdapter {
    private lateinit var _adapter: SongsAdapter

    override val viewModel: PlaylistSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_playlist_songs

    override fun loadNavParams() {
        viewModel.navParams = PlaylistSongsFragmentArgs.fromBundle(requireArguments()).navParams
    }

    override fun configView() {
        configRecyclerView()
        binding.playlistsSongsEmptyState.hideAction()
    }

    private fun configRecyclerView() {
        _adapter = SongsAdapter(BR.songItem, this)

        binding.playlistSongsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistSongsRecyclerview.setHasFixedSize(true)
        binding.playlistSongsRecyclerview.adapter = _adapter
    }

    override fun setViewListeners() {
        binding.playlistSongsAddSong.setOnClickListener {
            setFragmentResultListener(SelectSongsFragment.SELECT_SONGS_FRAGMENT_KEY) { _, bundle ->
                val songsIds = bundle.getStringArrayList(SelectSongsFragment.SELECT_SONGS_FRAGMENT_RESULT)
                viewModel.addSongsToPlaylist(songsIds)
            }
            findNavController().navigate(PlaylistSongsFragmentDirections.actionPlaylistSongsFragmentToSelectSongsFragment())
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

        view.setOnClickListener {
            SongUtil.readSong(requireContext(), item)
            mainActivity.startNotificationService(Actions.PLAY_SPECIFIC)
        }
    }

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) = Unit
}