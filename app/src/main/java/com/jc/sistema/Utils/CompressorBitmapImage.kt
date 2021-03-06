package com.jc.sistema.Utils

import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class CompressorBitmapImage {
    fun getImage(ctx: Context?, path: String?, width: Int, height: Int): ByteArray? {
        val file_thumb_path = File(path)
        var thumb_bitmap: Bitmap? = null
        try {
            thumb_bitmap = Compressor(ctx)
                .setMaxWidth(width)
                .setMaxHeight(height)
                .setQuality(75)
                .compressToBitmap(file_thumb_path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val baos = ByteArrayOutputStream()
        thumb_bitmap!!.compress(Bitmap.CompressFormat.JPEG, 30, baos)
        return baos.toByteArray()
    }
}