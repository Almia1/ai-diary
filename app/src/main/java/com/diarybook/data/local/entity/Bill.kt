package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tb_bill",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.SET_DEFAULT,
            deferred = true
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_DEFAULT,
            deferred = true
        )
    ],
    indices = [
        Index("book_id"),
        Index("category_id"),
        Index("date")
    ]
)
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val book_id: Long = 0,
    
    val type: Int,
    
    val amount: Double,
    
    val category_id: Long = 0,
    
    val category_name: String,
    
    val category_icon: String,
    
    val date: String,
    
    val remark: String? = null,
    
    val image_path: String? = null,
    
    val create_time: String,
    
    val update_time: String? = null
)
