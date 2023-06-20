package ge.baqar.gogia.malazani.model

import com.google.gson.annotations.SerializedName

data class Song(
    override val id: String,
    val name: String,
    var nameEng: String,
    val path: String,
    val songType: Int,
    val artistId: String,
    val artistName: String,
    var isPlaying: Boolean = false,
    var data: ByteArray? = null,
    var isFav: Boolean = false
) : SearchedItem {


    override fun equals(other: Any?): Boolean {
        if (other is Song) {
            return other.name == name
                    && other.path == path
                    && other.artistId == artistId
                    && other.songType == songType
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
    @SerializedName("0")
    Song(0),

    @SerializedName("1")
    Chant(1)
}