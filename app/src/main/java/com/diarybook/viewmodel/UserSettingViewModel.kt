package com.diarybook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.diarybook.DiaryBookApplication
import com.diarybook.data.local.entity.UserSetting
import com.diarybook.data.repository.UserSettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserSettingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UserSettingRepository
    
    private val _userSetting = MutableStateFlow<UserSetting?>(null)
    val userSetting: StateFlow<UserSetting?> = _userSetting.asStateFlow()
    
    private val _themeMode = MutableStateFlow(0) // 0: 跟随系统, 1: 浅色, 2: 深色
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()
    
    init {
        val app = application as DiaryBookApplication
        repository = UserSettingRepository(app.database.userSettingDao())
        loadUserSettings()
    }
    
    private fun loadUserSettings() {
        viewModelScope.launch {
            repository.getUserSettings().collect {
                _userSetting.value = it
                if (it != null) {
                    _themeMode.value = it.theme_mode
                }
            }
        }
    }
    
    fun updateThemeMode(mode: Int) {
        viewModelScope.launch {
            val currentSetting = _userSetting.value
            if (currentSetting != null) {
                val updatedSetting = currentSetting.copy(
                    theme_mode = mode,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.updateUserSetting(updatedSetting)
                _themeMode.value = mode
            } else {
                val newSetting = UserSetting(
                    theme_mode = mode,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.insertUserSetting(newSetting)
                _themeMode.value = mode
            }
        }
    }
    
    fun updateAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = _userSetting.value
            if (currentSetting != null) {
                val updatedSetting = currentSetting.copy(
                    auto_backup = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.updateUserSetting(updatedSetting)
            } else {
                val newSetting = UserSetting(
                    auto_backup = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.insertUserSetting(newSetting)
            }
        }
    }
    
    fun updateRemindDebt(enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = _userSetting.value
            if (currentSetting != null) {
                val updatedSetting = currentSetting.copy(
                    remind_debt = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.updateUserSetting(updatedSetting)
            } else {
                val newSetting = UserSetting(
                    remind_debt = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.insertUserSetting(newSetting)
            }
        }
    }
    
    fun updateRemindRecurring(enabled: Boolean) {
        viewModelScope.launch {
            val currentSetting = _userSetting.value
            if (currentSetting != null) {
                val updatedSetting = currentSetting.copy(
                    remind_recurring = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.updateUserSetting(updatedSetting)
            } else {
                val newSetting = UserSetting(
                    remind_recurring = if (enabled) 1 else 0,
                    update_time = System.currentTimeMillis().toString()
                )
                repository.insertUserSetting(newSetting)
            }
        }
    }
}

class UserSettingViewModelFactory(
    private val application: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
