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

    private val gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }

    override fun onResult(result: AIResponse) {
//            Log.d("onResult", gson.toJson(result))
        saidThis(result.result.resolvedQuery)
        sayThis(result.result.fulfillment.speech)
        processMood(MainActivity.Mood.from(result.result.parameters.keys))
    }

    private fun processMood(mood: MainActivity.Mood) {
        Log.d("Mood", mood.mood)
        when (mood) {
            MainActivity.Mood.BAD -> onBadMood()
            MainActivity.Mood.OK -> onOkMood()
            MainActivity.Mood.GOOD -> onGoodMood()
        }
    }
}
