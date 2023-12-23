package br.com.listennow.ui.recyclerview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil

class ListSongsAdapter(private val context: Context, songs: List<Song>):
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

        holder.bind(song, context)
    }

    fun update(songs: List<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song, context: Context) {
            val title: TextView = itemView.findViewById(R.id.list_songs_title)
            val artist: TextView = itemView.findViewById(R.id.list_songs_artist)
            val thumb: ImageView = itemView.findViewById(R.id.list_songs_thumb)

            title.text = song.name
            artist.text = song.artist

            if(song.largeThumbBytes != null) {
                val bmp = BitmapFactory.decodeByteArray(song.smallThumbBytes, 0, song.smallThumbBytes.size)
                if (bmp != null) {
                    thumb.setImageBitmap(Bitmap.createScaledBitmap(bmp, 120, 120, false))
                } else {
                    thumb.setImageResource(R.drawable.icon2)
                }
            } else {
                thumb.setImageResource(R.drawable.icon2)
            }

            itemView.setOnClickListener {
                onItemClick?.invoke(songs[adapterPosition])
            }
        }
    }
}