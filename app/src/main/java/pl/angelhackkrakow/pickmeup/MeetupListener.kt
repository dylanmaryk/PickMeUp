package pl.angelhackkrakow.pickmeup

import ai.api.model.AIResponse

class MeetupListener : SimplifiedAIListener() {

    var onYes : (query: String, response: String) -> Unit = {_, _ ->}
    var onNo : () -> Unit = {}

    override fun onResult(result: AIResponse) {
        when(YesNo.from(result.result.parameters.keys)) {
            YesNo.YES -> onYes(result.result.resolvedQuery, result.result.fulfillment.speech)
            YesNo.NO -> onNo()
        }
    }

    enum class YesNo(val answer: String) {
        YES("yesMeetup"), NO("noMeetup");

        companion object {
            fun from(params: Set<String>): YesNo {
                YesNo.values().forEach { e ->
                    if (params.any { e.answer.equals(it, true) }) return e
                }
                return YES
            }
        }
    }

}