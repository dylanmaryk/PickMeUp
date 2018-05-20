package pl.angelhackkrakow.pickmeup

import ai.api.model.AIResponse
import android.util.Log
import com.google.gson.GsonBuilder

class MoodListener() : SimplifiedAIListener() {

    var onDidFinishListening: () -> Unit = {}
    var onGoodMood: (query: String, response: String) -> Unit = { _, _ -> }
    var onOkMood: (query: String, response: String) -> Unit = { _, _ -> }
    var onBadMood: (query: String, response: String) -> Unit = { _, _ -> }
    var onUnknown: (query: String, response: String) -> Unit = { _, _ -> }
    var onLonely: (query: String, response: String) -> Unit = { _, _ -> }

    private val gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }

    override fun onResult(result: AIResponse) {
        Log.d("onResult", gson.toJson(result))
        processMood(Mood.from(result.result.parameters.keys), result)
    }

    override fun onListeningFinished() {
        onDidFinishListening()
    }

    private fun query(result: AIResponse): String {
        return result.result.resolvedQuery
    }

    private fun response(result: AIResponse): String {
        return result.result.fulfillment.speech
    }

    private fun processMood(mood: Mood, result: AIResponse) {
        Log.d("Mood", mood.mood)
        val response = response(result)
        val query = query(result)
        when (mood) {
            Mood.BAD -> onBadMood(query, response)
            Mood.GOOD -> onGoodMood(query, response)
            Mood.LONLEY -> onLonely(query, response)
            else -> onUnknown(query, response)
        }
    }
}
