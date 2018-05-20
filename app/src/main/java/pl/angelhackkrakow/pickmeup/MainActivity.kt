package pl.angelhackkrakow.pickmeup

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val aiService by lazy {
        AIService.getService(this, config)
    }

    private fun createMoodListener(): MoodListener {
        return MoodListener()
                .apply {
                    onGoodMood = { query, response -> proceedGoodMood(query, response) }
                    onOkMood = { query, response -> proceedOkMood(query, response) }
                    onBadMood = { query, response -> proceedBadMood(query, response) }
                    onLonely = { query, response -> proceedLonely(query, response) }
                    onUnknown = { query, response -> listenAgain() }
                }
    }

    private fun proceedLonely(query: String, response: String) {
        displayView = LONELY_MOOD
        tts.speak(response, Runnable {
            aiService.stopListening()
            aiService.startListening()
            Log.d("proceedLonely", "on Done speaking  lonely")
        })
    }

    private fun proceedYesNoMeetup() {
        displayView = MEETUP_TYPE
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
        tts.init(INIT_SPEECH, this::onReady, this::onPostSpeech)
    }

    private fun onReady() {
        displayView = HI_MIKE
    }

    private fun onPostSpeech() {
        runOnUiThread {
            displayView = LISTENING
            startListeningForMood()
        }
    }

    private fun startListeningForMood() {
        aiService.setListener(createMoodListener())
        aiService.startListening()
    }

    private fun listenAgain() {
        displayView = LISTENING
        aiService.startListening()
    }

    private fun showWhatSaid(speech: String) {
        whatWeSaid.text = speech
    }

    private fun proceedBadMood(query: String, response: String) {
        displayView = BAD_MOOD
        tts.speak(response, Runnable {
            aiService.setListener(createGoodMoodListener())
            aiService.startListening()
        })
    }

    private fun proceedOkMood(query: String, response: String) {
        proceedGoodMood(query, response)
    }

    private fun proceedGoodMood(query: String, response: String) {
        displayView = GOOD_MOOD
        tts.speak(response, Runnable {
            aiService.setListener(createGoodMoodListener())
            aiService.startListening()
        })
    }

    private fun createGoodMoodListener(): AIListener {
        return GoodMoodListener(
                this::runSpotify, this::callFamily)
    }

    private fun runSpotify() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(NEISTAT_BANGERS)))
    }

    @SuppressLint("MissingPermission")
    private fun callFamily() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "666-666-666"))
        startActivity(intent)
    }

    fun launchMeetup(keyword: String) {
        startActivity(
                Intent(Intent.ACTION_VIEW, createMeetupUri(keyword))
        )
    }

    fun createMeetupUri(keyword: String): Uri {
        return Uri.parse(
                "https://www.meetup.com/find/events/?allMeetups=false&keywords=$keyword&radius=100&userFreeform=Warsaw%2C+Poland&mcId=z1032106&mcName=Warsaw%2C+PL&eventFilter=mysugg"
        )
    }

    companion object {
        const val HI_MIKE = 0
        const val LISTENING = 1
        const val WHAT_WAS_SAID = 2
        const val OK_MOOD = 3
        const val GOOD_MOOD = 4
        const val BAD_MOOD = 5
        const val LONELY_MOOD = 6
        const val MEETUP_TYPE = 7

        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
        const val NEISTAT_BANGERS = "spotify:user:1244785970:playlist:0YybZd87fuKnKxP5DloOsx"
        const val INIT_SPEECH = "Hi Mike. How are you feeling today?"
    }
}
