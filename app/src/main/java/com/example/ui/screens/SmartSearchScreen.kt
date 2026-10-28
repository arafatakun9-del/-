package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SmartSearchScreen(
  repository: CloudRepository,
  onNavigateBack: () -> Unit,
  onFileClick: (CloudFileItem) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedProvider by remember { mutableStateOf<CloudProvider?>(null) }

  val searchResults by repository.searchLocalFiles(searchQuery, selectedProvider)
    .collectAsState(initial = emptyList())

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      // Top Navigation & Live Search Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(NavySurface)
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text(localizedString("search_hint"), fontSize = 12.sp, color = TextMuted) },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyanAccent)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
              }
            }
          },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = NavyBorder,
            focusedContainerColor = NavySurface,
            unfocusedContainerColor = NavySurface,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )
      }

      // Provider Filter Chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
      ) {
        item {
          val isAll = selectedProvider == null
          FilterChip(
            selected = isAll,
            onClick = { selectedProvider = null },
            label = { Text(text = localizedString("filter_all"), fontSize = 11.sp, color = if (isAll) NavyDark else TextPrimary) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CyanAccent,
              containerColor = NavySurface
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isAll) CyanAccent else NavyBorder,
              enabled = true,
              selected = isAll
            )
          )
        }

        items(CloudProvider.entries) { provider ->
          val isSelected = selectedProvider == provider
          val color = when (provider) {
            CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
            CloudProvider.TERABOX -> ColorTeraBox
            CloudProvider.MEGA -> ColorMega
            CloudProvider.TELEGRAM -> ColorTelegram
          }
          FilterChip(
            selected = isSelected,
            onClick = { selectedProvider = if (isSelected) null else provider },
            label = {
              Text(
                text = provider.displayName,
                fontSize = 11.sp,
                color = if (isSelected) NavyDark else TextPrimary
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = color,
              containerColor = NavySurface
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isSelected) color else NavyBorder,
              enabled = true,
              selected = isSelected
            )
          )
        }
      }

      // Status Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = localizedString("search_instant_results"),
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SkyBlue
        )
        Text(
          text = "${searchResults.size} matches (SQLite Index)",
          fontSize = 11.sp,
          color = TextMuted
        )
      }

      // Results List
      if (searchResults.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = localizedString("search_no_results"),
              fontSize = 13.sp,
              color = TextSecondary
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(searchResults, key = { it.id }) { file ->
            SearchResultCard(file = file, onClick = { onFileClick(file) })
          }
        }
      }
    }
  }
}

@Composable
fun SearchResultCard(
  file: CloudFileItem,
  onClick: () -> Unit
) {
  val providerColor = when (file.provider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

  val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(file.timestamp))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(providerColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Cloud,
          contentDescription = null,
          tint = providerColor,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = file.fileName,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = file.remotePath,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "${formatBytes(file.fileSizeBytes)} • ${file.accountNickname}",
            fontSize = 10.sp,
            color = SkyBlue
          )
          Text(
            text = dateStr,
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }
    }
  }
}
