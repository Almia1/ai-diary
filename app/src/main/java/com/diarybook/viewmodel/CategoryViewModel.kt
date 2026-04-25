package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.Category
import com.diarybook.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: CategoryRepository
    
    private val _expenseCategories = MutableStateFlow<List<Category>>(emptyList())
    val expenseCategories: StateFlow<List<Category>> = _expenseCategories.asStateFlow()
    
    private val _incomeCategories = MutableStateFlow<List<Category>>(emptyList())
    val incomeCategories: StateFlow<List<Category>> = _incomeCategories.asStateFlow()
    
    private var currentBookId: Long = 0
    
    init {
        val app = application as DiaryBookApplication
        repository = CategoryRepository(app.database.categoryDao())
        // 不在这里加载，由外部调用 loadCategories 时再加载
    }
    
    /**
     * 加载指定账本的分类数据
     * @param bookId 账本ID，如果为0则加载全局分类
     */
    fun loadCategories(bookId: Long) {
        currentBookId = bookId
        loadExpenseCategories(bookId)
        loadIncomeCategories(bookId)
    }
    
    /**
     * 与 BookViewModel 联动，当切换账本时自动加载对应分类
     */
    fun syncWithBook(bookId: Long) {
        if (currentBookId != bookId) {
            loadCategories(bookId)
        }
    }
    
    private fun loadExpenseCategories(bookId: Long) {
        viewModelScope.launch {
            repository.getCategoriesByType(bookId, 0).collect {
                _expenseCategories.value = it
            }
        }
    }
    
    private fun loadIncomeCategories(bookId: Long) {
        viewModelScope.launch {
            repository.getCategoriesByType(bookId, 1).collect {
                _incomeCategories.value = it
            }
        }
    }
    
    fun addCategory(name: String, icon: String, color: String, type: Int) {
        viewModelScope.launch {
            val category = Category(
                book_id = currentBookId, // 使用当前账本ID
                type = type,
                name = name,
                icon = icon,
                color = color,
                is_default = 0,
                sort = getNextSort(type),
                create_time = System.currentTimeMillis().toString()
            )
            val id = repository.insertCategory(category)
            val newCategory = category.copy(id = id)
            if (type == 0) {
                _expenseCategories.value = _expenseCategories.value + newCategory
            } else {
                _incomeCategories.value = _incomeCategories.value + newCategory
            }
        }
    }
    
    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            if (category.type == 0) {
                _expenseCategories.value = _expenseCategories.value.map {
                    if (it.id == category.id) category else it
                }
            } else {
                _incomeCategories.value = _incomeCategories.value.map {
                    if (it.id == category.id) category else it
                }
            }
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (category.type == 0) {
                _expenseCategories.value = _expenseCategories.value.filter { it.id != category.id }
            } else {
                _incomeCategories.value = _incomeCategories.value.filter { it.id != category.id }
            }
        }
    }
    
    private fun getNextSort(type: Int): Int {
        val categories = if (type == 0) _expenseCategories.value else _incomeCategories.value
        return if (categories.isEmpty()) 1 else categories.maxOfOrNull { it.sort }?.plus(1) ?: 1
    }
}

class CategoryViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
