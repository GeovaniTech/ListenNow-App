package br.com.listennow.fragments

import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SongsAdapter
import br.com.listennow.adapter.lookup.SongKeyLookup
import br.com.listennow.adapter.provider.SongKeyProvider
import br.com.listennow.databinding.FragmentSelectSongsBinding
import br.com.listennow.viewmodel.SelectSongsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectSongsFragment : CommonFragment<SelectSongsViewModel, FragmentSelectSongsBinding>(), IControllerItemsAdapter {
    private lateinit var _adapter: SongsAdapter
    private lateinit var _tracker: SelectionTracker<String>

    override val viewModel: SelectSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_select_songs

    override fun loadNavParams() = Unit

    override fun setViewListeners() {
        binding.selectSongsSave.setOnClickListener {
            if (_adapter.tracker!!.selection.size() == 0) {
                showSnackBar(R.string.select_songs_msg_select_at_least_one_song)
                return@setOnClickListener
            }

            setFragmentResult(SELECT_SONGS_FRAGMENT_KEY, bundleOf(SELECT_SONGS_FRAGMENT_RESULT to _adapter.tracker!!.selection.toList()))
            findNavController().popBackStack()
        }
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) { songs ->
            _adapter.loadItems(songs)
            stopShimmer()
        }
    }

    private fun configSearchView() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        binding.selectSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()

                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(Runnable {
                    filter?.let {
                        viewModel.query = it
                        viewModel.loadData()
                    }
                }, 700)

                return true
            }
        })
    }

    private fun startShimmer() {
        binding.selectSongsRecyclerview.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    private fun stopShimmer() {
        binding.selectSongsRecyclerview.visibility = View.VISIBLE
        binding.shimmerList.visibility = View.GONE
        binding.shimmerList.startShimmer()
    }

    override fun configView() {
        configRecyclerView()
        configSearchView()
        configSelectionTracker()

        binding.selectSongsSave.text = getString(R.string.select_amount_songs, 0)
    }

    private fun configSelectionTracker() {
        _tracker = SelectionTracker.Builder(
            "songSelection",
            binding.selectSongsRecyclerview,
            SongKeyProvider(_adapter),
            SongKeyLookup(binding.selectSongsRecyclerview),
            StorageStrategy.createStringStorage()
        ).build()

        _tracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                val count = _tracker.selection.size()

                binding.selectSongsSave.text = getString(R.string.select_amount_songs, count)
            }
        })

        _adapter.tracker = _tracker
    }

    private fun configRecyclerView() {
        createAdapter()

        binding.selectSongsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.selectSongsRecyclerview.setHasFixedSize(true)
        binding.selectSongsRecyclerview.adapter = _adapter
    }

    private fun createAdapter() {
        _adapter = SongsAdapter(
            BR.songItem,
            this
        )
    }

    override fun loadData() {
        startShimmer()
        viewModel.loadData()
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) = Unit

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) = Unit

    companion object {
        const val SELECT_SONGS_FRAGMENT_KEY = "NEW_PLAYLIST_FRAGMENT_KEY"
        const val SELECT_SONGS_FRAGMENT_RESULT = "NEW_PLAYLIST_FRAGMENT_RESULT"
    }
}