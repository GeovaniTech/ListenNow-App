package br.com.listennow.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.listennow.R

class ImageUtil {
    companion object {
        fun getBitmapImage(bytes: ByteArray, width: Int, height: Int): Bitmap {
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            return Bitmap.createScaledBitmap(bmp, width, height, false)
        }
    }
}