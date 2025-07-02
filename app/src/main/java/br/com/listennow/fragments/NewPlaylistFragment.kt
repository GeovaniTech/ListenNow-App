package br.com.listennow.fragments

import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import br.com.listennow.R
import br.com.listennow.databinding.FragmentPlaylistsNewPlaylistBinding
import br.com.listennow.enums.EnumPlaylistActionStatus
import br.com.listennow.viewmodel.NewPlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPlaylistFragment : CommonFragment<NewPlaylistViewModel, FragmentPlaylistsNewPlaylistBinding>() {
    override val viewModel: NewPlaylistViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        setFullWidth()
    }

    override fun getLayout(): Int = R.layout.fragment_playlists_new_playlist

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
        binding.playlistsNewPlaylistSave.setOnClickListener {
            onCreateNewPlaylist()
        }
        binding.playlistsNewPlaylistCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun onCreateNewPlaylist() {
        viewModel.createPlaylist()
    }

    override fun setViewModelObservers() {
        viewModel.statusCallback.observe(viewLifecycleOwner) { status ->
            when (status) {
                EnumPlaylistActionStatus.ERROR_TITLE_IS_REQUIRED -> {
                    Toast.makeText(requireContext(), R.string.playlists_new_playlist_msg_title_is_required, Toast.LENGTH_SHORT).show()
                }
                EnumPlaylistActionStatus.PLAYLIST_SAVED_SUCCESSFULLY -> {
                    setFragmentResult(NEW_PLAYLIST_FRAGMENT_KEY, bundleOf(NEW_PLAYLIST_FRAGMENT_RESULT to viewModel.playlist))
                    dismiss()
                }
                else -> {}
            }
        }
    }

    override fun loadBindingVariables() {
        binding.newPlaylistViewModel = viewModel
    }

    override fun loadData() = Unit

    companion object {
        const val TAG = "NewPlaylistFragment"
        const val NEW_PLAYLIST_FRAGMENT_KEY = "NEW_PLAYLIST_FRAGMENT_KEY"
        const val NEW_PLAYLIST_FRAGMENT_RESULT = "NEW_PLAYLIST_FRAGMENT_RESULT"

        fun newInstance(): NewPlaylistFragment = NewPlaylistFragment()
    }
}