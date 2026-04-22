package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tb_category",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("book_id"),
        Index("book_id", "type")
    ]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val book_id: Long,
    
    val type: Int,
    
    val name: String,
    
    val icon: String,
    
    val color: String = "#6A5ACD",
    
    val is_default: Int = 0,
    
    val sort: Int = 0,
    
    val create_time: String,
    
    val update_time: String? = null
)
