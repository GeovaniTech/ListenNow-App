package br.com.listennow.fragments

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SongsAdapter
import br.com.listennow.databinding.FragmentAlbumSongsBinding
import br.com.listennow.foreground.Actions
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.AlbumSongsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumSongsFragment : CommonFragment<AlbumSongsViewModel, FragmentAlbumSongsBinding>(), IControllerItemsAdapter {
    private lateinit var _adapter: SongsAdapter

    override val viewModel: AlbumSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_album_songs

    override fun loadNavParams() {
        AlbumSongsFragmentArgs.fromBundle(requireArguments()).navParams.let {
            viewModel.navParams = it
        }
    }

    override fun setViewListeners() {
        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(AlbumSongsFragmentDirections.actionAlbumSongsFragmentToSongDetailsFragment(
                    SongUtil.actualSong!!.videoId
                ))
            }
        }
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) {
            _adapter.loadItems(it)
            SongUtil.songs = it
            stopShimmer()
        }
    }

    override fun loadData() {
        configRecyclerSongs()
        startShimmer()
        viewModel.loadData()
    }

    private fun configRecyclerSongs() {
        createAdapter()

        binding.albumSongsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.albumSongsRecyclerview.setHasFixedSize(true)
        binding.albumSongsRecyclerview.adapter = _adapter
    }

    private fun createAdapter() {
        _adapter = SongsAdapter(BR.songItem, this)
    }

    private fun startShimmer() {
        binding.albumSongsRecyclerview.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    private fun stopShimmer() {
        binding.albumSongsRecyclerview.visibility = View.VISIBLE
        binding.shimmerList.visibility = View.GONE
        binding.shimmerList.startShimmer()
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