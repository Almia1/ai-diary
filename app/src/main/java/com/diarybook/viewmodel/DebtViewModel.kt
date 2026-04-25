package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Debt
import com.diarybook.data.repository.DebtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DebtRepository
    
    private val _debts = MutableStateFlow<List<Debt>>(emptyList())
    val debts: StateFlow<List<Debt>> = _debts.asStateFlow()
    
    private val _unpaidDebts = MutableStateFlow<List<Debt>>(emptyList())
    val unpaidDebts: StateFlow<List<Debt>> = _unpaidDebts.asStateFlow()
    
    private val _paidDebts = MutableStateFlow<List<Debt>>(emptyList())
    val paidDebts: StateFlow<List<Debt>> = _paidDebts.asStateFlow()
    
    private var currentBookId: Long = 0
    
    init {
        val app = application as DiaryBookApplication
        repository = DebtRepository(app.database.debtDao())
    }
    
    fun loadDebts(bookId: Long) {
        currentBookId = bookId
        loadAllDebts(bookId)
        loadUnpaidDebts(bookId)
        loadPaidDebts(bookId)
    }
    
    private fun loadAllDebts(bookId: Long) {
        viewModelScope.launch {
            repository.getDebtsByBook(bookId).collect {
                _debts.value = it
            }
        }
    }
    
    private fun loadUnpaidDebts(bookId: Long) {
        viewModelScope.launch {
            repository.getDebtsByStatus(bookId, 0).collect {
                _unpaidDebts.value = it
            }
        }
    }
    
    private fun loadPaidDebts(bookId: Long) {
        viewModelScope.launch {
            repository.getDebtsByStatus(bookId, 1).collect {
                _paidDebts.value = it
            }
        }
    }
    
    fun addDebt(
        name: String,
        amount: Double,
        type: Int,
        dueDate: String,
        remark: String? = null
    ) {
        viewModelScope.launch {
            val debt = Debt(
                book_id = currentBookId,
                name = name,
                amount = amount,
                type = type,
                status = 0,
                due_date = dueDate,
                remark = remark,
                create_time = System.currentTimeMillis().toString()
            )
            val id = repository.insertDebt(debt)
            val newDebt = debt.copy(id = id)
            _debts.value = _debts.value + newDebt
            _unpaidDebts.value = _unpaidDebts.value + newDebt
        }
    }
    
    fun updateDebt(debt: Debt) {
        viewModelScope.launch {
            repository.updateDebt(debt)
            _debts.value = _debts.value.map {
                if (it.id == debt.id) debt else it
            }
            if (debt.status == 0) {
                _unpaidDebts.value = _unpaidDebts.value.map {
                    if (it.id == debt.id) debt else it
                }
                _paidDebts.value = _paidDebts.value.filter { it.id != debt.id }
            } else {
                _paidDebts.value = _paidDebts.value.map {
                    if (it.id == debt.id) debt else it
                }
                _unpaidDebts.value = _unpaidDebts.value.filter { it.id != debt.id }
            }
        }
    }
    
    fun markAsPaid(debt: Debt) {
        val updatedDebt = debt.copy(
            status = 1,
            update_time = System.currentTimeMillis().toString()
        )
        updateDebt(updatedDebt)
    }
    
    fun deleteDebt(debt: Debt) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
            _debts.value = _debts.value.filter { it.id != debt.id }
            if (debt.status == 0) {
                _unpaidDebts.value = _unpaidDebts.value.filter { it.id != debt.id }
            } else {
                _paidDebts.value = _paidDebts.value.filter { it.id != debt.id }
            }
        }
    }
}

class DebtViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebtViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DebtViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
