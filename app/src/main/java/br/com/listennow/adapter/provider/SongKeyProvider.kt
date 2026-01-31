package br.com.listennow.adapter.provider

import androidx.recyclerview.selection.ItemKeyProvider
import br.com.listennow.adapter.SongsAdapter

class SongKeyProvider(private val adapter: SongsAdapter) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String {
        return adapter.items!![position].videoId
    }

    override fun getPosition(key: String): Int {
       return adapter.items!!.indexOfFirst { it.videoId == key }
    }

}