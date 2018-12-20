package com.piotrgluszek.announcementboard.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


class ImageConverter {
    companion object {
        fun fromBase64(base64: String): Bitmap {
            val decodedImage = Base64.decode(base64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
        }

        fun toBase64(bitmap: Bitmap): String {
            val out = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            val imageBytes = out.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }
}