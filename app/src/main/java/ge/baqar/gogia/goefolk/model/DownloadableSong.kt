package ge.baqar.gogia.goefolk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadableSong(
    val id: String,
    val name: String,
    val nameEng: String,
    val link: String,
    val songType: Int,
    val ensembleId: String
) : Parcelable {
}
