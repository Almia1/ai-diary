package com.diarybook.testing

import android.content.Context
import androidx.room.Room
import com.diarybook.data.local.database.AppDatabase

object TestDatabases {
    fun inMemoryDb(context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    fun setSingletonInstance(db: AppDatabase?) {
        val field = AppDatabase::class.java.getDeclaredField("INSTANCE")
        field.isAccessible = true
        field.set(null, db)
    }
}

