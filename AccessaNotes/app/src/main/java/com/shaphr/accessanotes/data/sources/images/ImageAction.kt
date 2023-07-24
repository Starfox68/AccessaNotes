package com.shaphr.accessanotes.data.sources.images

import android.graphics.Bitmap

interface ImageAction {
    suspend fun parseImage(image: Bitmap)
}