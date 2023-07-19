package com.shaphr.accessanotes.data.sources

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextRecognizerSource @Inject constructor() {

    val imageTextFlow: MutableSharedFlow<String> = MutableSharedFlow()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun onImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val result = recognizer.process(image).await()
        if (result.text.isNotBlank()) {
            Log.d(TAG, result.text)
            imageTextFlow.emit(result.text)
        }
    }

    companion object {
        val TAG = TextRecognizerSource::class.simpleName
    }
}