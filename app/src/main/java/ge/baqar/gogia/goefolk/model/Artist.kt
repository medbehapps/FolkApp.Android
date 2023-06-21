package ge.baqar.gogia.goefolk.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Artist(
    override val id: String,
    val name: String,
    var nameEng: String,
    var artistType: ArtistType,
    var isPlaying: Boolean = false
) : SearchedItem, Parcelable {
    override fun detailedName(): String {
        return name
    }
}

enum class ArtistType {
    @SerializedName("2")
    ENSEMBLE,
    @SerializedName("1")
    OLD_RECORDING
}