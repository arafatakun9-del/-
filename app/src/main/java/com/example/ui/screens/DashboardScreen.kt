package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AggregatedStorage
import com.example.data.CloudAccountEntity
import com.example.data.CloudFileItem
import com.example.data.CloudProvider
import com.example.data.CloudRepository
import com.example.i18n.localizedString
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.ColorGoogleDrive
import com.example.ui.theme.ColorMega
import com.example.ui.theme.ColorTelegram
import com.example.ui.theme.ColorTeraBox
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavyHighlight
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
  repository: CloudRepository,
  onNavigateToAccounts: () -> Unit,
  onNavigateToGallery: () -> Unit,
  onNavigateToBrowser: () -> Unit,
  onNavigateToSearch: () -> Unit,
  onFileClick: (CloudFileItem) -> Unit,
  onLockApp: () -> Unit
) {
  val storageStats by repository.aggregatedStorage.collectAsState(
    initial = AggregatedStorage(0L, 0L, 0f, emptyMap())
  )
  val accounts by repository.allAccounts.collectAsState(initial = emptyList())
  val recentFiles by repository.recentFiles.collectAsState(initial = emptyList())
  val coroutineScope = rememberCoroutineScope()
  var isSyncing by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Logo",
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .border(1.5.dp, CyanAccent, CircleShape)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = localizedString("app_name"),
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .clip(CircleShape)
                  .background(SuccessGreen)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "System VPN Inherited",
                fontSize = 11.sp,
                color = SuccessGreen,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateToSearch,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(NavySurface)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = CyanAccent,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = onLockApp,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(NavySurface)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Lock",
              tint = TextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }

    // 5) Unified Global Storage Aggregator Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CloudQueue,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = localizedString("storage_aggregator_title"),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }

            Text(
              text = "${"%.1f".format(storageStats.usedPercentage)}%",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = CyanAccent
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Used vs Total Large Typography
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Column {
              Text(
                text = localizedString("storage_used_total"),
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = formatBytes(storageStats.totalUsedBytes),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = localizedString("storage_total_capacity"),
                fontSize = 12.sp,
                color = TextSecondary
              )
              Text(
                text = formatBytes(storageStats.totalCapacityBytes),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkyBlue
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Clean Unified Horizontal Progress Indicator Bar
          val animatedProgress by animateFloatAsState(
            targetValue = (storageStats.usedPercentage / 100f).coerceIn(0f, 1f),
            label = "storageProgress"
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(CircleShape)
              .background(NavyElevated)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(10.dp)
                .clip(CircleShape)
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(CyanAccent, AzureBlue, ColorGoogleDrive)
                  )
                )
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Provider Breakdown Badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            ProviderPill(
              name = "Drive",
              color = ColorGoogleDrive,
              used = formatBytes(storageStats.providerBreakdown[CloudProvider.GOOGLE_DRIVE]?.usedBytes ?: 0L)
            )
            ProviderPill(
              name = "TeraBox",
              color = ColorTeraBox,
              used = formatBytes(storageStats.providerBreakdown[CloudProvider.TERABOX]?.usedBytes ?: 0L)
            )
            ProviderPill(
              name = "MEGA",
              color = ColorMega,
              used = formatBytes(storageStats.providerBreakdown[CloudProvider.MEGA]?.usedBytes ?: 0L)
            )
            ProviderPill(
              name = "Telegram",
              color = ColorTelegram,
              used = formatBytes(storageStats.providerBreakdown[CloudProvider.TELEGRAM]?.usedBytes ?: 0L)
            )
          }
        }
      }
    }

    // Quick Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickActionButton(
          title = localizedString("nav_accounts"),
          icon = Icons.Default.Cloud,
          color = AzureBlue,
          modifier = Modifier.weight(1f),
          onClick = onNavigateToAccounts
        )
        QuickActionButton(
          title = if (isSyncing) "Syncing..." else localizedString("settings_refresh_sync"),
          icon = Icons.Default.Refresh,
          color = CyanAccent,
          isLoading = isSyncing,
          modifier = Modifier.weight(1f),
          onClick = {
            if (!isSyncing) {
              coroutineScope.launch {
                isSyncing = true
                repository.refreshSyncAll()
                kotlinx.coroutines.delay(1200)
                isSyncing = false
              }
            }
          }
        )
        QuickActionButton(
          title = localizedString("nav_browser"),
          icon = Icons.Default.Public,
          color = SkyBlue,
          modifier = Modifier.weight(1f),
          onClick = onNavigateToBrowser
        )
      }
    }

    // 6) Google Gallery "Recent Media Shelf"
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = localizedString("recent_media"),
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = localizedString("view_all"),
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = CyanAccent,
          modifier = Modifier.clickable { onNavigateToGallery() }
        )
      }
    }

    item {
      if (recentFiles.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = NavySurface),
          shape = RoundedCornerShape(14.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = localizedString("no_media_found"),
              fontSize = 13.sp,
              color = TextSecondary
            )
          }
        }
      } else {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
          items(recentFiles) { file ->
            RecentShelfItem(file = file, onClick = { onFileClick(file) })
          }
        }
      }
    }

    // Active Cloud Nodes Overview
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = localizedString("active_accounts"),
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "${accounts.size} Active",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    }

    items(accounts) { account ->
      CloudAccountCardItem(
        account = account,
        onClick = onNavigateToAccounts
      )
    }
  }
}

@Composable
fun ProviderPill(name: String, color: Color, used: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
    Text(text = used, fontSize = 10.sp, color = TextSecondary)
  }
}

@Composable
fun QuickActionButton(
  title: String,
  icon: ImageVector,
  color: Color,
  isLoading: Boolean = false,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(68.dp)
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    color = NavySurface,
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 2.dp,
          color = CyanAccent
        )
      } else {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = color,
          modifier = Modifier.size(22.dp)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun RecentShelfItem(file: CloudFileItem, onClick: () -> Unit) {
  val providerColor = when (file.provider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

  Card(
    modifier = Modifier
      .width(140.dp)
      .height(160.dp)
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Thumbnail area mimicking Google Gallery
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(100.dp)
          .background(
            Brush.verticalGradient(
              listOf(NavyElevated, NavyHighlight)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (file.category) {
            "videos" -> Icons.Default.Public
            "docs" -> Icons.Default.Folder
            "archives" -> Icons.Default.VpnKey
            "audio" -> Icons.Default.Send
            else -> Icons.Default.CloudDone
          },
          contentDescription = null,
          tint = providerColor,
          modifier = Modifier.size(36.dp)
        )

        // Provider Badge
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(providerColor.copy(alpha = 0.85f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text(
            text = when (file.provider) {
              CloudProvider.GOOGLE_DRIVE -> "Drive"
              CloudProvider.TERABOX -> "Tera"
              CloudProvider.MEGA -> "MEGA"
              CloudProvider.TELEGRAM -> "TG"
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }

      // Metadata
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp)
      ) {
        Text(
          text = file.fileName,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = formatBytes(file.fileSizeBytes),
          fontSize = 10.sp,
          color = TextSecondary
        )
      }
    }
  }
}

@Composable
fun CloudAccountCardItem(
  account: CloudAccountEntity,
  onClick: () -> Unit
) {
  val providerColor = when (account.provider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

  val pct = if (account.totalBytes > 0) {
    (account.usedBytes.toFloat() / account.totalBytes.toFloat()).coerceIn(0f, 1f)
  } else 0f

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(providerColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Cloud,
          contentDescription = null,
          tint = providerColor,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = account.nickname,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "${formatBytes(account.usedBytes)} / ${formatBytes(account.totalBytes)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = SkyBlue
          )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = account.identifier,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { pct },
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(CircleShape),
          color = providerColor,
          trackColor = NavyElevated
        )
      }
    }
  }
}

fun formatBytes(bytes: Long): String {
  if (bytes < 1024) return "$bytes B"
  val kb = bytes / 1024.0
  if (kb < 1024) return "${"%.1f".format(kb)} KB"
  val mb = kb / 1024.0
  if (mb < 1024) return "${"%.1f".format(mb)} MB"
  val gb = mb / 1024.0
  if (gb < 1024) return "${"%.1f".format(gb)} GB"
  val tb = gb / 1024.0
  return "${"%.2f".format(tb)} TB"
}
