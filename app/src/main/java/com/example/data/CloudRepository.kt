package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

data class AggregatedStorage(
  val totalCapacityBytes: Long,
  val totalUsedBytes: Long,
  val usedPercentage: Float,
  val providerBreakdown: Map<CloudProvider, ProviderStorageStats>
)

data class ProviderStorageStats(
  val provider: CloudProvider,
  val accountsCount: Int,
  val totalCapacityBytes: Long,
  val usedBytes: Long,
  val percentage: Float
)

class CloudRepository(private val dao: CloudDao) {

  val allAccounts: Flow<List<CloudAccountEntity>> = dao.getAllAccounts()
  val allFiles: Flow<List<CloudFileItem>> = dao.getAllFiles()
  val recentFiles: Flow<List<CloudFileItem>> = dao.getRecentFiles()

  fun getFilesByCategory(category: String): Flow<List<CloudFileItem>> {
    return if (category.lowercase() == "all") {
      dao.getAllFiles()
    } else {
      dao.getFilesByCategory(category.lowercase())
    }
  }

  fun searchLocalFiles(query: String, providerFilter: CloudProvider?): Flow<List<CloudFileItem>> {
    return if (providerFilter == null) {
      dao.searchFiles(query)
    } else {
      dao.searchFilesWithProvider(query, providerFilter.id)
    }
  }

  val aggregatedStorage: Flow<AggregatedStorage> = dao.getAllAccounts().map { accounts ->
    var totalCap = 0L
    var totalUsed = 0L
    val breakdown = mutableMapOf<CloudProvider, MutableList<CloudAccountEntity>>()

    for (p in CloudProvider.entries) {
      breakdown[p] = mutableListOf()
    }

    for (acc in accounts) {
      totalCap += acc.totalBytes
      totalUsed += acc.usedBytes
      breakdown[acc.provider]?.add(acc)
    }

    val statsMap = breakdown.mapValues { (provider, list) ->
      val pCap = list.sumOf { it.totalBytes }
      val pUsed = list.sumOf { it.usedBytes }
      val pPct = if (pCap > 0) (pUsed.toFloat() / pCap.toFloat()) * 100f else 0f
      ProviderStorageStats(
        provider = provider,
        accountsCount = list.size,
        totalCapacityBytes = pCap,
        usedBytes = pUsed,
        percentage = pPct
      )
    }

    val overallPct = if (totalCap > 0) (totalUsed.toFloat() / totalCap.toFloat()) * 100f else 0f
    AggregatedStorage(
      totalCapacityBytes = totalCap,
      totalUsedBytes = totalUsed,
      usedPercentage = overallPct,
      providerBreakdown = statsMap
    )
  }

  suspend fun addAccount(
    provider: CloudProvider,
    nickname: String,
    identifier: String,
    quotaGb: Long
  ) = withContext(Dispatchers.IO) {
    val totalBytes = quotaGb * 1024L * 1024L * 1024L
    // Simulate initial realistic used footprint
    val usedBytes = (totalBytes * (0.15 + (Math.random() * 0.45))).toLong()
    val newAccount = CloudAccountEntity(
      id = UUID.randomUUID().toString(),
      providerId = provider.id,
      nickname = nickname.ifBlank { "${provider.displayName} #${(Math.random() * 900 + 100).toInt()}" },
      identifier = identifier.ifBlank { "user_${(Math.random() * 9000 + 1000).toInt()}" },
      totalBytes = totalBytes,
      usedBytes = usedBytes,
      isActive = true,
      lastSyncTime = System.currentTimeMillis()
    )
    dao.insertAccount(newAccount)
    generateMockFilesForAccount(newAccount)
  }

  suspend fun updateNickname(accountId: String, newNickname: String) = withContext(Dispatchers.IO) {
    val acc = dao.getAccountById(accountId) ?: return@withContext
    dao.updateAccount(acc.copy(nickname = newNickname))
  }

  suspend fun removeAccount(accountId: String) = withContext(Dispatchers.IO) {
    dao.deleteAccountById(accountId)
    dao.deleteFilesForAccount(accountId)
  }

  suspend fun refreshSyncAll(): Int = withContext(Dispatchers.IO) {
    // Re-index / update sync timestamp for all active accounts
    val accounts = mutableListOf<CloudAccountEntity>()
    // Re-generate or freshen file cache
    val existingAccounts = dao.getAccountsCount()
    if (existingAccounts == 0) {
      seedDefaultAccountsAndFiles()
    } else {
      // Refresh timestamps and ensure files are present
      val count = dao.getFilesCount()
      if (count == 0) {
        val currentAccs = mutableListOf<CloudAccountEntity>()
        // Populate sample assets for each existing account
        seedSampleFilesOnly()
      }
    }
    dao.getFilesCount()
  }

  suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
    if (dao.getAccountsCount() == 0) {
      seedDefaultAccountsAndFiles()
    }
  }

  private suspend fun seedDefaultAccountsAndFiles() {
    val defaultAccounts = listOf(
      CloudAccountEntity(
        id = "acc_gdrive_main",
        providerId = CloudProvider.GOOGLE_DRIVE.id,
        nickname = "Work Google Drive",
        identifier = "primary.work@gmail.com",
        totalBytes = 15L * 1024 * 1024 * 1024,
        usedBytes = (10.4 * 1024 * 1024 * 1024).toLong(),
        lastSyncTime = System.currentTimeMillis()
      ),
      CloudAccountEntity(
        id = "acc_terabox_1",
        providerId = CloudProvider.TERABOX.id,
        nickname = "TeraBox Media Vault",
        identifier = "hunter.media.vault",
        totalBytes = 1024L * 1024 * 1024 * 1024,
        usedBytes = (342.8 * 1024 * 1024 * 1024).toLong(),
        lastSyncTime = System.currentTimeMillis()
      ),
      CloudAccountEntity(
        id = "acc_mega_vault",
        providerId = CloudProvider.MEGA.id,
        nickname = "MEGA Zero-Knowledge Drive",
        identifier = "secure.hunter@mega.nz",
        totalBytes = 20L * 1024 * 1024 * 1024,
        usedBytes = (7.1 * 1024 * 1024 * 1024).toLong(),
        lastSyncTime = System.currentTimeMillis()
      ),
      CloudAccountEntity(
        id = "acc_tg_channel_1",
        providerId = CloudProvider.TELEGRAM.id,
        nickname = "Telegram Video Backup (@VaultCloud)",
        identifier = "@HunterCloudStorageBot",
        totalBytes = 250L * 1024 * 1024 * 1024,
        usedBytes = (84.5 * 1024 * 1024 * 1024).toLong(),
        lastSyncTime = System.currentTimeMillis()
      )
    )

    dao.insertAccounts(defaultAccounts)
    for (acc in defaultAccounts) {
      generateMockFilesForAccount(acc)
    }
  }

  private suspend fun seedSampleFilesOnly() {
    val accounts = listOf(
      CloudAccountEntity(
        id = "acc_gdrive_main",
        providerId = CloudProvider.GOOGLE_DRIVE.id,
        nickname = "Work Google Drive",
        identifier = "primary.work@gmail.com",
        totalBytes = 15L * 1024 * 1024 * 1024,
        usedBytes = 10L * 1024 * 1024 * 1024
      )
    )
    for (acc in accounts) {
      generateMockFilesForAccount(acc)
    }
  }

  private suspend fun generateMockFilesForAccount(account: CloudAccountEntity) {
    val now = System.currentTimeMillis()
    val files = when (account.provider) {
      CloudProvider.GOOGLE_DRIVE -> listOf(
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Quarterly_Financial_Report_2026.pdf",
          remotePath = "/Documents/Finance/Quarterly_Financial_Report_2026.pdf",
          fileSizeBytes = 4_850_000L,
          mimeType = "application/pdf",
          category = "docs",
          timestamp = now - (2 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Team_Sprint_Planning_Notes.docx",
          remotePath = "/Documents/Team_Sprint_Planning_Notes.docx",
          fileSizeBytes = 1_220_000L,
          mimeType = "application/msword",
          category = "docs",
          timestamp = now - (6 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Project_Architecture_Blueprint.png",
          remotePath = "/Designs/Project_Architecture_Blueprint.png",
          fileSizeBytes = 8_940_000L,
          mimeType = "image/png",
          category = "photos",
          timestamp = now - (24 * 3600 * 1000),
          isRecent = false
        )
      )
      CloudProvider.TERABOX -> listOf(
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Dhaka_City_Sunset_4K.mp4",
          remotePath = "/Cinematics/Dhaka_City_Sunset_4K.mp4",
          fileSizeBytes = 1_420_000_000L,
          mimeType = "video/mp4",
          category = "videos",
          timestamp = now - (1 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Drone_Aerial_Footage_StMartin.mp4",
          remotePath = "/Cinematics/Drone_Aerial_Footage_StMartin.mp4",
          fileSizeBytes = 2_890_000_000L,
          mimeType = "video/mp4",
          category = "videos",
          timestamp = now - (14 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "CoxsBazar_Beach_Raw_Collection.zip",
          remotePath = "/Backups/CoxsBazar_Beach_Raw_Collection.zip",
          fileSizeBytes = 5_400_000_000L,
          mimeType = "application/zip",
          category = "archives",
          timestamp = now - (3 * 86400 * 1000),
          isRecent = false
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Night_Sky_MilkyWay_LongExposure.jpg",
          remotePath = "/Photos/Night_Sky_MilkyWay_LongExposure.jpg",
          fileSizeBytes = 14_600_000L,
          mimeType = "image/jpeg",
          category = "photos",
          timestamp = now - (4 * 3600 * 1000),
          isRecent = true
        )
      )
      CloudProvider.MEGA -> listOf(
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Encrypted_KeePassXC_SafeVault.kdbx",
          remotePath = "/Security/Encrypted_KeePassXC_SafeVault.kdbx",
          fileSizeBytes = 3_400_000L,
          mimeType = "application/octet-stream",
          category = "docs",
          timestamp = now - (5 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Private_RSA_Keys_Encrypted.tar.gz",
          remotePath = "/Security/Private_RSA_Keys_Encrypted.tar.gz",
          fileSizeBytes = 450_000L,
          mimeType = "application/gzip",
          category = "archives",
          timestamp = now - (48 * 3600 * 1000),
          isRecent = false
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Personal_Documents_Identity_Scans.pdf",
          remotePath = "/Documents/Personal_Documents_Identity_Scans.pdf",
          fileSizeBytes = 18_200_000L,
          mimeType = "application/pdf",
          category = "docs",
          timestamp = now - (8 * 3600 * 1000),
          isRecent = true
        )
      )
      CloudProvider.TELEGRAM -> listOf(
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Tech_Podcast_Episode_84_Lossless.flac",
          remotePath = "/Channel/Tech_Podcast_Episode_84_Lossless.flac",
          fileSizeBytes = 184_000_000L,
          mimeType = "audio/flac",
          category = "audio",
          timestamp = now - (30 * 60 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "OpenSource_Conference_Keynote.mp4",
          remotePath = "/Channel/OpenSource_Conference_Keynote.mp4",
          fileSizeBytes = 720_000_000L,
          mimeType = "video/mp4",
          category = "videos",
          timestamp = now - (12 * 3600 * 1000),
          isRecent = true
        ),
        CloudFileItem(
          accountId = account.id,
          providerId = account.providerId,
          accountNickname = account.nickname,
          fileName = "Telegram_Cloud_Archive_Backup.7z",
          remotePath = "/Channel/Telegram_Cloud_Archive_Backup.7z",
          fileSizeBytes = 1_850_000_000L,
          mimeType = "application/x-7z-compressed",
          category = "archives",
          timestamp = now - (5 * 86400 * 1000),
          isRecent = false
        )
      )
    }
    dao.insertFiles(files)
  }
}
