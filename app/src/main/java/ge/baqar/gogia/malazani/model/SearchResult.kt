package ge.baqar.gogia.malazani.model


data class SearchResult(val artists: MutableList<Artist>, val songs: MutableList<Song>)
interface SearchedItem {
    val id: String
    fun detailedName(): String
}