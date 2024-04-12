package br.com.listennow.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentSongDetailsBinding
import br.com.listennow.model.Song
import br.com.listennow.repository.song.SongRepository
import br.com.listennow.utils.ImageUtil
import br.com.listennow.webclient.song.service.SongWebClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongDetailsFragment : Fragment() {
    private lateinit var binding: FragmentSongDetailsBinding
    private lateinit var song: Song

    private val repository by lazy {
        SongRepository(AppDatabase.getInstance(requireContext()).songDao(), SongWebClient())
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongDetailsBinding.inflate(inflater, container, false)

        arguments?.getString("song").let { songFromHomeId ->
            repository.findSongById(songFromHomeId.toString()).let {  songDB ->
                songDB?.let {
                    song = songDB
                }
            }
        }

        bindSong()
        return binding.root
    }

    private fun bindSong() {
        binding.nameSongDetail.text = song.name
        binding.artistSongDetail.text = song.artist
        binding.albumSongDetail.text = song.album
        binding.lyricsSongDetail.text = song.lyrics

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = ImageUtil.getBitmapImage(song.smallThumb, 120, 120, requireContext())

            withContext(Dispatchers.Main) {
                binding.imgThumbSongDetail.setImageBitmap(bitmap)
            }
        }

    }
}