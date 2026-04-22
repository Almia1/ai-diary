package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tb_budget",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("book_id"),
        Index("category_id"),
        Index("book_id", "year", "month", "type")
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val book_id: Long,
    
    val year: Int,
    
    val month: Int,
    
    val type: Int,
    
    val category_id: Long? = null,
    
    val amount: Double,
    
    val used_amount: Double = 0.0,
    
    val create_time: String,
    
    val update_time: String? = null
)
