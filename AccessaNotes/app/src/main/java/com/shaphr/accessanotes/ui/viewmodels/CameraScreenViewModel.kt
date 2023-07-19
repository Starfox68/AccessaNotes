package com.shaphr.accessanotes.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.data.sources.TextRecognizerSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val textRecognizerSource: TextRecognizerSource
) : ViewModel() {

    private val imageFlow: MutableStateFlow<Bitmap> =
        MutableStateFlow(Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8))
    val image: StateFlow<Bitmap> = imageFlow

    private val isImageTakenFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isImageTaken: StateFlow<Boolean> = isImageTakenFlow

    fun onPhotoTaken(bitmap: Bitmap) {
        viewModelScope.launch {
            imageFlow.emit(bitmap)
            isImageTakenFlow.emit(true)
        }
    }

    fun onImageChoice(option: ImageOption) {

    }
}

enum class ImageOption() {
    IMAGE,
    TRANSCRIPT,
    NOTES,
    DIAGRAM
}