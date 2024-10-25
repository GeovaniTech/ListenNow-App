package br.com.listennow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListSongsAdapter(songs: List<Song>, private val ctx: Context):
    RecyclerView.Adapter<ListSongsAdapter.ViewHolder>() {

    private val songs = songs.toMutableList()
    var onItemClick: ((Song) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]

        holder.bind(song)
    }

    fun update(songs: List<Song>) {
        notifyItemRangeRemoved(0, this.songs.size)
        this.songs.clear()
        this.songs.addAll(songs)
        notifyItemInserted(this.songs.size)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {

            val title: TextView = itemView.findViewById(R.id.list_songs_title)
            val artist: TextView = itemView.findViewById(R.id.list_songs_artist)
            val thumb: ImageView = itemView.findViewById(R.id.list_songs_thumb)

            val songSelected = songs[adapterPosition]

            title.text = song.name
            artist.text = song.artist

            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = ImageUtil.getBitmapImage(song.smallThumb, 120, 120, ctx)

                withContext(Dispatchers.Main) {
                    thumb.setImageBitmap(bitmap)
                }
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(songSelected)
            }
        }
    }
}