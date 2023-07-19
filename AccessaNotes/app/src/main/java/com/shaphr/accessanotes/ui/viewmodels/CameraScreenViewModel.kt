package com.shaphr.accessanotes.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shaphr.accessanotes.data.sources.TextRecognizerSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    private val textRecognizerSource: TextRecognizerSource
) : ViewModel() {

    fun onPhotoTaken(bitmap: Bitmap) {
        viewModelScope.launch {
            textRecognizerSource.onImage(bitmap)
        }
    }
}