package br.com.listennow.adapter

import androidx.recyclerview.selection.SelectionTracker
import br.com.listennow.R
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.model.Song

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
}