package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoogleGalleryScreen(
  repository: CloudRepository,
  onNavigateToSearch: () -> Unit
) {
  val context = LocalContext.current
  var selectedCategory by remember { mutableStateOf("all") }
  val files by repository.getFilesByCategory(selectedCategory).collectAsState(initial = emptyList())
  val recentFiles by repository.recentFiles.collectAsState(initial = emptyList())

  var viewingFile by remember { mutableStateOf<CloudFileItem?>(null) }

  val categories = listOf(
    "all" to localizedString("filter_all"),
    "photos" to localizedString("filter_photos"),
    "videos" to localizedString("filter_videos"),
    "docs" to localizedString("filter_docs"),
    "audio" to localizedString("filter_audio"),
    "archives" to localizedString("filter_archives")
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Top Bar (Google Gallery Style - Clean, Minimal, Modern)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = localizedString("gallery_title"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "${files.size} media items indexed",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        IconButton(
          onClick = onNavigateToSearch,
          modifier = Modifier
            .size(38.dp)
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
      }

      // Filter Chips
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { (catId, label) ->
          val isSelected = selectedCategory == catId
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = catId },
            label = {
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NavyDark else TextPrimary
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CyanAccent,
              containerColor = NavySurface
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isSelected) CyanAccent else NavyBorder,
              enabled = true,
              selected = isSelected
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Minimal Grid Layout (Borderless, large preview tiles)
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Shelf at top for Recently Added Media
        if (selectedCategory == "all" && recentFiles.isNotEmpty()) {
          item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
              Text(
                text = localizedString("recent_media"),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
              )
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                items(recentFiles) { file ->
                  RecentShelfItem(file = file, onClick = { viewingFile = file })
                }
              }
            }
          }
        }

        if (files.isEmpty()) {
          item(span = { GridItemSpan(2) }) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = localizedString("no_media_found"),
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
              )
            }
          }
        } else {
          items(files, key = { it.id }) { file ->
            GoogleGalleryGridCard(
              file = file,
              onClick = { viewingFile = file }
            )
          }
        }
      }
    }

    // Full-screen Media Viewer Dialog
    viewingFile?.let { file ->
      MediaViewerDialog(
        file = file,
        onDismiss = { viewingFile = null },
        onShare = {
          val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Cloud Hunter Media: ${file.fileName}\nPath: ${file.remotePath}\nProvider: ${file.provider.displayName}")
            type = "text/plain"
          }
          context.startActivity(Intent.createChooser(sendIntent, "Share Cloud Asset"))
        }
      )
    }
  }
}

@Composable
fun GoogleGalleryGridCard(
  file: CloudFileItem,
  onClick: () -> Unit
) {
  val providerColor = when (file.provider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

  // Google Gallery style: Large minimal thumbnail with scrim and clean typography
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(0.92f)
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Borderless & shadowless
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Background / Preview Art
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(NavyElevated, Color(0xFF14243B), NavyHighlight)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (file.category) {
            "photos" -> Icons.Default.Image
            "videos" -> Icons.Default.VideoLibrary
            "docs" -> Icons.Default.Description
            "audio" -> Icons.Default.MusicNote
            "archives" -> Icons.Default.Archive
            else -> Icons.Default.Article
          },
          contentDescription = null,
          tint = providerColor.copy(alpha = 0.8f),
          modifier = Modifier.size(46.dp)
        )

        // Video Play Overlay Badge
        if (file.category == "videos") {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Play",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      // Top Cloud Tag Pill
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(NavyDark.copy(alpha = 0.8f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = when (file.provider) {
            CloudProvider.GOOGLE_DRIVE -> "Drive"
            CloudProvider.TERABOX -> "TeraBox"
            CloudProvider.MEGA -> "MEGA"
            CloudProvider.TELEGRAM -> "Telegram"
          },
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          color = providerColor
        )
      }

      // Bottom Scrim Overlay with Title & File Size
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
            )
          )
          .padding(horizontal = 10.dp, vertical = 8.dp)
      ) {
        Column {
          Text(
            text = file.fileName,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "${formatBytes(file.fileSizeBytes)} • ${file.accountNickname}",
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }
  }
}

@Composable
fun MediaViewerDialog(
  file: CloudFileItem,
  onDismiss: () -> Unit,
  onShare: () -> Unit
) {
  val providerColor = when (file.provider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

  val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(file.timestamp))

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.95f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Toolbar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(NavySurface)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextPrimary
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
              onClick = onShare,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(NavySurface)
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = CyanAccent
              )
            }
          }
        }

        // Center Preview Stage
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = when (file.category) {
                "photos" -> Icons.Default.Image
                "videos" -> Icons.Default.VideoLibrary
                "docs" -> Icons.Default.Description
                "audio" -> Icons.Default.MusicNote
                else -> Icons.Default.Archive
              },
              contentDescription = null,
              tint = providerColor,
              modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = file.fileName,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              textAlign = TextAlign.Center
            )
          }
        }

        // Bottom Info Sheet
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(text = localizedString("file_cloud"), fontSize = 11.sp, color = TextSecondary)
                Text(text = file.provider.displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = providerColor)
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(text = localizedString("file_size"), fontSize = 11.sp, color = TextSecondary)
                Text(text = formatBytes(file.fileSizeBytes), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Remote Path", fontSize = 11.sp, color = TextSecondary)
            Text(text = file.remotePath, fontSize = 12.sp, color = SkyBlue)

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = localizedString("file_date"), fontSize = 11.sp, color = TextSecondary)
            Text(text = dateStr, fontSize = 12.sp, color = TextPrimary)
          }
        }
      }
    }
  }
}
