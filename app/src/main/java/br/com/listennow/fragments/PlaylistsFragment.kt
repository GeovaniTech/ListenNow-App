package br.com.listennow.fragments

import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import br.com.listennow.R
import br.com.listennow.databinding.FragmentPlaylistsBinding
import br.com.listennow.decorator.PlaylistDecorator
import br.com.listennow.extensions.getSerializableValue
import br.com.listennow.fragments.NewPlaylistFragment.Companion.NEW_PLAYLIST_FRAGMENT_KEY
import br.com.listennow.fragments.NewPlaylistFragment.Companion.NEW_PLAYLIST_FRAGMENT_RESULT
import br.com.listennow.fragments.NewPlaylistFragment.Companion.TAG
import br.com.listennow.viewmodel.PlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistsFragment : CommonFragment<PlaylistsViewModel, FragmentPlaylistsBinding>() {
    override val viewModel: PlaylistsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_playlists

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
        binding.playlistsNewPlaylist.setOnClickListener {
            setFragmentResultListener(NEW_PLAYLIST_FRAGMENT_KEY) { _, bundle ->
                val playlist = bundle.getSerializableValue(NEW_PLAYLIST_FRAGMENT_RESULT, PlaylistDecorator::class.java)!!
                showSnackBar(getString(R.string.playlists_new_playlist_msg_playlist_saved, playlist.title))
            }
            NewPlaylistFragment.newInstance().show(parentFragmentManager, TAG)
        }
    }

    override fun setViewModelObservers() = Unit

    override fun loadData() {

    }
}