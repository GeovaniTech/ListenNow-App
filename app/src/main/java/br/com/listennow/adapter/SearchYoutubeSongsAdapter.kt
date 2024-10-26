package br.com.listennow.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class SearchYoutubeSongsAdapter(songs: List<SearchYTSongResponse>): RecyclerView.Adapter<SearchYoutubeSongsAdapter.ViewHolder>() {
    private val songs = songs.toMutableList()
    var onDownloadClicked: ((SearchYTSongResponse) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_search_youtube_songs_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = songs[position]

        holder.bind(currentSong)
    }

    fun update(songs: List<SearchYTSongResponse>) {
        notifyItemRangeRemoved(0, this.songs.size)
        this.songs.clear()
        this.songs.addAll(songs)
        notifyItemInserted(this.songs.size)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(song: SearchYTSongResponse) {
            val thumb: ImageView = itemView.findViewById(R.id.list_songs_search_thumb)
            val title: TextView = itemView.findViewById(R.id.list_songs_search_title)
            val artist: TextView = itemView.findViewById(R.id.list_songs_search_artist)

            title.text = song.title
            artist.text = song.artist
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val url = URL(song.thumb)
                    val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                    withContext(Dispatchers.Main) {
                        thumb.setImageBitmap(Bitmap.createScaledBitmap(image, 120, 120, false))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                thumb.setImageResource(R.drawable.icon)
            }

            val buttonDownload = itemView.findViewById<Button>(R.id.list_songs_search_sync)

            buttonDownload.setOnClickListener {
                onDownloadClicked?.invoke(song)
            }
        }
    }
}