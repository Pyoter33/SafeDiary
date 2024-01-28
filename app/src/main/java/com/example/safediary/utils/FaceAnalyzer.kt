package com.example.safediary.utils

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.LANDMARK_MODE_NONE
import com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE

class FaceAnalyzer(private val callBack: FaceAnalyzerCallback) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(LANDMARK_MODE_NONE)
        .setMinFaceSize(0.9f)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val bitmap = imageProxy.toBitmap()
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val newWidth = (originalWidth * IMAGE_SIZE_MODIFIER).toInt()
        val newHeight = (originalHeight * IMAGE_SIZE_MODIFIER).toInt()

        val left = (originalWidth - newWidth) / IMAGE_POSITION_MODIFIER
        val top = (originalHeight - newHeight) / IMAGE_POSITION_MODIFIER

        val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, newWidth, newHeight)

        mediaImage?.let {
            val inputImage =
                InputImage.fromBitmap(croppedBitmap, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val fullSizeFaces =
                            faces.filter {
                                it.boundingBox.height() > FACE_SIZE_RESTRICTION
                                        && it.boundingBox.width() > FACE_SIZE_RESTRICTION
                            }
                        if (fullSizeFaces.isNotEmpty()) {
                            callBack.processFace(bitmap)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener {
                    callBack.errorFace(it.message.orEmpty())
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    companion object {
        private const val IMAGE_SIZE_MODIFIER = 0.3
        private const val IMAGE_POSITION_MODIFIER = 2
        private const val FACE_SIZE_RESTRICTION = 125
    }
}

interface FaceAnalyzerCallback {
    fun processFace(detectedFace: Bitmap)
    fun errorFace(error: String)
}