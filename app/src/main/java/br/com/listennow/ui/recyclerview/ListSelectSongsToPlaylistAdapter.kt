package br.com.listennow.ui.recyclerview

import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.R
import br.com.listennow.to.TOSong

class ListSelectSongsToPlaylistAdapter(songs: List<TOSong>, private val ctx: Context):
    RecyclerView.Adapter<ListSelectSongsToPlaylistAdapter.ViewHolder>() {

    private val songs = songs.toMutableList()
    var checkBoxStateArray = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_select_song, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        val checkbox = holder.itemView.findViewById<CheckBox>(R.id.select_song)

        checkbox.isChecked = checkBoxStateArray.get(position, false)
        song.selected = checkbox.isChecked

        holder.bind(song)
    }

    fun update(songs: List<TOSong>) {
        notifyItemRangeRemoved(0, this.songs.size)
        this.songs.clear()
        this.songs.addAll(songs)
        notifyItemInserted(this.songs.size)
    }

    fun getSelectedSongs(): List<TOSong> {
        val selectedSongs = mutableListOf<TOSong>()
        for ((index, song) in songs.withIndex()) {
            if (checkBoxStateArray[index]) {
                selectedSongs.add(song)
            }
        }

        return selectedSongs
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(song: TOSong) {
            val checkbox: CheckBox = itemView.findViewById(R.id.select_song)
            checkbox.text = song.name

            checkbox.setOnClickListener {
                if(!checkBoxStateArray.get(adapterPosition, false)) {
                    checkbox.isChecked = true
                    checkBoxStateArray.put(adapterPosition, true)
                } else {
                    checkbox.isChecked = false
                    checkBoxStateArray.put(adapterPosition, false)
                }
            }

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                song.selected = isChecked
            }
        }
    }
}