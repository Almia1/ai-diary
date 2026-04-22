package com.diarybook.data.repository

import com.diarybook.data.local.dao.BudgetDao
import com.diarybook.data.local.entity.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    
    fun getBudgetsByMonth(bookId: Long, year: Int, month: Int): Flow<List<Budget>> =
        budgetDao.getBudgetsByMonth(bookId, year, month)
    
    suspend fun getTotalBudget(bookId: Long, year: Int, month: Int, type: Int): Budget? =
        budgetDao.getTotalBudget(bookId, year, month, type)
    
    suspend fun getCategoryBudget(bookId: Long, year: Int, month: Int, type: Int, categoryId: Long): Budget? =
        budgetDao.getCategoryBudget(bookId, year, month, type, categoryId)
    
    suspend fun getBudgetById(id: Long): Budget? =
        budgetDao.getBudgetById(id)
    
    suspend fun insertBudget(budget: Budget): Long =
        budgetDao.insertBudget(budget)
    
    suspend fun updateBudget(budget: Budget) =
        budgetDao.updateBudget(budget)
    
    suspend fun deleteBudget(budget: Budget) =
        budgetDao.deleteBudget(budget)
    
    suspend fun deleteBudgetsByBook(bookId: Long) =
        budgetDao.deleteBudgetsByBook(bookId)
}
