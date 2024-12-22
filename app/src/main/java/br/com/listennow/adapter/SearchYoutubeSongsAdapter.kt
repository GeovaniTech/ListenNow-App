package br.com.listennow.adapter

import br.com.listennow.R
import br.com.listennow.databinding.FragmentSearchYoutubeSongsItemBinding
import br.com.listennow.webclient.song.model.SearchYTSongResponse


class SearchYoutubeSongsAdapter(
    variableId: Int,
    itemsController: IControllerItemsAdapter): CommonAdapter<SearchYTSongResponse, FragmentSearchYoutubeSongsItemBinding>(variableId, itemsController) {

    override fun getItemViewType(position: Int): Int = R.layout.fragment_search_youtube_songs_item
}