package com.diarybook.data.repository

import com.diarybook.data.local.dao.RecurringBillDao
import com.diarybook.data.local.entity.RecurringBill
import kotlinx.coroutines.flow.Flow

class RecurringBillRepository(private val recurringBillDao: RecurringBillDao) {
    
    fun getRecurringBillsByBook(bookId: Long): Flow<List<RecurringBill>> =
        recurringBillDao.getRecurringBillsByBook(bookId)
    
    suspend fun getRecurringBillById(id: Long): RecurringBill? =
        recurringBillDao.getRecurringBillById(id)
    
    suspend fun insertRecurringBill(recurringBill: RecurringBill): Long =
        recurringBillDao.insertRecurringBill(recurringBill)
    
    suspend fun updateRecurringBill(recurringBill: RecurringBill) =
        recurringBillDao.updateRecurringBill(recurringBill)
    
    suspend fun deleteRecurringBill(recurringBill: RecurringBill) =
        recurringBillDao.deleteRecurringBill(recurringBill)
    
    suspend fun deleteRecurringBillsByBook(bookId: Long) =
        recurringBillDao.deleteRecurringBillsByBook(bookId)
}
