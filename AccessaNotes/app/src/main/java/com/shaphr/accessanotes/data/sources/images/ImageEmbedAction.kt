package com.shaphr.accessanotes.data.sources.images

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageEmbedAction @Inject constructor() : ImageAction {

    val imageFlow: MutableSharedFlow<Bitmap> = MutableSharedFlow()

    override suspend fun parseImage(image: Bitmap) {
        imageFlow.emit(image)
    }
}