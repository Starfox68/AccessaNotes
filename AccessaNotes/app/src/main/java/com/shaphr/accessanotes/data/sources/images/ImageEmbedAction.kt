package com.shaphr.accessanotes.data.sources.images

import android.graphics.Bitmap
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageEmbedAction @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository
) : ImageAction {

    override suspend fun parseImage(image: Bitmap) {
        liveRecordingRepository.imageFlow.emit(image)
    }
}