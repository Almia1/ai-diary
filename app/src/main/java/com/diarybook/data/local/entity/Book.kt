package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_book")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val icon: String? = null,
    
    val color: String = "#6A5ACD",
    
    val is_default: Int = 0,
    
    val sort: Int = 0,
    
    val create_time: String,
    
    val update_time: String? = null
)
