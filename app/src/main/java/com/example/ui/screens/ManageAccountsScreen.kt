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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CloudAccountEntity
import com.example.data.CloudProvider
import com.example.data.CloudRepository
import com.example.i18n.localizedString
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.ColorGoogleDrive
import com.example.ui.theme.ColorMega
import com.example.ui.theme.ColorTelegram
import com.example.ui.theme.ColorTeraBox
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
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

@Composable
fun ManageAccountsScreen(
  repository: CloudRepository,
  onNavigateBack: () -> Unit
) {
  val allAccounts by repository.allAccounts.collectAsState(initial = emptyList())
  val coroutineScope = rememberCoroutineScope()

  var selectedTabIdx by remember { mutableIntStateOf(0) }
  val providers = CloudProvider.entries

  var showAddDialogForProvider by remember { mutableStateOf<CloudProvider?>(null) }
  var accountToEditNickname by remember { mutableStateOf<CloudAccountEntity?>(null) }
  var accountToDelete by remember { mutableStateOf<CloudAccountEntity?>(null) }

  val currentProvider = providers[selectedTabIdx]
  val currentAccounts = allAccounts.filter { it.provider == currentProvider }

  val currentProviderColor = when (currentProvider) {
    CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
    CloudProvider.TERABOX -> ColorTeraBox
    CloudProvider.MEGA -> ColorMega
    CloudProvider.TELEGRAM -> ColorTelegram
  }

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
      // Top AppBar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, bottom = 12.dp),
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
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = localizedString("accounts_title"),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = localizedString("accounts_subtitle"),
            fontSize = 11.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Provider Category Tabs
      ScrollableTabRow(
        selectedTabIndex = selectedTabIdx,
        containerColor = NavyDark,
        contentColor = CyanAccent,
        edgePadding = 0.dp,
        divider = {},
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
            color = currentProviderColor,
            height = 3.dp
          )
        }
      ) {
        providers.forEachIndexed { index, provider ->
          val color = when (provider) {
            CloudProvider.GOOGLE_DRIVE -> ColorGoogleDrive
            CloudProvider.TERABOX -> ColorTeraBox
            CloudProvider.MEGA -> ColorMega
            CloudProvider.TELEGRAM -> ColorTelegram
          }
          val count = allAccounts.count { it.provider == provider }
          Tab(
            selected = selectedTabIdx == index,
            onClick = { selectedTabIdx = index },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = when (provider) {
                    CloudProvider.GOOGLE_DRIVE -> localizedString("category_gdrive")
                    CloudProvider.TERABOX -> localizedString("category_terabox")
                    CloudProvider.MEGA -> localizedString("category_mega")
                    CloudProvider.TELEGRAM -> localizedString("category_telegram")
                  },
                  fontSize = 13.sp,
                  fontWeight = if (selectedTabIdx == index) FontWeight.Bold else FontWeight.Medium,
                  color = if (selectedTabIdx == index) TextPrimary else TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selectedTabIdx == index) color.copy(alpha = 0.2f) else NavySurface)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "$count",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTabIdx == index) color else TextMuted
                  )
                }
              }
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Provider Module Banner Card with Clear "+" Button
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = currentProvider.displayName,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Supports concurrent multi-instance nodes (10+ accounts)",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }

          // Clear "+" Add Account Button
          Button(
            onClick = { showAddDialogForProvider = currentProvider },
            colors = ButtonDefaults.buttonColors(containerColor = currentProviderColor),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = localizedString("add_account"),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // List of Account Instances
      if (currentAccounts.isEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Cloud,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = localizedString("no_accounts"),
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(currentAccounts, key = { it.id }) { acc ->
            AccountRowCard(
              account = acc,
              providerColor = currentProviderColor,
              onEditNickname = { accountToEditNickname = acc },
              onDelete = { accountToDelete = acc }
            )
          }
        }
      }
    }

    // Dialog: Add Account Connection
    showAddDialogForProvider?.let { provider ->
      AddAccountDialog(
        provider = provider,
        onDismiss = { showAddDialogForProvider = null },
        onConfirm = { nickname, identifier, quotaGb ->
          coroutineScope.launch {
            repository.addAccount(provider, nickname, identifier, quotaGb)
            showAddDialogForProvider = null
          }
        }
      )
    }

    // Dialog: Edit Nickname
    accountToEditNickname?.let { acc ->
      EditNicknameDialog(
        currentNickname = acc.nickname,
        onDismiss = { accountToEditNickname = null },
        onConfirm = { newName ->
          coroutineScope.launch {
            repository.updateNickname(acc.id, newName)
            accountToEditNickname = null
          }
        }
      )
    }

    // Dialog: Remove Account Confirmation
    accountToDelete?.let { acc ->
      AlertDialog(
        onDismissRequest = { accountToDelete = null },
        title = { Text(text = localizedString("btn_remove"), color = TextPrimary) },
        text = { Text(text = localizedString("confirm_remove_account"), color = TextSecondary) },
        containerColor = NavyElevated,
        confirmButton = {
          Button(
            onClick = {
              coroutineScope.launch {
                repository.removeAccount(acc.id)
                accountToDelete = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
          ) {
            Text(text = localizedString("delete"), color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { accountToDelete = null }) {
            Text(text = localizedString("cancel"), color = TextSecondary)
          }
        }
      )
    }
  }
}

@Composable
fun AccountRowCard(
  account: CloudAccountEntity,
  providerColor: Color,
  onEditNickname: () -> Unit,
  onDelete: () -> Unit
) {
  val pct = if (account.totalBytes > 0) {
    (account.usedBytes.toFloat() / account.totalBytes.toFloat()).coerceIn(0f, 1f)
  } else 0f

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = NavySurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(providerColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Cloud,
              contentDescription = null,
              tint = providerColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = account.nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              IconButton(
                onClick = onEditNickname,
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit Nickname",
                  tint = TextSecondary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Text(
              text = account.identifier,
              fontSize = 11.sp,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Action Buttons
        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Remove",
            tint = DangerRed.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quota Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${formatBytes(account.usedBytes)} of ${formatBytes(account.totalBytes)} used",
          fontSize = 11.sp,
          color = TextSecondary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(SuccessGreen)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = localizedString("account_status_active"),
            fontSize = 10.sp,
            color = SuccessGreen,
            fontWeight = FontWeight.Medium
          )
        }
      }

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

@Composable
fun AddAccountDialog(
  provider: CloudProvider,
  onDismiss: () -> Unit,
  onConfirm: (nickname: String, identifier: String, quotaGb: Long) -> Unit
) {
  var nickname by remember { mutableStateOf("") }
  var identifier by remember { mutableStateOf("") }
  var selectedQuotaGb by remember { mutableStateOf(provider.defaultQuotaGb) }

  val quotaPresets = when (provider) {
    CloudProvider.GOOGLE_DRIVE -> listOf(15L, 100L, 2048L)
    CloudProvider.TERABOX -> listOf(1024L, 2048L)
    CloudProvider.MEGA -> listOf(20L, 400L, 2048L)
    CloudProvider.TELEGRAM -> listOf(50L, 100L, 500L)
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "${localizedString("add_account")} - ${provider.displayName}",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = nickname,
          onValueChange = { nickname = it },
          label = { Text(localizedString("account_nickname")) },
          placeholder = { Text("e.g. Personal ${provider.displayName}") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = NavyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = identifier,
          onValueChange = { identifier = it },
          label = { Text(localizedString("account_identifier")) },
          placeholder = { Text(if (provider == CloudProvider.TELEGRAM) "@channel_name / Bot Token" else "account@email.com") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = NavyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = localizedString("account_quota"),
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (q in quotaPresets) {
            val isSelected = selectedQuotaGb == q
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) AzureBlue else NavyDark)
                .clickable { selectedQuotaGb = q }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (q >= 1024) "${q / 1024} TB" else "$q GB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) NavyDark else TextPrimary
              )
            }
          }
        }
      }
    },
    containerColor = NavyElevated,
    confirmButton = {
      Button(
        onClick = {
          onConfirm(
            if (nickname.isNotBlank()) nickname else "${provider.displayName} Instance",
            if (identifier.isNotBlank()) identifier else "node_${(Math.random()*9000).toInt()}",
            selectedQuotaGb
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = AzureBlue)
      ) {
        Text(text = localizedString("btn_add_connection"), color = NavyDark, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = localizedString("cancel"), color = TextSecondary)
      }
    }
  )
}

@Composable
fun EditNicknameDialog(
  currentNickname: String,
  onDismiss: () -> Unit,
  onConfirm: (newName: String) -> Unit
) {
  var name by remember { mutableStateOf(currentNickname) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = localizedString("btn_edit_nickname"),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(localizedString("account_nickname")) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = CyanAccent,
          unfocusedBorderColor = NavyBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )
    },
    containerColor = NavyElevated,
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) onConfirm(name)
        },
        colors = ButtonDefaults.buttonColors(containerColor = AzureBlue)
      ) {
        Text(text = localizedString("save"), color = NavyDark)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = localizedString("cancel"), color = TextSecondary)
      }
    }
  )
}
