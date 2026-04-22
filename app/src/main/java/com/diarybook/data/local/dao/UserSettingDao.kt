package com.diarybook.data.local.dao

import androidx.room.*
import com.diarybook.data.local.entity.UserSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingDao {
    @Query("SELECT * FROM tb_user_setting LIMIT 1")
    fun getUserSettings(): Flow<UserSetting?>
    
    @Query("SELECT * FROM tb_user_setting LIMIT 1")
    suspend fun getUserSettingsSync(): UserSetting?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSetting(userSetting: UserSetting): Long
    
    @Update
    suspend fun updateUserSetting(userSetting: UserSetting)
    
    @Query("DELETE FROM tb_user_setting")
    suspend fun deleteAllUserSettings()
}
