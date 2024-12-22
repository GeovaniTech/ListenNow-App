package br.com.listennow.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DataBindingViewHolder<T, B: ViewDataBinding>(
    val binding: B,
    private val variableId: Int
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: T) {
        binding.setVariable(variableId, item)
        binding.executePendingBindings()
    }
}