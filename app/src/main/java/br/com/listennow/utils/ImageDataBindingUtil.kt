package br.com.listennow.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import br.com.listennow.R
import com.bumptech.glide.Glide

@BindingAdapter("bindImage")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(url)
            .error(R.drawable.icon)
            .into(view)
    } else {
        view.setImageResource(R.drawable.icon)
    }
}