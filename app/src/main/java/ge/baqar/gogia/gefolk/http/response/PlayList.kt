package ge.baqar.gogia.gefolk.http.response

import ge.baqar.gogia.gefolk.model.Song

data class PlayList(
    val playListId: String,
    val name: String,
    val songs: MutableList<Song>,
    var isSelected: Boolean = false
)