package br.com.listennow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.listennow.databinding.FragmentSongDetailsBinding
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SongDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        viewModel.song.observe(viewLifecycleOwner) {song ->
            song?.let {
                bindSong(song)
            }
        }
    }

    override fun loadData() {
        lifecycleScope.launch {
            viewModel.loadSong()
        }
    }

    private fun bindSong(song: Song) {
        // TODO - I am gonna change this to dataBinding
        binding.nameSongDetail.text = song.name
        binding.artistSongDetail.text = song.artist
        binding.albumSongDetail.text = song.album
        binding.lyricsSongDetail.text = song.lyrics

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = ImageUtil.getBitmapImage(song.thumb, 120, 120, requireContext())

            withContext(Dispatchers.Main) {
                binding.imgThumbSongDetail.setImageBitmap(bitmap)
            }
        }

    }
}