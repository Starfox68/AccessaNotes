package com.shaphr.accessanotes

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechClient @Inject constructor(@ApplicationContext private val context: Context) {
    // Class should be created with engine before use
    private var tts: TextToSpeech = TextToSpeech(context) {
        if (it == TextToSpeech.SUCCESS) {
            println("Created TTS engine")
        } else {
            println("TTS init failed, status $it")
        }
    }

    init {
        tts.language = Locale.ENGLISH
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

        if (tts.isSpeaking) {
            println("TTS speaking: $text")
        } else {
            println("TTS failed, not speaking")
        }
    }

    fun stop() {
        println("TTS stopped speaking")
        tts.stop()
    }
}
