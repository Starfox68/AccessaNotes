package com.shaphr.accessanotes.data.sources.images

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.shaphr.accessanotes.data.repositories.LiveRecordingRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class ImageTranscriptionAction @Inject constructor(
    private val liveRecordingRepository: LiveRecordingRepository
): ImageAction {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun parseImage(image: Bitmap) {
        val inputImage = InputImage.fromBitmap(image, 0)
        val result = recognizer.process(inputImage).await()
        Log.d("TEST", "Parsing image")
        if (result.text.isNotBlank()) {
            Log.d("TEST", result.text)
            liveRecordingRepository.bareTranscriptionFlow.emit(result.text)
        }
    }
}