package ge.baqar.gogia.goefolk.storage.usecase

import android.content.Context
import android.os.Build
import ge.baqar.gogia.goefolk.storage.dataaccess.AudioFileSaveLegacyProcessor
import ge.baqar.gogia.goefolk.storage.dataaccess.AudioFileSaveProcessor
import ge.baqar.gogia.goefolk.storage.dataaccess.FileSaveProcessor

internal data class ProcessorProvider(
    private val context: Context,
    private val fileProviderName: String?) {

    val audioManager: FileSaveProcessor =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AudioFileSaveProcessor(context.contentResolver, context)
        } else {
            AudioFileSaveLegacyProcessor(context, fileProviderName)
        }
}