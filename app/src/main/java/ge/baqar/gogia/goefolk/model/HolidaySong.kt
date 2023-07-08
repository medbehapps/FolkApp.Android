package ge.baqar.gogia.goefolk.model

class HolidaySong(
    override val id: String,
    val name: String,
    var nameEng: String,
    val path: String,
    val songType: Int,
    val artistId: String,
    val artistName: String,
    var holidayImagePath: String,
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