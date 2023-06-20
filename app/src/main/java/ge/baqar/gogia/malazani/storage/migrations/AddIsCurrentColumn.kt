package ge.baqar.gogia.malazani.storage.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


object AddIsCurrentColumn {
    val migration: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE Song "
                        + "ADD COLUMN is_current BIT"
            )
        }
    }
}