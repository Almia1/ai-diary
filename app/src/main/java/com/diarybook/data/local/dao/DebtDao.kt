package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM tb_debt WHERE book_id = :bookId ORDER BY status ASC, due_date ASC")
    fun getDebtsByBook(bookId: Long): Flow<List<Debt>>
    
    @Query("SELECT * FROM tb_debt WHERE book_id = :bookId AND status = :status ORDER BY due_date ASC")
    fun getDebtsByStatus(bookId: Long, status: Int): Flow<List<Debt>>
    
    @Query("SELECT * FROM tb_debt WHERE id = :id")
    suspend fun getDebtById(id: Long): Debt?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt): Long
    
    @Update
    suspend fun updateDebt(debt: Debt)
    
    @Delete
    suspend fun deleteDebt(debt: Debt)
    
    @Query("DELETE FROM tb_debt WHERE book_id = :bookId")
    suspend fun deleteDebtsByBook(bookId: Long)
}
