package br.com.listennow.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.listennow.R
import br.com.listennow.database.AppDatabase
import br.com.listennow.databinding.SongDetailsActivityBinding
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SongDetailsActivity : AppCompatActivity() {
    private lateinit var binding: SongDetailsActivityBinding
    private lateinit var song: Song

    private val songDao by lazy {
        AppDatabase.getInstance(this).songDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongDetailsActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val songId = intent.getStringExtra("songId")

        if(!songId.equals("")) {
            CoroutineScope(Dispatchers.IO).launch {
                song = songId?.let { songDao.findById(it) }!!
                bindSong(song)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_songs, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.song_detail_remove -> {
                try {
                    // Used to ensure that the variable is not null
                    if(::song.isInitialized) {
                        val file = File(song.path)
                        file.delete()

                        CoroutineScope(Dispatchers.IO).launch {
                            songDao.delete(song)
                        }
                        finish()
                        true
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to delete song", Toast.LENGTH_SHORT).show()
                    false
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindSong(song: Song) {
        val songName = binding.nameSongDetail
        val artist = binding.artistSongDetail
        val album = binding.albumSongDetail
        val lyrics = binding.lyricsSongDetail
        val thumb = binding.imgThumbSongDetail

        songName.text = song.name
        artist.text = song.artist
        album.text = song.album
        lyrics.text = song.lyrics

        thumb.setImageBitmap(ImageUtil.getBitmapImage(song.largeThumb, 120, 120, this))
    }
}