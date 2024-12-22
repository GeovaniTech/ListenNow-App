package br.com.listennow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.com.listennow.databinding.FragmentSongDetailsBinding
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SongDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SongDetailsFragment : CommonFragment<SongDetailsViewModel>() {
    private lateinit var binding: FragmentSongDetailsBinding

    override val viewModel: SongDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

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