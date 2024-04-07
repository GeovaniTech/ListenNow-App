package br.com.listennow.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.listennow.R
import java.net.URL

class ImageUtil {
    companion object {
        fun getBitmapImage(urlThumb: String, width: Int, height: Int, ctx: Context): Bitmap {
            try {
                val url = URL(urlThumb)
                val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                return Bitmap.createScaledBitmap(image, width, height, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val listenIcon = BitmapFactory.decodeResource(ctx.resources, R.drawable.icon)
            return Bitmap.createScaledBitmap(listenIcon, width, height, false)
        }
    }
}