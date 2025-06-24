package br.com.listennow.fragments

import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.AlbumsAdapter
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.databinding.FragmentAlbumsBinding
import br.com.listennow.databinding.FragmentAlbumsItemBinding
import br.com.listennow.decorator.AlbumItemDecorator
import br.com.listennow.model.Song
import br.com.listennow.navparams.AlbumSongsNavParams
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.AlbumsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlbumsFragment : CommonFragment<AlbumsViewModel, FragmentAlbumsBinding>(), IControllerItemsAdapter {

    private lateinit var _adapter: AlbumsAdapter

    override val viewModel: AlbumsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_albums

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
        configSearchView()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(AlbumsFragmentDirections.actionAlbumsFragmentToSongDetailsFragment(
                    SongUtil.actualSong!!.videoId
                ))
            }
        }
    }

    private fun configSearchView() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.albumsSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(Runnable {
                    filter?.let {
                        viewModel.viewModelScope.launch {
                            viewModel.loadData(it)
                        }
                    }
                }, 700)

                return true
            }
        })
    }

    private fun startShimmer() {
        binding.albumsRecyclerview.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    private fun setViewState(albums: List<AlbumItemDecorator>) {
        binding.shimmerList.stopShimmer()
        binding.shimmerList.visibility = View.GONE
        binding.albumsRecyclerview.visibility = View.VISIBLE

        // TODO - Create EmptyState Component
    }

    override fun setViewModelObservers() {
        viewModel.albums.observe(viewLifecycleOwner) {
            _adapter.loadItems(it)
            setViewState(it)
        }
    }

    override fun loadData() {
        configRecyclerView()
        startShimmer()
        viewModel.loadData()
    }

    private fun configRecyclerView() {
        createAdapter()

        binding.albumsRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.albumsRecyclerview.setHasFixedSize(true)
        binding.albumsRecyclerview.adapter = _adapter
    }

    private fun createAdapter() {
        _adapter = AlbumsAdapter(BR.albumItemDecorator, this)
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as AlbumItemDecorator
        dataBinding as FragmentAlbumsItemBinding

        view.setOnClickListener {
            findNavController().navigate(AlbumsFragmentDirections.actionAlbumsFragmentToAlbumSongsFragment(
                AlbumSongsNavParams(
                    album = item.name,
                    artist = item.artist
                )
            ))
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