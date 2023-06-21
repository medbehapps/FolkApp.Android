package ge.baqar.gogia.goefolk.storage.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ge.baqar.gogia.goefolk.model.ArtistType

@Entity(tableName = "Ensemble")
data class DbEnsemble(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "reference_id") val referenceId: String,
    val name: String,
    val nameEng: String,
    var artistType: ArtistType,
    @ColumnInfo(name = "is_current") val isCurrent: Boolean
)