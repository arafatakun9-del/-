package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudDao {
  // Accounts queries
  @Query("SELECT * FROM cloud_accounts ORDER BY lastSyncTime DESC")
  fun getAllAccounts(): Flow<List<CloudAccountEntity>>

  @Query("SELECT * FROM cloud_accounts WHERE providerId = :providerId ORDER BY lastSyncTime DESC")
  fun getAccountsByProvider(providerId: String): Flow<List<CloudAccountEntity>>

  @Query("SELECT * FROM cloud_accounts WHERE id = :id LIMIT 1")
  suspend fun getAccountById(id: String): CloudAccountEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccount(account: CloudAccountEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccounts(accounts: List<CloudAccountEntity>)

  @Update
  suspend fun updateAccount(account: CloudAccountEntity)

  @Delete
  suspend fun deleteAccount(account: CloudAccountEntity)

  @Query("DELETE FROM cloud_accounts WHERE id = :id")
  suspend fun deleteAccountById(id: String)

  // Files & Gallery Cache Queries
  @Query("SELECT * FROM cloud_files ORDER BY timestamp DESC")
  fun getAllFiles(): Flow<List<CloudFileItem>>

  @Query("SELECT * FROM cloud_files WHERE isRecent = 1 ORDER BY timestamp DESC LIMIT 20")
  fun getRecentFiles(): Flow<List<CloudFileItem>>

  @Query("SELECT * FROM cloud_files WHERE category = :category ORDER BY timestamp DESC")
  fun getFilesByCategory(category: String): Flow<List<CloudFileItem>>

  @Query("SELECT * FROM cloud_files WHERE providerId = :providerId ORDER BY timestamp DESC")
  fun getFilesByProvider(providerId: String): Flow<List<CloudFileItem>>

  // Local Cache Smart Search Query (indexes file name, remote path, account nickname)
  @Query("""
    SELECT * FROM cloud_files 
    WHERE fileName LIKE '%' || :query || '%' 
       OR remotePath LIKE '%' || :query || '%' 
       OR accountNickname LIKE '%' || :query || '%' 
    ORDER BY timestamp DESC
  """)
  fun searchFiles(query: String): Flow<List<CloudFileItem>>

  @Query("""
    SELECT * FROM cloud_files 
    WHERE providerId = :providerId 
      AND (fileName LIKE '%' || :query || '%' 
           OR remotePath LIKE '%' || :query || '%' 
           OR accountNickname LIKE '%' || :query || '%')
    ORDER BY timestamp DESC
  """)
  fun searchFilesWithProvider(query: String, providerId: String): Flow<List<CloudFileItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFiles(files: List<CloudFileItem>)

  @Query("DELETE FROM cloud_files WHERE accountId = :accountId")
  suspend fun deleteFilesForAccount(accountId: String)

  @Query("DELETE FROM cloud_files")
  suspend fun clearAllFiles()

  @Query("SELECT COUNT(*) FROM cloud_files")
  suspend fun getFilesCount(): Int

  @Query("SELECT COUNT(*) FROM cloud_accounts")
  suspend fun getAccountsCount(): Int
}
