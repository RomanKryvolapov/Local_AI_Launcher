/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.romankryvolapov.localailauncher.data.models.database.ChatDialogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDialogDao {

    @Query("SELECT * FROM chatDialog")
    fun subscribeToChatDialogs(): Flow<List<ChatDialogEntity>>

    @Transaction
    fun replaceChatDialog(data: ChatDialogEntity) {
        deleteChatDialog()
        saveChatDialog(data)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChatDialog(data: ChatDialogEntity)

    @Query("DELETE FROM chatDialog")
    fun deleteChatDialog()

}