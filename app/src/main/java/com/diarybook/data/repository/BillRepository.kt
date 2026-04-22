package com.diarybook.data.repository

import com.diarybook.data.local.dao.BillDao
import com.diarybook.data.local.entity.Bill
import kotlinx.coroutines.flow.Flow

class BillRepository(private val billDao: BillDao) {
    
    fun getAllBills(): Flow<List<Bill>> = 
        billDao.getAllBills()
    
    fun getBillsByBook(bookId: Long): Flow<List<Bill>> = 
        billDao.getBillsByBook(bookId)
    
    fun getBillsByDateRange(bookId: Long, startDate: String, endDate: String): Flow<List<Bill>> =
        billDao.getBillsByDateRange(bookId, startDate, endDate)
    
    fun getBillsByDate(bookId: Long, date: String): Flow<List<Bill>> =
        billDao.getBillsByDate(bookId, date)
    
    suspend fun getBillById(id: Long): Bill? = 
        billDao.getBillById(id)
    
    suspend fun insertBill(bill: Bill): Long = 
        billDao.insertBill(bill)
    
    suspend fun updateBill(bill: Bill) = 
        billDao.updateBill(bill)
    
    suspend fun deleteBill(bill: Bill) = 
        billDao.deleteBill(bill)
    
    suspend fun deleteBillsByBook(bookId: Long) = 
        billDao.deleteBillsByBook(bookId)
    
    suspend fun getTotalExpense(bookId: Long, startDate: String, endDate: String): Double? =
        billDao.getTotalExpense(bookId, startDate, endDate)
    
    suspend fun getTotalIncome(bookId: Long, startDate: String, endDate: String): Double? =
        billDao.getTotalIncome(bookId, startDate, endDate)
}
