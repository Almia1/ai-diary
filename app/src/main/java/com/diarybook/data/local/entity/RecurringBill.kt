package com.diarybook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tb_recurring_bill",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("book_id")
    ]
)
data class RecurringBill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val book_id: Long,
    
    val type: Int,
    
    val amount: Double,
    
    val category_id: Long,
    
    val cycle_type: Int,
    
    val cycle_value: String,
    
    val start_date: String,
    
    val end_date: String? = null,
    
    val is_auto_create: Int = 0,
    
    val remind_time: String,
    
    val create_time: String,
    
    val update_time: String? = null
)
