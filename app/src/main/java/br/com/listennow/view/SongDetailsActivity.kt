package br.com.listennow.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.listennow.R
import br.com.listennow.databinding.SongDetailsActivityBinding
import br.com.listennow.model.Song

class SongDetailsActivity : AppCompatActivity() {
    private lateinit var binding: SongDetailsActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SongDetailsActivityBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // Geovani, don't forgot to change the Entity to use Parceble.
        val song = intent.getSerializableExtra("song") as Song

        bindSong(song)
    }

    fun bindSong(song: Song) {
        val songName = findViewById<TextView>(R.id.name_song_detail)
        val artist = findViewById<TextView>(R.id.artist_song_detail)
        val album = findViewById<TextView>(R.id.album_song_detail)
        val lyrics = findViewById<TextView>(R.id.lyrics_song_detail)
        val thumb = findViewById<ImageView>(R.id.img_thumb_song_detail)

        songName.text = song.name
        artist.text = song.artist
        album.text = song.album
        lyrics.text = song.lyrics

        if(song.largeThumbBytes != null) {
            val bmp = BitmapFactory.decodeByteArray(song.largeThumbBytes, 0, song.largeThumbBytes.size)
            if (bmp != null) {
                thumb.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 120, false))
            } else {
                thumb.setImageResource(R.drawable.icon2)
            }
        } else {
            thumb.setImageResource(R.drawable.icon2)
        }
    }
}