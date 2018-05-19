package pl.angelhackkrakow.pickmeup

import ai.api.android.AIConfiguration
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.ui.AIButton
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tts by lazy { TTS(this) }
    private val config by lazy {
        AIConfiguration(DIALOG_FLOW_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initMicButton()
        tts.speak("Hi Mike, how are you feeling today?")
    }

    private fun initMicButton() {
        micButton.initialize(config)
        micButton.setResultsListener(createMoodListener())
    }

    private fun createMoodListener(): MoodListener {
        return MoodListener { speech -> tts.speak(speech) }
                .apply {
                    onGoodMood = { proceedGoodMood() }
                    onOkMood = { proceedOkMood() }
                    onBadMood = { proceedBadMood() }
                }
    }

    private fun proceedBadMood() {

    }

    private fun proceedOkMood() {

    }

    private fun proceedGoodMood() {

    }

    class MoodListener(val speech: (String) -> Unit) : AIButton.AIButtonListener {

        var onGoodMood: () -> Unit = {}
        var onOkMood: () -> Unit = {}
        var onBadMood: () -> Unit = {}


        override fun onCancelled() {
        }

        override fun onResult(result: AIResponse) {
            speech(result.result.fulfillment.speech)
            processMood(Mood.from(result.result.action))
        }

        private fun processMood(mood: Mood) {
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

            fun from(value: String): Mood {
                return Mood.values()
                        .firstOrNull { mood -> mood.mood.equals(value, true) } ?: OK
            }
        }
    }

    companion object {
        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
    }
}
