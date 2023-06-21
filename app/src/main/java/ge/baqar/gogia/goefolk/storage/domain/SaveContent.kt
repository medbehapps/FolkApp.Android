package ge.baqar.gogia.goefolk.storage.domain

import ge.baqar.gogia.goefolk.storage.utils.DOT

interface SaveContent {
    val fileNameWithoutSuffix: String
    val suffix: String
    val mimeType: String?
    val subfolderName: String?

    val fileNameWithSuffix: String
        get() = "$fileNameWithoutSuffix$DOT${suffix}"

}