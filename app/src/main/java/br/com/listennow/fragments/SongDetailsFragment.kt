package br.com.listennow.fragments

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
        mainActivity.binding.playBackButtons.setOnClickListener {
            SongUtil.actualSong?.videoId?.let {
                viewModel.songId = it
                loadData()
            }
        }
    }

    override fun setViewModelObservers() {
        viewModel.song.observe(viewLifecycleOwner) {
            binding.song = it
        }
    }

    override fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadSong()
        }
    }
}