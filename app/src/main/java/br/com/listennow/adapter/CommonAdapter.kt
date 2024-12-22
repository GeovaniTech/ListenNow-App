package br.com.listennow.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class CommonAdapter<T, B: ViewDataBinding> (
    private val variableId: Int,
    private val itemsController: IControllerItemsAdapter
):  RecyclerView.Adapter<DataBindingViewHolder<T, B>>()  {
    private var items: List<T>? = null

    fun loadItems(items: List<T>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<T, B> {
        return DataBindingViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(
                parent.context
            ),
            viewType,
            parent,
            false
        ), variableId)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder<T, B>, position: Int) {
        val item = items?.get(position)

        item?.let {
            holder.bind(it)
            addListeners(holder.binding.root, position, item, holder, holder.binding)
        }
    }

    private fun addListeners(view: View, position: Int, item: Any?, holder: RecyclerView.ViewHolder, dataBinding: ViewDataBinding) {
        itemsController.onChangeViewItem(view, position, item, holder, dataBinding)
        itemsController.onViewItemClickListener(view, position, item, holder, dataBinding)
    }
}