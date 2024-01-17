package com.example.safediary

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.CLASSIFICATION_MODE_ALL
import com.google.mlkit.vision.face.FaceDetectorOptions.CONTOUR_MODE_ALL
import com.google.mlkit.vision.face.FaceDetectorOptions.LANDMARK_MODE_NONE
import com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE
import com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_FAST

class FaceAnalyzer(private val callBack : FaceAnalyzerCallback) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(LANDMARK_MODE_NONE)
        .setMinFaceSize(0.9f)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val bitmap = imageProxy.toBitmap()
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val newWidth = (originalWidth * 0.3).toInt()
        val newHeight = (originalHeight * 0.3).toInt()

        val left = (originalWidth - newWidth) / 2
        val top = (originalHeight - newHeight) / 2

        val croppedBitmap = Bitmap.createBitmap(bitmap, left, top, newWidth, newHeight)

        mediaImage?.let {
            val inputImage = InputImage.fromBitmap(croppedBitmap, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val fullSizeFaces = faces.filter { it.boundingBox.height() > 125 && it.boundingBox.width() > 125 }
                        if (fullSizeFaces.isNotEmpty()) {
                            callBack.processFace(faces)
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
}

interface FaceAnalyzerCallback {
    fun processFace(faces: List<Face>)
    fun errorFace(error: String)
}