package br.com.listennow.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.R.drawable
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.SongDao
import br.com.listennow.databinding.HomeBinding
import br.com.listennow.model.Song
import br.com.listennow.ui.activity.AbstractUserActivity
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.io.File

class HomeActivity: AbstractUserActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var songDao: SongDao
    private lateinit var adapter: ListSongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        songDao = AppDatabase.getInstance(this).songDao()
        adapter = ListSongsAdapter(emptyList(), this)

        lifecycleScope.launch {
            user.filterNotNull().collect {
                updateSongs()
            }

            withContext(Dispatchers.Main) {
                if (SongUtil.songs.isNotEmpty()) {
                    playRandomSong()
                }
            }
        }

        configButtonShuffle()
        configRecyclerSongs()
        configButtonSync()
        configLinkRegister()
        configButtonPlayPause()
        configButtonNext()
        configSongsDetails()
        configThumbClick()
        configRemoveSongFromRecyclerMenu()
        configOnNextSongAutomatically()
        configOpenSongDetailsFromRecycler()
    }

    private fun configOnNextSongAutomatically() {
        SongUtil.onNextSong = { song ->
            SongUtil.readSong(this, song)
            configButtonToPause()
            configThumbDetails(song)
        }
    }

    private fun configThumbClick() {
        val cardThumb = binding.homeCardViewSongDetails

        cardThumb.setOnClickListener {
            openSongDetails(SongUtil.actualSong)
        }
    }

    private fun playRandomSong() {
        val song = getRandomSong()

        SongUtil.readSong(this, song)
        configButtonToPause()
        configThumbDetails(song)
    }

    private fun getRandomSong(): Song {
        val position = (0 until (SongUtil.songs.size)).random()
        return SongUtil.songs[position]
    }

    private fun configButtonToPause() {
        binding.play.setBackgroundResource(drawable.ic_pause)
    }

    private fun configThumbDetails(song: Song) {
        runOnUiThread {
            val thumbDetails = binding.homeThumbSongDetails
            thumbDetails.setImageBitmap(ImageUtil.getBitmapImage(song.smallThumbBytes, 60, 60))
        }
    }

    private fun configSongsDetails() {
        adapter.onItemClick = { song ->
            try {
                SongUtil.readSong(this, song)
                configButtonToPause()
                configThumbDetails(song)
            } catch (e: Exception) {
                e.printStackTrace()
                onRemoveSong(song)
            }
        }
    }

    private fun configOpenSongDetailsFromRecycler() {
        adapter.onDetailsSongsClick =  { song ->
            openSongDetails(song)
        }
    }

    private fun configRemoveSongFromRecyclerMenu() {
        adapter.onDeleteItemClick = { song ->
            onRemoveSong(song)
        }
    }

    private fun openSongDetails(song: Song) {
        val intent = Intent(this, SongDetailsActivity::class.java)
        intent.putExtra("songId", song.id)
        startActivity(intent)
    }

    private fun configButtonShuffle() {
    }

    private fun configButtonNext() {
        binding.next.setOnClickListener {
            playRandomSong()
        }
    }

    private fun configButtonPlayPause() {
        binding.play.setOnClickListener {
            if (SongUtil.isPlaying()) {
                SongUtil.pause()
                binding.play.setBackgroundResource(drawable.ic_play)
            } else {
                SongUtil.play()
                binding.play.setBackgroundResource(drawable.ic_pause)
            }
        }
    }

    private fun configLinkRegister() {
        binding.search.setOnClickListener {
            startRegisterActivity()
        }
    }

    private fun startRegisterActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun configButtonSync() {
        binding.btnSyncSongs.setOnClickListener {
        }
    }

    override fun onResume() {
        lifecycleScope.launch {
            updateSongs()
        }
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu?.findItem(R.id.search_songs)

        if(searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {

                    if(p0!!.isNotEmpty()) {
                        lifecycleScope.launch {
                            songDao.listByFilters(p0).collect { songs ->
                                updateSongs(songs)
                            }
                        }
                    } else {
                        lifecycleScope.launch {
                            updateSongs()
                        }
                    }

                    return true
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.btn_go_profile -> {
                startProfileActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private suspend fun updateSongs() {
        songDao.getSongs().collect { songs ->
            SongUtil.songs = songs
            updateSongs(songs)
        }
    }

    private suspend fun updateSongs(songs: List<Song>) {
        runOnUiThread {
            adapter.update(songs)
        }
    }

    private fun configRecyclerSongs() {
        val listSongs = binding.songs

        listSongs.layoutManager = LinearLayoutManager(this)
        listSongs.setHasFixedSize(true)
        listSongs.adapter = adapter
    }

    private fun onRemoveSong(song: Song) {
        try {
            val file = File(song.path)
            file.delete()

            lifecycleScope.launch {
                songDao.delete(song)
                updateSongs()
            }

            if(SongUtil.actualSong.id == song.id) {
                playRandomSong()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to delete song", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}