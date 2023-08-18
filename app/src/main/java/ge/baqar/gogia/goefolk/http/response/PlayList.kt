package ge.baqar.gogia.goefolk.http.response

import ge.baqar.gogia.goefolk.model.Song

data class PlayList(
    val playListId: String,
    val name: String,
    val songs: MutableList<Song>,
    var isSelected: Boolean = false
)