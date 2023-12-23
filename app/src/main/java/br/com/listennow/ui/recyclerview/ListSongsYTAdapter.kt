package br.com.listennow.ui.recyclerview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.to.TOSongYTSearch
import br.com.listennow.utils.SongUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class ListSongsYTAdapter(private val ctx: Context, songs: List<TOSongYTSearch>): RecyclerView.Adapter<ListSongsYTAdapter.ViewHolder>() {
    private val songs = songs.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_search, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSong = songs[position]

        holder.bind(currentSong, ctx)
    }

    fun update(songs: List<TOSongYTSearch>) {
        this.songs.clear()
        this.songs.addAll(songs)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(song: TOSongYTSearch, context: Context) {
            val thumb: ImageView = itemView.findViewById(R.id.list_songs_search_thumb)
            val title: TextView = itemView.findViewById(R.id.list_songs_search_title)
            val artist: TextView = itemView.findViewById(R.id.list_songs_search_artist)

            title.text = song.title
            artist.text = song.artist
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val url = URL(song.smallThumb)
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
                Toast.makeText(context, R.string.download_started, Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val results = URL("https://api.devpree.com.br/listennow/download/${song.videoId}/Token").readText()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}