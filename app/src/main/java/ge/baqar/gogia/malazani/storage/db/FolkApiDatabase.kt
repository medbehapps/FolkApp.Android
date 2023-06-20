package ge.baqar.gogia.malazani.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ge.baqar.gogia.malazani.storage.model.DbEnsemble
import ge.baqar.gogia.malazani.storage.model.DbSong

@Database(entities = [DbSong::class, DbEnsemble::class], version = 4)
abstract class FolkApiDatabase : RoomDatabase() {
    abstract fun folkApiDao(): FolkApiDao?
}