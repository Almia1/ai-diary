package com.diarybook.data.repository

import com.diarybook.data.local.dao.DebtDao
import com.diarybook.data.local.entity.Debt
import kotlinx.coroutines.flow.Flow

class DebtRepository(private val debtDao: DebtDao) {
    
    fun getDebtsByBook(bookId: Long): Flow<List<Debt>> =
        debtDao.getDebtsByBook(bookId)
    
    fun getDebtsByStatus(bookId: Long, status: Int): Flow<List<Debt>> =
        debtDao.getDebtsByStatus(bookId, status)
    
    suspend fun getDebtById(id: Long): Debt? =
        debtDao.getDebtById(id)
    
    suspend fun insertDebt(debt: Debt): Long =
        debtDao.insertDebt(debt)
    
    suspend fun updateDebt(debt: Debt) =
        debtDao.updateDebt(debt)
    
    suspend fun deleteDebt(debt: Debt) =
        debtDao.deleteDebt(debt)
    
    suspend fun deleteDebtsByBook(bookId: Long) =
        debtDao.deleteDebtsByBook(bookId)
}
