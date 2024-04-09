package br.com.listennow.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.FragmentPlaylistHomeSongsBinding
import br.com.listennow.model.Song
import br.com.listennow.repository.playlistsong.PlaylistSongRepository
import br.com.listennow.ui.MainActivity
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistHomeSongsFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistHomeSongsBinding
    private lateinit var adapter: ListSongsAdapter
    private lateinit var playlistId: String

    private val mainActivity by lazy {
        activity as MainActivity
    }

    private val playlistSongRepository by lazy {
        PlaylistSongRepository(AppDatabase.getInstance(requireContext()).playlistSongDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistHomeSongsBinding.inflate(inflater, container, false)
        adapter = ListSongsAdapter(emptyList(), requireContext())

        arguments?.getString("playlistId").let { playlistIdFromArgs ->
            playlistIdFromArgs?.let {
                playlistId = playlistIdFromArgs

                lifecycleScope.launch {
                    playlistSongRepository.getSongsFromPlaylist(playlistIdFromArgs).collect {
                        updateSongsOnScreen(it.songs)
                    }
                }
            }
        }

        configRecyclerSongs()
        onAddSongsClicked()
        configSongClicked()
        configOnNextSongAutomatically()

        return binding.root
    }

    private fun configRecyclerSongs() {
        val listSongs = binding.songs

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    private fun onAddSongsClicked() {
        binding.btnAddSongs.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("playlistId", playlistId)
            findNavController().navigate(R.id.selectPlaylistsSongs, bundle)
        }
    }

    private fun updateSongsOnScreen(songs: List<Song>) {
        adapter.update(songs)
        SongUtil.songs = songs
    }

    private fun configOnNextSongAutomatically() {
        SongUtil.onNextSong = { song ->
            SongUtil.readSong(requireContext(), song)
            configSongToolbar(song)
        }
    }

    private fun configSongClicked() {
        adapter.onItemClick = { song ->
            SongUtil.readSong(requireContext(), song)
            configSongToolbar(song)
        }
    }

    private fun configSongToolbar(song: Song) {
        mainActivity.binding.listSongsTitle.text = song.name
        mainActivity.binding.listSongsArtist.text = song.artist
        mainActivity.binding.play.setBackgroundResource(R.drawable.ic_pause)

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = ImageUtil.getBitmapImage(song.smallThumb, 120, 120, requireContext())

            withContext(Dispatchers.Main) {
                mainActivity.binding.homeThumbSongDetails.setImageBitmap(bitmap)
            }
        }
    }
}