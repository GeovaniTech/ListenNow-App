package br.com.listennow.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

interface IControllerItemsAdapter {
    fun onViewItemClickListener(view: View, position: Int, item: Any?, holder: RecyclerView.ViewHolder, dataBinding: ViewDataBinding)
    fun onChangeViewItem(view: View, position: Int, item: Any?, holder: RecyclerView.ViewHolder, dataBinding: ViewDataBinding)
}