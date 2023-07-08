package com.shaphr.accessanotes

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechClient @Inject constructor(@ApplicationContext private val context: Context) {
    var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                println("Created TTS engine")
            } else {
                println("TTS init failed, status $it")
            }
        }
    }

    fun speak(text: String) {
        val tts = tts
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

        if (tts == null || !tts.isSpeaking) {
            println("TTS failed, not speaking")
        } else {
            println("Speaking")
        }
    }
}