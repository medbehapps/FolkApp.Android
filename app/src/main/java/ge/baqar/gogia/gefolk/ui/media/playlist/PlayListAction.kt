package ge.baqar.gogia.gefolk.ui.media.playlist

import ge.baqar.gogia.gefolk.model.Song

//Actions
open class PlayListAction
open class LoadPlayLists : PlayListAction()
class ReloadAction : PlayListAction()
open class CreateNewPlaylist(val name: String, val songs: MutableList<Song>) : PlayListAction()
open class RemoveSongsFromPlayList(val playlistId: String, val songs: MutableList<Song>, val action: Int): PlayListAction()

open class DeletePlayListsAction(val playListsId: MutableList<String>): PlayListAction()