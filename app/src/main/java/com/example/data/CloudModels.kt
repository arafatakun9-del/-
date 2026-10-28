package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class CloudProvider(
  val id: String,
  val displayName: String,
  val defaultQuotaGb: Long,
  val iconName: String
) {
  GOOGLE_DRIVE("GOOGLE_DRIVE", "Google Drive", 15L, "gdrive"),
  TERABOX("TERABOX", "TeraBox Cloud", 1024L, "terabox"),
  MEGA("MEGA", "MEGA Encrypted", 20L, "mega"),
  TELEGRAM("TELEGRAM", "Telegram Channels", 100L, "telegram");

  companion object {
    fun fromId(id: String): CloudProvider {
      return entries.find { it.id.equals(id, ignoreCase = true) } ?: GOOGLE_DRIVE
    }
  }
}

@Entity(tableName = "cloud_accounts")
data class CloudAccountEntity(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val providerId: String,
  val nickname: String,
  val identifier: String,
  val totalBytes: Long,
  val usedBytes: Long,
  val isActive: Boolean = true,
  val lastSyncTime: Long = System.currentTimeMillis()
) {
  val provider: CloudProvider
    get() = CloudProvider.fromId(providerId)
}

@Entity(tableName = "cloud_files")
data class CloudFileItem(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val accountId: String,
  val providerId: String,
  val accountNickname: String,
  val fileName: String,
  val remotePath: String,
  val fileSizeBytes: Long,
  val mimeType: String,
  val category: String, // "photos", "videos", "docs", "audio", "archives"
  val timestamp: Long,
  val isRecent: Boolean = false,
  val previewThumbnail: String = "",
  val localCached: Boolean = true
) {
  val provider: CloudProvider
    get() = CloudProvider.fromId(providerId)
}
