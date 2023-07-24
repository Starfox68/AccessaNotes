package com.shaphr.accessanotes.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.viewmodels.CameraScreenViewModel
import com.shaphr.accessanotes.ui.viewmodels.ImageOption

@Composable
fun CameraScreen(navController: NavHostController) {
    val viewModel: CameraScreenViewModel = hiltViewModel()

    if (viewModel.isImageTaken.value) {
        ImagePreviewScreen(
            selectedOptions = viewModel.selectedOptions.collectAsState().value,
            image = viewModel.image.collectAsState().value.asImageBitmap(),
            onOptionSelect = viewModel::onOptionSelect,
            onFinish = { viewModel.onFinish(navController) },
            isLoading = viewModel.isLoading.collectAsState().value
        )
    } else {
        CameraCaptureScreen(
            onPhotoTaken = viewModel::onPhotoTaken
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    onPhotoTaken: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val executor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(
                        executor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                Log.d("Camera Screen", "Capture Success")
                                val bitmap = image.toBitmap()
                                val rotation = image.imageInfo.rotationDegrees
                                val matrix = Matrix().apply {
                                    postRotate(-rotation.toFloat())
                                    postScale(-1f, -1f)
                                }

                                onPhotoTaken(
                                    Bitmap.createBitmap(
                                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                                    )
                                )
                                image.close()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Camera Screen", "Camera Capture error")
                            }
                        })
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.camera_icon), contentDescription = null)
            }
        }
    ) { paddingValues: PaddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            })
    }
}

@Composable
fun ImagePreviewScreen(
    selectedOptions: List<ImageOption>,
    image: ImageBitmap,
    onOptionSelect: (Boolean, ImageOption) -> Unit,
    onFinish: () -> Unit,
    isLoading: Boolean
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator()
        }
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .alpha(if (isLoading) 0.5f else 1.0f)
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(bitmap = image, contentDescription = null, modifier = Modifier.fillMaxWidth())
            Text(
                text = stringResource(R.string.camera_screen_description),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
            )
            Divider(modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            OptionCheckbox(
                isChecked = ImageOption.SAVE_IMAGE in selectedOptions,
                text = stringResource(id = R.string.camera_screen_image),
                onClick = { isChecked: Boolean ->
                    onOptionSelect(
                        isChecked,
                        ImageOption.SAVE_IMAGE
                    )
                }
            )
            Divider(modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            OptionCheckbox(
                isChecked = ImageOption.TRANSCRIPT in selectedOptions,
                text = stringResource(id = R.string.camera_screen_transcription),
                onClick = { isChecked: Boolean ->
                    onOptionSelect(
                        isChecked,
                        ImageOption.TRANSCRIPT
                    )
                }
            )
            Divider(modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            OptionCheckbox(
                isChecked = ImageOption.SUMMARY in selectedOptions,
                text = stringResource(id = R.string.camera_screen_summary),
                onClick = { isChecked: Boolean -> onOptionSelect(isChecked, ImageOption.SUMMARY) }
            )
            Divider(modifier = Modifier.padding(start = 8.dp, end = 8.dp))
            Button(onClick = onFinish, modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(id = R.string.camera_screen_finish))
            }
        }
    }
}

@Composable
fun OptionCheckbox(isChecked: Boolean, text: String, onClick: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .clickable { onClick(!isChecked) }
    ) {
        Checkbox(checked = isChecked, onCheckedChange = {})
        Text(text, modifier = Modifier)
    }
}

@Preview
@Composable
private fun PreviewCameraScreen() {
    CameraCaptureScreen(
        onPhotoTaken = {}
    )
}

@Preview
@Composable
private fun ImagePreviewScreen() {

}