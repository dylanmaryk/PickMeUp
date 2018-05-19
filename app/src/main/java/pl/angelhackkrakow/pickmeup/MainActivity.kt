package pl.angelhackkrakow.pickmeup

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tts by lazy { TTS(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1)
        viewAnimator.setInAnimation(this, android.R.anim.fade_in)
        viewAnimator.setOutAnimation(this, android.R.anim.fade_out)
        view.setOnClickListener {
            viewAnimator.showNext()
            val config = AIConfiguration(DIALOG_FLOW_TOKEN,
                    ai.api.AIConfiguration.SupportedLanguages.English,
                    AIConfiguration.RecognitionEngine.System)
            val aiService = AIService.getService(this, config)
            aiService.setListener(object : AIListener {
                override fun onResult(result: AIResponse?) {
                    val speech = result?.result?.fulfillment?.speech
                    speech?.let {
                        tts.speak(it)
                    }
                    val resolvedQuery = result?.result?.resolvedQuery
                    resolvedQuery?.let {
                        feeling_goo.text = it
                    }
                    viewAnimator.showNext()
                }

                override fun onListeningStarted() {
                }

                override fun onAudioLevel(level: Float) {
                }

                override fun onError(error: AIError?) {
                }

                override fun onListeningCanceled() {
                }

                override fun onListeningFinished() {
                }
            })
            aiService.startListening()
        }
    }

    companion object {
        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
    }
}
