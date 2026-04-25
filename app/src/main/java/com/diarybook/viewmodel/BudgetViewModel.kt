package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Budget
import com.diarybook.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: BudgetRepository
    
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()
    
    private val _totalExpenseBudget = MutableStateFlow<Double>(0.0)
    val totalExpenseBudget: StateFlow<Double> = _totalExpenseBudget.asStateFlow()
    
    private val _totalIncomeBudget = MutableStateFlow<Double>(0.0)
    val totalIncomeBudget: StateFlow<Double> = _totalIncomeBudget.asStateFlow()
    
    private var currentBookId: Long = 0
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    
    init {
        val app = application as DiaryBookApplication
        repository = BudgetRepository(app.database.budgetDao())
    }
    
    fun loadBudgets(bookId: Long, year: Int, month: Int) {
        currentBookId = bookId
        currentYear = year
        currentMonth = month
        loadBudgetsByMonth(bookId, year, month)
        loadTotalBudgets(bookId, year, month)
    }
    
    private fun loadBudgetsByMonth(bookId: Long, year: Int, month: Int) {
        viewModelScope.launch {
            repository.getBudgetsByMonth(bookId, year, month).collect {
                _budgets.value = it
            }
        }
    }
    
    private fun loadTotalBudgets(bookId: Long, year: Int, month: Int) {
        viewModelScope.launch {
            val expenseBudget = repository.getTotalBudget(bookId, year, month, 0)
            _totalExpenseBudget.value = expenseBudget?.amount ?: 0.0
            
            val incomeBudget = repository.getTotalBudget(bookId, year, month, 1)
            _totalIncomeBudget.value = incomeBudget?.amount ?: 0.0
        }
    }
    
    fun addTotalBudget(amount: Double, type: Int) {
        viewModelScope.launch {
            val existingBudget = repository.getTotalBudget(currentBookId, currentYear, currentMonth, type)
            if (existingBudget != null) {
                val updatedBudget = existingBudget.copy(amount = amount)
                repository.updateBudget(updatedBudget)
            } else {
                val budget = Budget(
                    book_id = currentBookId,
                    year = currentYear,
                    month = currentMonth,
                    type = type,
                    category_id = 0,
                    amount = amount,
                    create_time = System.currentTimeMillis().toString()
                )
                repository.insertBudget(budget)
            }
            loadTotalBudgets(currentBookId, currentYear, currentMonth)
        }
    }
    
    fun addCategoryBudget(categoryId: Long, amount: Double, type: Int) {
        viewModelScope.launch {
            val existingBudget = repository.getCategoryBudget(currentBookId, currentYear, currentMonth, type, categoryId)
            if (existingBudget != null) {
                val updatedBudget = existingBudget.copy(amount = amount)
                repository.updateBudget(updatedBudget)
            } else {
                val budget = Budget(
                    book_id = currentBookId,
                    year = currentYear,
                    month = currentMonth,
                    type = type,
                    category_id = categoryId,
                    amount = amount,
                    create_time = System.currentTimeMillis().toString()
                )
                repository.insertBudget(budget)
            }
            loadBudgetsByMonth(currentBookId, currentYear, currentMonth)
        }
    }
    
    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            repository.updateBudget(budget)
            _budgets.value = _budgets.value.map {
                if (it.id == budget.id) budget else it
            }
            loadTotalBudgets(currentBookId, currentYear, currentMonth)
        }
    }
    
    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
            _budgets.value = _budgets.value.filter { it.id != budget.id }
            loadTotalBudgets(currentBookId, currentYear, currentMonth)
        }
    }
}

class BudgetViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
