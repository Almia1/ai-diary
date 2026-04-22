package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tb_debt",
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
        Index("book_id", "status", "due_date")
    ]
)
data class Debt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val book_id: Long,
    
    val name: String,
    
    val type: Int,
    
    val amount: Double,
    
    val due_date: String,
    
    val paid_amount: Double = 0.0,
    
    val status: Int = 0,
    
    val remark: String? = null,
    
    val create_time: String,
    
    val update_time: String? = null
)
