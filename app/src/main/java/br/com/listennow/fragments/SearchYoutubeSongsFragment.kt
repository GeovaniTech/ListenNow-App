package br.com.listennow.fragments

import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.BR
import br.com.listennow.R
import br.com.listennow.adapter.IControllerItemsAdapter
import br.com.listennow.adapter.SearchYoutubeSongsAdapter
import br.com.listennow.databinding.FragmentSearchYoutubeSongsBinding
import br.com.listennow.databinding.FragmentSearchYoutubeSongsItemBinding
import br.com.listennow.utils.NotificationUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.viewmodel.SearchYoutubeSongsViewModel
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchYoutubeSongsFragment : CommonFragment<SearchYoutubeSongsViewModel, FragmentSearchYoutubeSongsBinding>(), IControllerItemsAdapter {
    private lateinit var adapter: SearchYoutubeSongsAdapter

    override val viewModel: SearchYoutubeSongsViewModel by viewModels()

    override fun getLayout(): Int = R.layout.fragment_search_youtube_songs

    private fun createAdapter() {
        adapter = SearchYoutubeSongsAdapter(
            variableId = BR.ytSong,
            this
        )
    }

    override fun loadNavParams() {
    }

    override fun setViewListeners() {
        configSearchSongs()

        mainActivity.binding.playBackButtons.setOnClickListener {
            if(SongUtil.actualSong != null && SongUtil.actualSong!!.videoId.isNotEmpty()) {
                findNavController().navigate(SearchYoutubeSongsFragmentDirections.actionSearchNewSongsFragmentSongDetailsFragment(SongUtil.actualSong!!.videoId))
            }
        }
    }

    private fun getNotificationBuilder(song: SearchYTSongResponse) =
        NotificationCompat.Builder(
            requireContext(),
            MainActivity.DOWNLOAD_SONG_NOTIFICATION_CHANNEl
        )
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle("${song.title} - ${song.artist}")
            .setContentText(getString(R.string.download_resquest_has_started))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

    private fun configSearchSongs() {
        val handlerThread = HandlerThread("Song Delay")
        handlerThread.start()
        val looper = handlerThread.looper
        val handler = Handler(looper)

        createAdapter()
        showSoftKeyboard(binding.searchYtSongs)

        binding.searchYtSongs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                startShimmer()
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(Runnable {
                    filter?.let {
                        viewModel.viewModelScope.launch {
                            viewModel.loadYoutubeSongs(it)
                        }
                    }
                }, 700)
                return true
            }
        })
    }

    private fun startShimmer() {
        binding.listSongsYT.visibility = View.GONE
        binding.fragmentSearchYoutubeSongsEmptyText.visibility = View.GONE
        binding.fragmentSearchYoutubeSongsEmptyImage.visibility = View.GONE
        binding.shimmerList.visibility = View.VISIBLE
        binding.shimmerList.startShimmer()
    }

    override fun setViewModelObservers() {
        viewModel.songs.observe(viewLifecycleOwner) {songs ->
            binding.shimmerList.stopShimmer()
            binding.shimmerList.visibility = View.GONE

            if (songs.isNullOrEmpty()) {
                binding.fragmentSearchYoutubeSongsEmptyImage.visibility = View.VISIBLE
                binding.fragmentSearchYoutubeSongsEmptyText.visibility = View.VISIBLE
                binding.listSongsYT.visibility = View.GONE
            } else {
                adapter.loadItems(songs)

                binding.fragmentSearchYoutubeSongsEmptyImage.visibility = View.GONE
                binding.fragmentSearchYoutubeSongsEmptyText.visibility = View.GONE
                binding.listSongsYT.visibility = View.VISIBLE
            }
        }
    }

    override fun loadData() {
        configRecyclerView()
    }

    private fun configRecyclerView() {
        val listSongs = binding.listSongsYT

        listSongs.layoutManager = LinearLayoutManager(requireContext())
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    override fun onViewItemClickListener(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {
        item as SearchYTSongResponse
        dataBinding as FragmentSearchYoutubeSongsItemBinding

        dataBinding.listSongsSearchSync.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.downloadSong(item.videoId)
            }

            showSnackBar(
                anchorView = mainActivity.binding.playBackButtons,
                messageId = R.string.download_started
            )

            var notificationBuilder = getNotificationBuilder(item)

            notificationBuilder.setProgress(0,  0, true)

            NotificationManagerCompat.from(requireContext()).apply {
                val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationId = NotificationUtil.getUniqueNotificationId()

                manager.notify(notificationId, notificationBuilder.build())

                Thread(Runnable {
                    val retries = 10
                    var attempts = 0

                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            while (attempts < retries) {
                                if (viewModel.songSynchronizedSuccessfully(item.videoId)) {
                                    notificationBuilder = getNotificationBuilder(item)
                                    notificationBuilder.setContentText(getString(R.string.download_completed))
                                    manager.notify(notificationId, notificationBuilder.build())
                                    break
                                }

                                attempts += 1
                                delay(5000)
                            }

                            if (attempts == retries) {
                                notificationBuilder = getNotificationBuilder(item)
                                notificationBuilder.setContentText(getString(R.string.download_failed))
                                manager.notify(notificationId, notificationBuilder.build())
                            }
                        }
                    }
                }).start()
            }
        }
    }

    override fun onChangeViewItem(
        view: View,
        position: Int,
        item: Any?,
        holder: RecyclerView.ViewHolder,
        dataBinding: ViewDataBinding
    ) {}
}