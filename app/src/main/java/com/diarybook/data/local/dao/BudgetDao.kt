package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM tb_budget WHERE book_id = :bookId AND year = :year AND month = :month ORDER BY type ASC")
    fun getBudgetsByMonth(bookId: Long, year: Int, month: Int): Flow<List<Budget>>
    
    @Query("SELECT * FROM tb_budget WHERE book_id = :bookId AND year = :year AND month = :month AND type = :type AND category_id IS NULL LIMIT 1")
    suspend fun getTotalBudget(bookId: Long, year: Int, month: Int, type: Int): Budget?
    
    @Query("SELECT * FROM tb_budget WHERE book_id = :bookId AND year = :year AND month = :month AND type = :type AND category_id = :categoryId LIMIT 1")
    suspend fun getCategoryBudget(bookId: Long, year: Int, month: Int, type: Int, categoryId: Long): Budget?
    
    @Query("SELECT * FROM tb_budget WHERE id = :id")
    suspend fun getBudgetById(id: Long): Budget?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long
    
    @Update
    suspend fun updateBudget(budget: Budget)
    
    @Delete
    suspend fun deleteBudget(budget: Budget)
    
    @Query("DELETE FROM tb_budget WHERE book_id = :bookId")
    suspend fun deleteBudgetsByBook(bookId: Long)
}
