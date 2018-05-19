package pl.angelhackkrakow.pickmeup

import ai.api.model.AIResponse

class GoodMoodListener(
        val spotify: () -> Unit,
        val callFamily: () -> Unit
) : SimplifiedAIListener() {
    override fun onResult(result: AIResponse) {
        val option = GoodMoodOptions.from(result.result.parameters.keys)
        when (option) {
            GoodMoodOptions.SPOTIFY -> spotify()
            GoodMoodOptions.CALL_FAMILY -> callFamily()
        }
    }

    enum class GoodMoodOptions(val option: String) {
        SPOTIFY("playSpotifyGoodMood"), CALL_FAMILY("callFamily");

        companion object {
            fun from(options: Set<String>): GoodMoodOptions {
                GoodMoodOptions.values().forEach { opt ->
                    if (options.any { it.equals(opt.option, true) }) {
                        return opt
                    }

                }
                return SPOTIFY
            }
        }
    }
}