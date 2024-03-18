package ge.baqar.gogia.gefolk.model

import com.google.gson.annotations.SerializedName
import ge.baqar.gogia.gefolk.storage.CharConverter

data class Song(
    override val id: String,
    val name: String,
    val path: String,
    val songType: Int,
    val artistId: String,
    val artistName: String,
    var isPlaying: Boolean = false,
    var data: ByteArray? = null,
    var isFav: Boolean = false,
    var isSelected: Boolean = false
) : SearchedItem {

    val nameEng: String
        get() {
            return CharConverter.toEng(name)
        }

    override fun equals(other: Any?): Boolean {
        if (other is Song) {
            return other.name == name && other.path == path && other.artistId == artistId && other.songType == songType
        }
        return super.equals(other)
    }

    override fun detailedName(): String {
        return "$name - $artistName"
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}

enum class SongType(val index: Int) {
    @SerializedName("1")
    Song(1),

    @SerializedName("2")
    Chant(2)
}