package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.Bill
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM tb_bill ORDER BY date DESC, create_time DESC")
    fun getAllBills(): Flow<List<Bill>>
    
    @Query("SELECT * FROM tb_bill ORDER BY date DESC, create_time DESC")
    suspend fun getAllBillsSync(): List<Bill>
    
    @Query("SELECT * FROM tb_bill WHERE book_id = :bookId ORDER BY date DESC, create_time DESC")
    fun getBillsByBook(bookId: Long): Flow<List<Bill>>
    
    @Query("SELECT * FROM tb_bill WHERE book_id = :bookId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, create_time DESC")
    fun getBillsByDateRange(bookId: Long, startDate: String, endDate: String): Flow<List<Bill>>
    
    @Query("SELECT * FROM tb_bill WHERE book_id = :bookId AND date = :date ORDER BY create_time DESC")
    fun getBillsByDate(bookId: Long, date: String): Flow<List<Bill>>
    
    @Query("SELECT * FROM tb_bill WHERE id = :id")
    suspend fun getBillById(id: Long): Bill?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long
    
    @Update
    suspend fun updateBill(bill: Bill)
    
    @Delete
    suspend fun deleteBill(bill: Bill)
    
    @Query("DELETE FROM tb_bill WHERE book_id = :bookId")
    suspend fun deleteBillsByBook(bookId: Long)
    
    @Query("SELECT SUM(amount) FROM tb_bill WHERE book_id = :bookId AND type = 0 AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpense(bookId: Long, startDate: String, endDate: String): Double?
    
    @Query("SELECT SUM(amount) FROM tb_bill WHERE book_id = :bookId AND type = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalIncome(bookId: Long, startDate: String, endDate: String): Double?
}