package pl.angelhackkrakow.pickmeup

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

class TTS(private val context: Context) {

    private lateinit var textToSpeech: TextToSpeech
    fun init(
            speech: String,
            onReady: (() -> Unit) = {},
            onPostSpeech: () -> Unit = {}
    ) {
        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
            onReady()
            speak(speech, onPostSpeech)
        })
    }


    @JvmOverloads fun speak(text: String, onDone: (() -> Unit) = {}) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, Bundle.EMPTY, UUID.randomUUID().toString())
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(s: String) {
                /**/
            }

            override fun onDone(s: String) {
                onDone.invoke()
            }

            override fun onError(s: String) {
                /**/
            }
        })
    }
}