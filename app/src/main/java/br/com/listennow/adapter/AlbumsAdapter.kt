package br.com.listennow.adapter

import br.com.listennow.R
import br.com.listennow.databinding.FragmentAlbumsItemBinding
import br.com.listennow.decorator.AlbumItemDecorator

class AlbumsAdapter(
    variableId: Int,
    itemsController: IControllerItemsAdapter
) : CommonAdapter<AlbumItemDecorator, FragmentAlbumsItemBinding>(variableId, itemsController) {
    override fun getItemViewType(position: Int): Int = R.layout.fragment_albums_item
}