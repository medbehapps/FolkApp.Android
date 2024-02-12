package ge.baqar.gogia.gefolk.ui.media.playlist

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import ge.baqar.gogia.gefolk.R
import ge.baqar.gogia.gefolk.http.request.addSongAction
import ge.baqar.gogia.gefolk.http.service_implementations.PlayListServiceImpl
import ge.baqar.gogia.gefolk.model.FailedResult
import ge.baqar.gogia.gefolk.model.Song
import ge.baqar.gogia.gefolk.model.SucceedResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddSongToPlayListDialog(
    private val playListService: PlayListServiceImpl
) : CoroutineScope {

    fun showPlayListDialog(activity: Activity, songs: MutableList<Song>) {
        launch {
            playListService.list().collect {
                if (it is SucceedResult) {
                    val result = it.value

                    val dialog = AlertDialog.Builder(activity)
                        .setTitle(R.string.play_list)
                        .setMultiChoiceItems(
                            result.map { it.name }.toTypedArray(),
                            result.map { false }.toBooleanArray()
                        ) { dialog, which, isChecked ->
                            result[which].isSelected = isChecked
                        }
                        .setPositiveButton(
                            activity.getString(R.string.add_to_playlist_action)
                        ) { dialog, which ->
                            result.filter { it.isSelected }.forEach { playList ->
                                launch(Dispatchers.IO) {
                                    playListService.addOrRemoveSong(
                                        playList.playListId,
                                        songs.map { it.id }.toMutableList(),
                                        addSongAction
                                    ).collect {
                                        if (it is SucceedResult) {
                                            val songNames =
                                                songs.map { it.name }.joinToString()
                                            val stringRes =
                                                if (songs.size > 1) R.string.musics_is_added_to_playlist else R.string.music_is_added_to_playlist
                                            val toastText =
                                                String.format(
                                                    activity.getString(stringRes),
                                                    songNames,
                                                    playList.name
                                                )
                                            launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    activity,
                                                    toastText,
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                            return@collect
                                        }

                                        if (it is FailedResult) {
                                            launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    activity,
                                                    it.value.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        .create()
                    dialog.show()
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}