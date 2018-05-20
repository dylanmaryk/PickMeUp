package pl.angelhackkrakow.pickmeup

import ai.api.AIListener
import ai.api.model.AIError

abstract class SimplifiedAIListener : AIListener {
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

    override fun onError(error: AIError?) {
    }
}