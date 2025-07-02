package br.com.listennow.adapter

import br.com.listennow.R
import br.com.listennow.databinding.FragmentPlaylistsItemBinding
import br.com.listennow.decorator.PlaylistItemDecorator

class PlaylistsAdapter(
    variableId: Int,
    itemsController: IControllerItemsAdapter
): CommonAdapter<PlaylistItemDecorator, FragmentPlaylistsItemBinding>(variableId, itemsController) {
    override fun getItemViewType(position: Int): Int = R.layout.fragment_playlists_item
}