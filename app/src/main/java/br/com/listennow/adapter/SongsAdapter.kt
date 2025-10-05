package br.com.listennow.adapter

import androidx.recyclerview.selection.SelectionTracker
import br.com.listennow.R
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.model.Song
import br.com.listennow.utils.SongUtil

class SongsAdapter(
    variableId: Int,
    itemsController: IControllerItemsAdapter
): CommonAdapter<Song, FragmentSongItemBinding>(variableId, itemsController) {

    var tracker: SelectionTracker<String>? = null

    override fun getItemViewType(position: Int): Int = R.layout.fragment_song_item


    override fun onBindViewHolder(
        holder: DataBindingViewHolder<Song, FragmentSongItemBinding>,
        position: Int
    ) {
        val item = items?.get(position)

        item?.let {
            holder.bind(it, tracker?.isSelected(item.videoId) == true)
            addListeners(holder.binding.root, position, item, holder, holder.binding)
        }
    }

    fun removeAt(position: Int) {
        val updatedSongs = items!!.toMutableList()
        val deletedSong = updatedSongs[position]

        updatedSongs.removeAt(position)

        items = updatedSongs
        SongUtil.songs = updatedSongs

        SongUtil.actualSong?.let {
            if (it.videoId == deletedSong.videoId) {
                if (SongUtil.songs.isNotEmpty()) {
                    SongUtil.playRandomSong()
                } else {
                    SongUtil.actualSong = null
                    SongUtil.clear()
                }
            }
        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items!!.size)
    }
}