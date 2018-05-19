package pl.angelhackkrakow.pickmeup

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
