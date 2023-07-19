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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.ui.viewmodels.CameraScreenViewModel

@Composable
fun CameraScreen() {
    val viewModel: CameraScreenViewModel = hiltViewModel()
    CameraScreenContent(
        onPhotoTaken = viewModel::onPhotoTaken
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
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
                Text(stringResource(R.string.camera_screen_take_picture))
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

@Preview
@Composable
private fun PreviewCameraScreen() {
    CameraScreenContent(
        onPhotoTaken = {}
    )
}

