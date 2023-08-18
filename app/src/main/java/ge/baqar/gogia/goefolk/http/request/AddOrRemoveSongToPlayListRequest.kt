package ge.baqar.gogia.goefolk.http.request

data class AddOrRemoveSongToPlayListRequest (val songId: MutableList<String>, val action: Int)

val addSongAction = 1
val removeSongAction = 2