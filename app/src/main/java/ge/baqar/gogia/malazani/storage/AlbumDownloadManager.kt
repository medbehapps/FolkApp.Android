package ge.baqar.gogia.malazani.storage

import ge.baqar.gogia.malazani.storage.db.FolkApiDao
import ge.baqar.gogia.malazani.storage.model.DbEnsemble
import ge.baqar.gogia.malazani.storage.model.DbSong
import ge.baqar.gogia.malazani.http.FolkApiRepository
import ge.baqar.gogia.malazani.utility.toDb
import ge.baqar.gogia.malazani.model.DownloadableSong
import ge.baqar.gogia.malazani.model.Artist
import ge.baqar.gogia.malazani.model.SucceedResult
import ge.baqar.gogia.malazani.storage.domain.FileStreamContent
import ge.baqar.gogia.malazani.storage.usecase.FileSaveController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*

class AlbumDownloadManager internal constructor(
    private val folkApiDao: FolkApiDao,
    private val folkApiRepository: FolkApiRepository,
    private val saveController: FileSaveController
) : CoroutineScope {
    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private val songs: MutableList<DbSong> = mutableListOf()
    private lateinit var _artist: Artist
    private var isDownloading = false
    private var canceled = false
    var downloadId = Random().nextInt(3000)

    fun setDownloadData(artist: Artist, downloadSongs: MutableList<DownloadableSong>) {
        _artist = artist
        songs.clear()
        songs.addAll(downloadSongs.map {
            it.toDb()
        })
    }

    fun download(completion: () -> Unit) {
        if (!::_artist.isInitialized)
            return

        launch {
            isDownloading = true
            var existingEnsemble = folkApiDao.ensembleById(_artist.id)
            if (existingEnsemble == null) {
                existingEnsemble = DbEnsemble(
                    UUID.randomUUID().toString(),
                    _artist.id,
                    _artist.name,
                    _artist.nameEng,
                    _artist.artistType,
                    false
                )
                folkApiDao.saveEnsemble(existingEnsemble)
            }

            val dbSongs = folkApiDao.songsByEnsembleId(_artist.id)
            val filtered =
                songs.filter { outer ->
                    dbSongs.firstOrNull { inner -> inner.referenceId == outer.referenceId } == null
                }

            if (filtered.isEmpty()) {
                completion()
                return@launch
            }

            for (song in filtered) {
                if (canceled) return@launch

                val exists = saveController.exists(_artist.nameEng, song.nameEng)
                if (!exists) {
                    val result = folkApiRepository.downloadSong(song.referenceId)
                    if (result is SucceedResult<ByteArray>) {
                        saveController.saveDocumentFile(
                            FileStreamContent(
                                data = result.value,
                                fileNameWithoutSuffix = song.nameEng,
                                suffix = "mp3",
                                mimeType = "audio/mp3",
                                subfolderName = _artist.nameEng
                            )
                        )
                    }
                }
                folkApiDao.saveSong(song)
            }

            isDownloading = false
            completion()
        }
    }

    fun cancel() {
        canceled = true
    }

    fun clearDownloads(ensembleId: String, songIds: MutableList<DownloadableSong>, ensembleName: String) {
        launch {
            folkApiDao.removeSongsByIds(songIds.map { it.id })

            val songs = folkApiDao.songsByEnsembleId(ensembleId)
            if (songs.isEmpty())
                folkApiDao.removeEnsemble(ensembleId)

            songIds.forEach {
                saveController.delete(ensembleName, it.nameEng)
            }
        }
    }
}