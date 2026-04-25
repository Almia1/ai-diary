package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.RecurringBill
import com.diarybook.data.repository.RecurringBillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecurringBillViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: RecurringBillRepository
    
    private val _recurringBills = MutableStateFlow<List<RecurringBill>>(emptyList())
    val recurringBills: StateFlow<List<RecurringBill>> = _recurringBills.asStateFlow()
    
    private var currentBookId: Long = 0
    
    init {
        val app = application as DiaryBookApplication
        repository = RecurringBillRepository(app.database.recurringBillDao())
    }
    
    fun loadRecurringBills(bookId: Long) {
        currentBookId = bookId
        loadBillsByBook(bookId)
    }
    
    private fun loadBillsByBook(bookId: Long) {
        viewModelScope.launch {
            repository.getRecurringBillsByBook(bookId).collect {
                _recurringBills.value = it
            }
        }
    }
    
    fun addRecurringBill(
        type: Int,
        amount: Double,
        categoryId: Long,
        cycleType: Int,
        cycleValue: String,
        startDate: String,
        remindTime: String,
        endDate: String? = null,
        isAutoCreate: Int = 0
    ) {
        viewModelScope.launch {
            val recurringBill = RecurringBill(
                book_id = currentBookId,
                type = type,
                amount = amount,
                category_id = categoryId,
                cycle_type = cycleType,
                cycle_value = cycleValue,
                start_date = startDate,
                end_date = endDate,
                is_auto_create = isAutoCreate,
                remind_time = remindTime,
                create_time = System.currentTimeMillis().toString()
            )
            val id = repository.insertRecurringBill(recurringBill)
            val newBill = recurringBill.copy(id = id)
            _recurringBills.value = _recurringBills.value + newBill
        }
    }
    
    fun updateRecurringBill(recurringBill: RecurringBill) {
        viewModelScope.launch {
            repository.updateRecurringBill(recurringBill)
            _recurringBills.value = _recurringBills.value.map {
                if (it.id == recurringBill.id) recurringBill else it
            }
        }
    }
    
    fun deleteRecurringBill(recurringBill: RecurringBill) {
        viewModelScope.launch {
            repository.deleteRecurringBill(recurringBill)
            _recurringBills.value = _recurringBills.value.filter { it.id != recurringBill.id }
        }
    }
    
    fun updateRecurringBillWithTime(recurringBill: RecurringBill) {
        val updatedBill = recurringBill.copy(
            update_time = System.currentTimeMillis().toString()
        )
        updateRecurringBill(updatedBill)
    }
}

class RecurringBillViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecurringBillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecurringBillViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
