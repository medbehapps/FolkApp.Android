package ge.baqar.gogia.malazani.storage

import ge.baqar.gogia.malazani.storage.db.FolkApiDao
import ge.baqar.gogia.malazani.http.FolkApiRepository
import ge.baqar.gogia.malazani.storage.usecase.FileSaveController
import java.util.concurrent.ConcurrentHashMap

class AlbumDownloadProvider(
    private val folkApiDao: FolkApiDao,
    private val folkApiRepository: FolkApiRepository,
    private val saveController: FileSaveController
) {
    private val _queue = ConcurrentHashMap<String, AlbumDownloadManager>()

    fun tryGet(ensembleId: String): AlbumDownloadManager {
        if (_queue.containsKey(ensembleId))
            return _queue[ensembleId]!!
        val albumDownloadManager = AlbumDownloadManager(folkApiDao, folkApiRepository, saveController)
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