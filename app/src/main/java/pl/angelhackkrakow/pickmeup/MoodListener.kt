package pl.angelhackkrakow.pickmeup

import ai.api.model.AIResponse
import android.util.Log
import com.google.gson.GsonBuilder

class MoodListener(
        val saidThis: (String) -> Unit,
        val sayThis: (String) -> Unit

) : SimplifiedAIListener() {

    var onGoodMood: () -> Unit = {}
    var onOkMood: () -> Unit = {}
    var onBadMood: () -> Unit = {}
    var onUnknown: () -> Unit = {}

    private val gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }

    override fun onResult(result: AIResponse) {
//            Log.d("onResult", gson.toJson(result))
        saidThis(result.result.resolvedQuery)
        sayThis(result.result.fulfillment.speech)
        Thread.sleep(1000)
        processMood(Mood.from(result.result.parameters.keys))
    }

    override fun onListeningFinished() {
        super.onListeningFinished()
        Log.d("finished listening", "finished")
    }

    private fun processMood(mood: Mood) {
        Log.d("Mood", mood.mood)
        when (mood) {
            Mood.BAD -> onBadMood()
            Mood.GOOD -> onGoodMood()
            else -> onUnknown()
        }
    }
}
