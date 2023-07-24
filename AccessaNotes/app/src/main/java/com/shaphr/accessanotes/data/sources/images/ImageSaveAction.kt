package com.shaphr.accessanotes.data.sources.images

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@ViewModelScoped
class ImageSaveAction @Inject constructor(
    @ApplicationContext private val context: Context
): ImageAction {

    override suspend fun parseImage(image: Bitmap) {
        val name = SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss a zzz", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Accessanotes")
        }

        var uri: Uri? = null
        val resolver = context.contentResolver
        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Failed to create Media Store")
            val stream = resolver.openOutputStream(uri) ?: throw IOException("Failed to create stream")

            if (!image.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                throw IOException("Failed to save image")
            }
        } catch (e: IOException) {
            uri?.let {
                resolver.delete(it, null, null)
            }
        }
    }
}