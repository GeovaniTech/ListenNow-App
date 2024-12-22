package br.com.listennow.adapter

import br.com.listennow.R
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.model.Song

class HomeSongsAdapter(
    variableId: Int,
    itemsController: IControllerItemsAdapter
): CommonAdapter<Song, FragmentSongItemBinding>(variableId, itemsController) {

    override fun getItemViewType(position: Int): Int = R.layout.fragment_song_item
}