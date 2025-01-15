package ro.pub.cs.systems.eim.practicaltest02v2

data class DictionaryResponse(
    val meanings: List<Meaning>
)

data class Meaning(
    val definitions: List<Definition>
)

data class Definition(
    val definition: String
)