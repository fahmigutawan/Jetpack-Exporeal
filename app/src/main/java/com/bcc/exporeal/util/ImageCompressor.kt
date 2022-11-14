package com.bcc.exporeal.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageCompressor {
    fun compress(context: Context, uri:Uri, quality: Int = 100):ByteArray{
        val input = context.getContentResolver().openInputStream(uri)
        val image = BitmapFactory.decodeStream(input , null, null)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        return baos.toByteArray()
    }
}