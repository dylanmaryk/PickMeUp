package pl.angelhackkrakow.pickmeup

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val aiService by lazy {
        AIService.getService(this, config)
    }

    private val config by lazy {
        AIConfiguration(DIALOG_FLOW_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System)
    }
    private val tts by lazy { TTS(this) }

    private var displayedView: Int
        get() = viewAnimator.displayedChild
        set(value) {
            viewAnimator.displayedChild = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts.init(INIT_SPEECH, {
            runOnUiThread {
                displayedView = LISTENING
                startListening()
            }
        })
    }

    private fun startListening() {
        aiService.setListener(createMoodListener())
        aiService.startListening()
    }

    private fun createMoodListener(): MoodListener {
        return MoodListener(
                { wasSaid -> showWhatSaid(wasSaid) },
                { sayThis -> tts.speak(sayThis) })
                .apply {
                    onGoodMood = { proceedGoodMood() }
                    onOkMood = { proceedOkMood() }
                    onBadMood = { proceedBadMood() }
                }
    }

    private fun showWhatSaid(speech: String) {
        displayedView = WHAT_WAS_SAID
        whatWeSaid.text = speech
    }

    private fun proceedBadMood() {
    }

    private fun proceedOkMood() {
    }

    private fun proceedGoodMood() {

    }

    class MoodListener(
            val saidThis: (String) -> Unit,
            val sayThis: (String) -> Unit

    ) : AIListener {
        override fun onListeningStarted() {
            /*empty*/
        }

        override fun onAudioLevel(level: Float) {
            /*empty*/
        }

        override fun onListeningCanceled() {
            /*empty*/
        }

        override fun onListeningFinished() {
            /*empty*/
        }

        var onGoodMood: () -> Unit = {}
        var onOkMood: () -> Unit = {}
        var onBadMood: () -> Unit = {}

        private val gson by lazy {
            GsonBuilder().setPrettyPrinting().create()
        }

        override fun onResult(result: AIResponse) {
//            Log.d("onResult", gson.toJson(result))
            saidThis(result.result.resolvedQuery)
            sayThis(result.result.fulfillment.speech)
            processMood(Mood.from(result.result.parameters.keys))
        }

        private fun processMood(mood: Mood) {
            Log.d("Mood", mood.mood)
            when (mood) {
                Mood.BAD -> onBadMood()
                Mood.OK -> onOkMood()
                Mood.GOOD -> onGoodMood()
            }
        }

        override fun onError(error: AIError?) {
        }
    }

    enum class Mood(val mood: String) {
        BAD("BadMood"), OK("OkMood"), GOOD("GoodMood");

        companion object {

            fun from(params: Set<String>): Mood {
                Mood.values().forEach { mood ->
                    if (params.any { it == mood.mood }) {
                        return mood
                    }
                }
                return Mood.OK
            }
        }
    }

    companion object {
        const val HI_MIKE = 0
        const val LISTENING = 1
        const val WHAT_WAS_SAID = 2

        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
        const val INIT_SPEECH = "Hi Mike. How are you feeling today?"
    }
}
