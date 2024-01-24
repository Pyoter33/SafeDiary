package com.example.safediary.login.face_login

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.safediary.FaceAnalyzer
import com.example.safediary.FaceAnalyzerCallback
import com.example.safediary.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceLoginRegisterView(onEvent: (FaceLoginEvent) -> Unit, isRegister: Boolean = false) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.BLACK)
            scaleType = PreviewView.ScaleType.FILL_START
        }
    }
    val cameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
    val imageCapture = remember { ImageCapture.Builder().build() }
    val preview = androidx.camera.core.Preview.Builder().build()

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setImageQueueDepth(10)
        .build()
        .apply {
            setAnalyzer(ContextCompat.getMainExecutor(context), FaceAnalyzer(
                object : FaceAnalyzerCallback {
                    override fun processFace(detectedFace: Bitmap) {
                        if (isRegister) {
                            onEvent(RegisterFaceEvent(detectedFace))
                        } else {
                            onEvent(LoginWithFaceEvent(detectedFace))
                        }
                    }

                    override fun errorFace(error: String) {
                        Log.e("FaceLoginView", error)
                    }

                }
            ))
        }

    if (!permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        return
    }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalysis
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
        onEvent(WaitForPositionEvent)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                },
            onDraw = {
                drawRect(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f))
                drawCircle(
                    androidx.compose.ui.graphics.Color.Transparent,
                    style = Fill,
                    blendMode = BlendMode.Clear
                )
            })
        Text(
            text = stringResource(R.string.login_face_position),
            style = TextStyle(
                color = androidx.compose.ui.graphics.Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .padding(10.dp)
        )
        if (isRegister) {
            Text(
                text = stringResource(R.string.register_face_explanation),
                style = TextStyle(
                    color = androidx.compose.ui.graphics.Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .align(
                        Alignment.TopCenter
                    )
                    .padding(10.dp)
            )
        } else {
            IconButton(
                onClick = {
                    onEvent(BackClickedEvent)
                },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = androidx.compose.ui.graphics.Color.White,
                    contentDescription = null
                )
            }
        }
    }

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { future ->
            future.addListener(
                {
                    continuation.resume(future.get())
                }, ContextCompat.getMainExecutor(this)
            )
        }
    }
