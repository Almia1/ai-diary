package com.diarybook

import android.app.Application
import com.diarybook.data.local.database.AppDatabase
import com.diarybook.data.local.database.DatabaseInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DiaryBookApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    val database by lazy { AppDatabase.getDatabase(this) }
    
    val databaseInitializer by lazy {
        DatabaseInitializer(this, applicationScope)
    }
    
    override fun onCreate() {
        super.onCreate()
        databaseInitializer.initialize()
    }
}