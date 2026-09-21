package com.example.data.equipment

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EquipmentRecord::class,
        PrRequest::class,
        LongHourDutyRecord::class,
        StoreIssueRecord::class,
        StoreShiftRecord::class,
        JeepMovementRecord::class,
        RosterTlcRecord::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun prDao(): PrDao
    abstract fun longHourDao(): LongHourDao
    abstract fun storeDao(): StoreDao
    abstract fun jeepDao(): JeepDao
    abstract fun rosterTlcDao(): RosterTlcDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kharsia_equipment_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
