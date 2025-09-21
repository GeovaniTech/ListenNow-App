package br.com.listennow.adapter.lookup

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.adapter.DataBindingViewHolder
import br.com.listennow.databinding.FragmentSongItemBinding
import br.com.listennow.model.Song

class SongKeyLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y) ?: return  null
        val holder = recyclerView.getChildViewHolder(view) as DataBindingViewHolder<Song, FragmentSongItemBinding>

        return object : ItemDetails<String>() {
            override fun getPosition(): Int {
                return holder.bindingAdapterPosition
            }

            override fun getSelectionKey(): String {
                return holder.itemBindKey
            }

            override fun inSelectionHotspot(e: MotionEvent?): Boolean {
                return true
            }
        }
    }
}