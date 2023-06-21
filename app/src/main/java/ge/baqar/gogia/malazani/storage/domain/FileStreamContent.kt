package ge.baqar.gogia.malazani.storage.domain

data class FileStreamContent(
    val data: ByteArray,
    override val fileNameWithoutSuffix: String,
    override val suffix: String,
    override val mimeType: String?,
    override val subfolderName: String?
) : SaveContent