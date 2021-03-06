package pl.angelhackkrakow.pickmeup

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener

class TTS(
        private val context: Context
) {


    private var textToSpeech: TextToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener { })


    @JvmOverloads fun speak(text: String, onDone: Runnable? = null) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(s: String) {

            }

            override fun onDone(s: String) {
                onDone?.run()
            }

            override fun onError(s: String) {

            }
        })
    }
}