package com.shaphr.accessanotes.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.data.sources.images.ImageAction
import com.shaphr.accessanotes.data.sources.images.ImageSaveAction
import com.shaphr.accessanotes.data.sources.images.ImageSummaryAction
import com.shaphr.accessanotes.data.sources.images.ImageTranscriptionAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraScreenViewModel @Inject constructor(
    imageTranscriptionSource: ImageTranscriptionAction,
    imageSummarySource: ImageSummaryAction,
    imageDirectSource: ImageSaveAction
) : ViewModel() {

    private val options: Map<ImageOption, ImageAction> = mapOf(
        ImageOption.TRANSCRIPT to imageTranscriptionSource,
        ImageOption.SUMMARY to imageSummarySource,
        ImageOption.SAVE_IMAGE to imageDirectSource
    )

    private val imageFlow: MutableStateFlow<Bitmap> =
        MutableStateFlow(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8))
    val image: StateFlow<Bitmap> = imageFlow

    private val isImageTakenState:  MutableState<Boolean> = mutableStateOf(false)
    val isImageTaken: State<Boolean> = isImageTakenState

    private val selectedOptionsFlow: MutableStateFlow<List<ImageOption>> = MutableStateFlow(emptyList())
    val selectedOptions: StateFlow<List<ImageOption>> = selectedOptionsFlow

    private val isLoadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = isLoadingFlow

    fun onPhotoTaken(bitmap: Bitmap) {
        viewModelScope.launch {
            imageFlow.emit(bitmap)
            isImageTakenState.value = true
        }
    }

    fun onOptionSelect(isChecked: Boolean, option: ImageOption) {
        if (isChecked) {
            selectedOptionsFlow.value = selectedOptionsFlow.value + option
        } else {
            selectedOptionsFlow.value = selectedOptionsFlow.value - option
        }
    }

    fun onFinish(navController: NavHostController) {
        viewModelScope.launch {
            isLoadingFlow.value = true
            selectedOptionsFlow.value.forEach {
                options[it]?.parseImage(image.value)
            }
            isLoadingFlow.value = false
            navController.popBackStack()
        }
    }
}

enum class ImageOption() {
    SAVE_IMAGE,
    TRANSCRIPT,
    SUMMARY,
}