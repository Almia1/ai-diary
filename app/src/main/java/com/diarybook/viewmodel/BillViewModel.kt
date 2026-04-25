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
    
    // 当前选中的账本ID，用于过滤账单
    private var currentFilterBookId: Long = 0
    
    init {
        val app = application as DiaryBookApplication
        repository = BillRepository(app.database.billDao())
        loadDefaultBook()
        // 不在这里加载账单，由外部调用 loadBillsForBook 时再加载
    }
    
    /**
     * 加载指定账本的账单数据
     * @param bookId 账本ID，如果为0则加载所有账单
     */
    fun loadBillsForBook(bookId: Long) {
        currentFilterBookId = bookId
        viewModelScope.launch {
            if (bookId > 0) {
                repository.getBillsByBook(bookId).collect { billList ->
                    _bills.value = billList
                    calculateTotals(billList)
                }
            } else {
                repository.getAllBills().collect { billList ->
                    _bills.value = billList
                    calculateTotals(billList)
                }
            }
        }
    }
    
    /**
     * 与 BookViewModel 联动，当切换账本时自动加载对应账单
     */
    fun syncWithBook(bookId: Long) {
        if (currentFilterBookId != bookId) {
            loadBillsForBook(bookId)
        }
    }
    
    private fun loadDefaultBook() {
        viewModelScope.launch {
            val app = getApplication<DiaryBookApplication>()
            val book = app.database.bookDao().getDefaultBook()
            _defaultBook.value = book
            // 默认加载默认账本的账单
            if (book != null) {
                loadBillsForBook(book.id)
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
