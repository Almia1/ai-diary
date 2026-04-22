package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Bill
import com.diarybook.data.local.entity.Book
import com.diarybook.data.repository.BillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: BillRepository
    
    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills.asStateFlow()
    
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    
    private val _defaultBook = MutableStateFlow<Book?>(null)
    val defaultBook: StateFlow<Book?> = _defaultBook.asStateFlow()
    
    init {
        val app = application as DiaryBookApplication
        repository = BillRepository(app.database.billDao())
        loadBills()
        loadDefaultBook()
    }
    
    private fun loadDefaultBook() {
        viewModelScope.launch {
            val app = getApplication<DiaryBookApplication>()
            val book = app.database.bookDao().getDefaultBook()
            _defaultBook.value = book
        }
    }
    
    private fun loadBills() {
        viewModelScope.launch {
            repository.getAllBills().collect { billList ->
                _bills.value = billList
                calculateTotals(billList)
            }
        }
    }
    
    private fun calculateTotals(bills: List<Bill>) {
        var expense = 0.0
        var income = 0.0
        
        bills.forEach { bill ->
            if (bill.type == 0) {
                expense += bill.amount
            } else {
                income += bill.amount
            }
        }
        
        _totalExpense.value = expense
        _totalIncome.value = income
    }
    
    fun insertBill(
        bookId: Long,
        type: Int,
        categoryId: Long,
        categoryName: String,
        categoryIcon: String,
        amount: Double,
        date: String,
        remark: String? = null
    ) {
        viewModelScope.launch {
            val bill = Bill(
                book_id = bookId,
                type = type,
                category_id = categoryId,
                category_name = categoryName,
                category_icon = categoryIcon,
                amount = amount,
                date = date,
                remark = remark,
                create_time = System.currentTimeMillis().toString(),
                update_time = System.currentTimeMillis().toString()
            )
            repository.insertBill(bill)
        }
    }
    
    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            repository.deleteBill(bill)
        }
    }
}

class BillViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
