package ge.baqar.gogia.goefolk.storage.domain

import android.net.Uri

sealed interface FileSaveResult {

    data class SaveSuccess(val successData: Uri) : FileSaveResult

    object MissingWritePermission : FileSaveResult

    data class SaveError(val exception: Throwable?) : FileSaveResult
}
