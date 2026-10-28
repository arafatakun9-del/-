package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [CloudAccountEntity::class, CloudFileItem::class],
  version = 1,
  exportSchema = false
)
abstract class CloudDatabase : RoomDatabase() {
  abstract fun cloudDao(): CloudDao

  companion object {
    @Volatile
    private var INSTANCE: CloudDatabase? = null

    fun getDatabase(context: Context): CloudDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CloudDatabase::class.java,
          "cloud_hunter_cache.db"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
