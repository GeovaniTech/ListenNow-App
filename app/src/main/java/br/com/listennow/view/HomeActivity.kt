package br.com.listennow.view

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.listennow.R
import br.com.listennow.R.*
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.HomeBinding
import br.com.listennow.database.dao.SongDao
import br.com.listennow.model.Song
import br.com.listennow.ui.recyclerview.ListSongsAdapter
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.sql.DriverManager

class HomeActivity: AppCompatActivity() {
    private lateinit var binding: HomeBinding
    private lateinit var songDao: SongDao
    private lateinit var adapter: ListSongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        songDao = AppDatabase.getInstance(this).songDao()

        val songs = songDao.getSongs()

        adapter = ListSongsAdapter(songs, this)

        SongUtil.songs = songs

        if(SongUtil.songs.isNotEmpty()) {
            playRandomSong()
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
        SongUtil.onNextSong = {song ->
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
        val thumbDetails = binding.homeThumbSongDetails
        thumbDetails.setImageBitmap(ImageUtil.getBitmapImage(song.smallThumbBytes, 60, 60))
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
        intent.putExtra("song", song)
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
            binding.btnSyncSongs.isClickable = false
            syncSongs()
        }
    }

    override fun onResume() {
        updateSongs()
        super.onResume()
    }

    private fun syncSongs() {
        val jdbc = "jdbc:postgresql://186.232.152.13:5432/listennow"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Class.forName("org.postgresql.Driver") // Load the PostgreSQL driver class

                val connection = DriverManager.getConnection(jdbc, "postgres", "#Geovani5280")

                if (connection.isValid(0)) {
                    withContext(Dispatchers.Main) {
                        makeToast("Syncronism started")
                    }

                    val query = connection.prepareStatement("select * from song")

                    val result = query.executeQuery()

                    while (result.next()) {
                        val id = result.getInt("id")
                        val videoId = result.getString("videoid")
                        val title = result.getString("title")
                        val artist = result.getString("artist")
                        val album = result.getString("album")
                        val smallThumb = result.getString("small_thumb")
                        val largeThumb = result.getString("large_thumb")
                        val smallThumbBytes = result.getBytes("small_thumb_bytes")
                        val largeThumbBytes = result.getBytes("large_thumb_bytes")
                        val fileBytes = result.getBytes("file")
                        val path =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).path + "/" + title + ".mp3"

                        val fileOutputStream = FileOutputStream(path)
                        val lyrics = result.getString("lyrics")

                        fileOutputStream.write(fileBytes)
                        fileOutputStream.close()

                        val song = Song(
                            0,
                            videoId,
                            title,
                            artist,
                            album,
                            smallThumb,
                            largeThumb,
                            smallThumbBytes,
                            largeThumbBytes,
                            path,
                            lyrics
                        )

                        songDao.save(song)

                        val sql = StringBuilder("DELETE FROM song WHERE id = $id")

                        val q = connection.prepareStatement(sql.toString())
                        q.executeUpdate()
                    }
                }

                withContext(Dispatchers.Main) {
                    makeToast("Syncronism finished")
                    updateSongs()
                }

                connection.close()

                binding.btnSyncSongs.isClickable = true
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    makeToast("Failed to sync songs")
                }

                e.printStackTrace()
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun updateSongs() {
        adapter.update(songDao.getSongs())
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
            songDao.delete(song)
            updateSongs()

            if(SongUtil.actualSong.id == song.id) {
                playRandomSong()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to delete song", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}