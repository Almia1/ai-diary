package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.RecurringBill
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringBillDao {
    @Query("SELECT * FROM tb_recurring_bill WHERE book_id = :bookId ORDER BY create_time DESC")
    fun getRecurringBillsByBook(bookId: Long): Flow<List<RecurringBill>>
    
    @Query("SELECT * FROM tb_recurring_bill WHERE id = :id")
    suspend fun getRecurringBillById(id: Long): RecurringBill?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringBill(recurringBill: RecurringBill): Long
    
    @Update
    suspend fun updateRecurringBill(recurringBill: RecurringBill)
    
    @Delete
    suspend fun deleteRecurringBill(recurringBill: RecurringBill)
    
    @Query("DELETE FROM tb_recurring_bill WHERE book_id = :bookId")
    suspend fun deleteRecurringBillsByBook(bookId: Long)
}
