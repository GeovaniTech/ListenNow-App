package br.com.listennow.ui.recyclerview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import br.com.listennow.utils.SongUtil
import br.com.listennow.view.SongDetailsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListSongsAdapter(songs: List<Song>, private val ctx: Context):
    RecyclerView.Adapter<ListSongsAdapter.ViewHolder>() {

    private val songs = songs.toMutableList()
    var onItemClick: ((Song) -> Unit)? = null
    var onDeleteItemClick: ((Song) -> Unit)? = null
    var onDetailsSongsClick: ((Song) -> Unit)? = null

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

    private fun popupMenu(view: View, song: Song) {
        val popupMenu = PopupMenu(ctx, view)
        popupMenu.inflate(R.menu.menu_song_options_recycler)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.remove_song -> {
                    onDeleteItemClick?.invoke(song)
                    true
                }

                R.id.details_song -> {
                    onDetailsSongsClick?.invoke(song)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
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
            val menu: Button = itemView.findViewById(R.id.btn_popup_menu)

            val songSelected = songs[adapterPosition]

            menu.setOnClickListener {
                popupMenu(itemView, songSelected)
            }

            title.text = song.name
            artist.text = song.artist

            thumb.setImageBitmap(ImageUtil.getBitmapImage(song.smallThumb, 60, 60, ctx))

            itemView.setOnClickListener {
                onItemClick?.invoke(songSelected)
            }
        }
    }
}