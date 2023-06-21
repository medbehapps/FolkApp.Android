package ge.baqar.gogia.goefolk.storage

import android.content.Context
import androidx.room.Room
import ge.baqar.gogia.goefolk.storage.db.FolkApiDao
import ge.baqar.gogia.goefolk.storage.db.FolkApiDatabase


fun provideFolkApiDatabase(context: Context): FolkApiDao? {
    val db =  Room.databaseBuilder(
        context,
        FolkApiDatabase::class.java,
        "folkapidb"
    ).fallbackToDestructiveMigration()
        .build()

    return db.folkApiDao()
}