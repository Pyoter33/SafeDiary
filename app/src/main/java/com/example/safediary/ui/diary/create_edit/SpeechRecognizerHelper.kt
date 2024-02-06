package com.example.safediary.ui.diary.create_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechRecognizerHelper(private val context: Context) {

    private lateinit var speechRecognizer: SpeechRecognizer

    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
    }

    fun createSpeechRecognizer(): Boolean {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            return false
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        return true
    }

    fun setActionListeners(
        onSpeechResult: (String) -> Unit,
        onSpeechEnd: () -> Unit
    ) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(p0: Float) {}

            override fun onBufferReceived(p0: ByteArray?) {}

            override fun onEndOfSpeech() {
                onSpeechEnd()
            }

            override fun onError(errorCode: Int) {
                onSpeechEnd()
            }

            override fun onResults(results: Bundle) {
                val result: ArrayList<String>? =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                result?.let {
                    onSpeechResult(it.toList().joinToString())
                }
            }

            override fun onPartialResults(p0: Bundle?) {}

            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
    }

    fun startListening() {
        speechRecognizer.startListening(recognizerIntent)
    }

}