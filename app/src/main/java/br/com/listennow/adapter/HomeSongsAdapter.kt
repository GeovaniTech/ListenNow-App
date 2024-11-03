package br.com.listennow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeSongsAdapter(songs: List<Song>, private val ctx: Context):
    PagingDataAdapter<Song, HomeSongsAdapter.ViewHolder>(differCallback) {

    private val songs = songs.toMutableList()
    var onItemClick: ((Song) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_song_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setIsRecyclable(false)
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

            title.text = song.name
            artist.text = song.artist

            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = ImageUtil.getBitmapImage(song.smallThumb, 120, 120, ctx)

                withContext(Dispatchers.Main) {
                    thumb.setImageBitmap(bitmap)
                }
            }

            itemView.isSelected = true

            itemView.setOnClickListener {
                onItemClick?.invoke(song)
            }
        }
    }

    companion object {
         private val differCallback = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.songId == newItem.songId
            }
        }
    }
}