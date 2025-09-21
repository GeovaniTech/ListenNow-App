package br.com.listennow.adapter

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import br.com.listennow.model.interfaces.IModelKey

class DataBindingViewHolder<T, B: ViewDataBinding>(
    val binding: B,
    private val variableId: Int
): RecyclerView.ViewHolder(binding.root) {
    lateinit var itemBindKey: String

    fun bind(item: T) {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }

    fun <T: IModelKey> bind(item: T, isActivated: Boolean) {
        itemBindKey = item.getModelKey()

        binding.setVariable(variableId, item)
        binding.executePendingBindings()
        binding.root.isActivated = isActivated

        if (isActivated) {
            itemView.setBackgroundColor(Color.parseColor("#228BE6")) // blue highlight
        } else {
            itemView.setBackgroundColor(Color.TRANSPARENT) // default
        }
    }
}