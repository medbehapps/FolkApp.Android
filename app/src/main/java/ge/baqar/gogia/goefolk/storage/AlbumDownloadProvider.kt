package ge.baqar.gogia.goefolk.storage

import ge.baqar.gogia.goefolk.storage.db.FolkApiDao
import ge.baqar.gogia.goefolk.http.service_implementations.ArtistsServiceImpl
import ge.baqar.gogia.goefolk.http.service_implementations.SongServiceImpl
import ge.baqar.gogia.goefolk.storage.usecase.FileSaveController
import java.util.concurrent.ConcurrentHashMap

class AlbumDownloadProvider(
    private val folkApiDao: FolkApiDao,
    private val songService: SongServiceImpl,
    private val saveController: FileSaveController
) {
    private val _queue = ConcurrentHashMap<String, AlbumDownloadManager>()

    fun tryGet(ensembleId: String): AlbumDownloadManager {
        if (_queue.containsKey(ensembleId))
            return _queue[ensembleId]!!
        val albumDownloadManager = AlbumDownloadManager(folkApiDao, songService, saveController)
        _queue[ensembleId] = albumDownloadManager
        return albumDownloadManager
    }

    fun dispose(albumDownloadProvider: AlbumDownloadManager) {
        if (_queue.containsValue(albumDownloadProvider)) {
            val key = _queue.entries.firstOrNull { it.value == albumDownloadProvider }?.key
            key?.let {
             _queue.remove(key)
            }
        }
    }
}