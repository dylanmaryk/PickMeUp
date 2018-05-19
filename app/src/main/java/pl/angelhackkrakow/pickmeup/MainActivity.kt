package pl.angelhackkrakow.pickmeup

import ai.api.android.AIConfiguration
import ai.api.android.AIService
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

    private var displayView: Int
        get() = viewAnimator.displayedChild
        set(value) {
            viewAnimator.displayedChild = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tts.init(INIT_SPEECH, {
            runOnUiThread {
                displayView = LISTENING
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
        displayView = WHAT_WAS_SAID
        whatWeSaid.text = speech
    }

    private fun proceedBadMood() {
        displayView = BAD_MOOD
    }

    private fun proceedOkMood() {
        displayView = OK_MOOD
    }

    private fun proceedGoodMood() {
        displayView = GOOD_MOOD
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
        const val OK_MOOD = 3
        const val GOOD_MOOD = 4
        const val BAD_MOOD = 5

        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
        const val INIT_SPEECH = "Hi Mike. How are you feeling today?"
    }
}
