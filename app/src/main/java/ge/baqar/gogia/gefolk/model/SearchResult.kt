package ge.baqar.gogia.gefolk.model


data class SearchResult(val artists: MutableList<Artist>, val songs: MutableList<Song>)
interface SearchedItem {
    val id: String
    fun detailedName(): String
}