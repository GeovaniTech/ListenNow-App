package br.com.listennow.fragments

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.com.listennow.R
import br.com.listennow.databinding.FragmentSongDetailsBinding
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SongDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongDetailsFragment : CommonFragment<SongDetailsViewModel, FragmentSongDetailsBinding>() {
    override val viewModel: SongDetailsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_song_details

    override fun loadNavParams() {
        viewModel.songId = SongDetailsFragmentArgs.fromBundle(requireArguments()).songId
    }

    override fun setViewListeners() {
        binding.btnPlayNext.setOnClickListener {
            SongUtil.playRandomSong()
            loadActualSong()
        }

        binding.btnDeleteSong.setOnClickListener {
            SongUtil.actualSong?.let { song ->
                showAlertDialog(
                    title = getString(R.string.are_you_sure),
                    message = getString(R.string.home_are_you_sure_about_deleting_song, song.name),
                    positiveClick = {
                        viewModel.deleteSong(song)
                    }
                )
            }
        }
    }

    private fun loadActualSong() {
        SongUtil.actualSong?.videoId?.let {
            viewModel.songId = it
            loadData()
            mainActivity.configToolbar()
        }
    }

    override fun setViewModelObservers() {
        viewModel.song.observe(viewLifecycleOwner) {
            binding.song = it
        }

        viewModel.songDeleted.observe(viewLifecycleOwner) { deleted ->
            deleted?.let {
                if (it.second.get()) {
                    if (SongUtil.songs.isEmpty()) {
                        mainActivity.stopNotificationService()
                        mainActivity.configEmptyToolbar()
                    }

                    SongUtil.playRandomSong()
                    loadActualSong()

                    showSnackBar(getString(R.string.song_deleted_successfully, it.first.name))
                } else {
                    showSnackBar(getString(R.string.failed_to_delete_song, it.first.name))
                }
            }
        }
    }

    override fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadSong()
        }
    }

    override fun applyInsetsEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.lyricsSongDetail
        ) { v, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())

            v.setPadding(statusBarInsets.left,
                0,
                statusBarInsets.right,
                statusBarInsets.bottom
            )

            insets
        }
    }
}