package br.com.listennow.fragments

import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.PlaylistsAdapter
import br.com.listennow.databinding.FragmentPlaylistsBinding
import br.com.listennow.databinding.FragmentPlaylistsItemBinding
import br.com.listennow.decorator.PlaylistDecorator
import br.com.listennow.decorator.PlaylistItemDecorator
import br.com.listennow.enums.EnumPlaylistActionStatus
import br.com.listennow.extensions.getSerializableValue
import br.com.listennow.fragments.NewPlaylistFragment.Companion.NEW_PLAYLIST_FRAGMENT_KEY
import br.com.listennow.fragments.NewPlaylistFragment.Companion.NEW_PLAYLIST_FRAGMENT_RESULT
import br.com.listennow.fragments.NewPlaylistFragment.Companion.TAG
import br.com.listennow.navparams.PlaylistSongsNavParams
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.PlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : CommonFragment<PlaylistsViewModel, FragmentPlaylistsBinding>(), IControllerItemsAdapter {
    private lateinit var _adapter: PlaylistsAdapter
    override val viewModel: PlaylistsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_playlists

    override fun loadNavParams() = Unit

    override fun configView() {
        configRecyclerView()
        binding.playlistsEmptyState.hideAction()
    }

    override fun setViewListeners() {
        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(PlaylistsFragmentDirections.actionPlaylistsFragmentToSongDetailsFragment(
                    SongUtil.actualSong!!.videoId
                ))
            }
        }

        binding.playlistsNewPlaylist.setOnClickListener {
            setFragmentResultListener(NEW_PLAYLIST_FRAGMENT_KEY) { _, bundle ->
                val playlist = bundle.getSerializableValue(NEW_PLAYLIST_FRAGMENT_RESULT, PlaylistDecorator::class.java)!!
                viewModel.loadData()
                showSnackBar(getString(R.string.playlists_new_playlist_msg_playlist_saved, playlist.title))
            }
            NewPlaylistFragment.newInstance().show(parentFragmentManager, TAG)
        }
    }

    override fun setViewModelObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            binding.playlistsEmptyState.isVisible = playlists.isEmpty()
            binding.playlistsRecyclerview.isVisible = playlists.isNotEmpty()

            _adapter.loadItems(playlists)
        }

        viewModel.statusCallback.observe(viewLifecycleOwner) { status ->
            when (status) {
                EnumPlaylistActionStatus.PLAYLIST_DELETED -> {
                    showSnackBar(messageId = R.string.playlistS_msg_playlist_deleted)
                    viewModel.loadData()
                }
                else -> {}
            }
        }
    }

    override fun loadData() {
        viewModel.loadData()
    }

    private fun configRecyclerView() {
        configAdapter()

        binding.playlistsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerview.setHasFixedSize(true)
        binding.playlistsRecyclerview.adapter = _adapter
    }

    private fun configAdapter() {
        _adapter = PlaylistsAdapter(
            variableId = BR.playlistItemDecorator,
            this
        )
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        dataBinding as FragmentPlaylistsItemBinding
        item as PlaylistItemDecorator

        view.setOnClickListener {
            findNavController().navigate(PlaylistsFragmentDirections.actionPlaylistsFragmentToPlaylistSongsFragment(
                PlaylistSongsNavParams(
                    item.playlistId
                )
            ))
        }

        dataBinding.playlistsItemDelete.setOnClickListener {
            viewModel.deletePlaylist(item.playlistId)
        }
    }

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        dataBinding as FragmentPlaylistsItemBinding
        item as PlaylistItemDecorator

        dataBinding.playlistsItemTotalSongs.text = getString(R.string.playlists_item_total_songs, item.totalSongs)

        if (item.artists.isNullOrEmpty()) {
            dataBinding.playlistsItemArtists.text = getString(R.string.playlists_item_artists, getString(R.string.playlists_no_artists_found))
        } else {
            dataBinding.playlistsItemArtists.text = getString(R.string.playlists_item_artists, item.artists)
        }
    }
}