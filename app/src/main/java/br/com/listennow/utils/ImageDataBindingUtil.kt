package br.com.listennow.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import br.com.listennow.R

@BindingAdapter("bindImage")
fun loadImage(view: View, url: String?) {
    val image = view as ImageView

    url?.let {
        val bitmap = ImageUtil.getBitmapImage(url, 60, 60,)
        image.setImageBitmap(bitmap)

        return
    }

    image.setImageResource(R.drawable.icon)
}