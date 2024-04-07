package br.com.listennow.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.model.Playlist
import br.com.listennow.model.Song
import br.com.listennow.utils.ImageUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListPlaylistsAdapter(playlists: List<Playlist>, private val ctx: Context):
    RecyclerView.Adapter<ListPlaylistsAdapter.ViewHolder>() {

    private val playlists = playlists.toMutableList()
    var onItemClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_playlist, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]

        holder.bind(playlist)
    }

    fun update(playlists: List<Playlist>) {
        notifyItemRangeRemoved(0, this.playlists.size)
        this.playlists.clear()
        this.playlists.addAll(playlists)
        notifyItemInserted(this.playlists.size)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(playlist: Playlist) {

            val name: TextView = itemView.findViewById(R.id.playlist_name)
            name.text = playlist.name

            val playlistSelected = playlists[adapterPosition]

            itemView.setOnClickListener {
                onItemClick?.invoke(playlistSelected)
            }
        }
    }
}