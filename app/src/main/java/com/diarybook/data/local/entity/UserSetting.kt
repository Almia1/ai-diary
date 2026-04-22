package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_user_setting")
data class UserSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val theme_mode: Int = 0,
    
    val auto_backup: Int = 1,
    
    val backup_time: String = "23:00",
    
    val currency_symbol: String = "¥",
    
    val remind_debt: Int = 1,
    
    val remind_recurring: Int = 1,
    
    val cache_auto_clean: Int = 1,
    
    val update_time: String? = null
)
