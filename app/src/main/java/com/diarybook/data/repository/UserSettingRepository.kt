package com.diarybook.data.repository

import com.diarybook.data.local.dao.UserSettingDao
import com.diarybook.data.local.entity.UserSetting
import kotlinx.coroutines.flow.Flow

class UserSettingRepository(private val userSettingDao: UserSettingDao) {
    
    fun getUserSettings(): Flow<UserSetting?> =
        userSettingDao.getUserSettings()
    
    suspend fun getUserSettingsSync(): UserSetting? =
        userSettingDao.getUserSettingsSync()
    
    suspend fun insertUserSetting(userSetting: UserSetting): Long =
        userSettingDao.insertUserSetting(userSetting)
    
    suspend fun updateUserSetting(userSetting: UserSetting) =
        userSettingDao.updateUserSetting(userSetting)
    
    suspend fun deleteAllUserSettings() =
        userSettingDao.deleteAllUserSettings()
}
