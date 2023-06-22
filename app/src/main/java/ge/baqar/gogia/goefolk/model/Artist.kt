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

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        if (id != other.id) return false
        if (name != other.name) return false
        if (nameEng != other.nameEng) return false
        if (artistType != other.artistType) return false
        if (isPlaying != other.isPlaying) return false

        return true
    }
}

enum class ArtistType {
    @SerializedName("2")
    ENSEMBLE,
    @SerializedName("1")
    OLD_RECORDING
}