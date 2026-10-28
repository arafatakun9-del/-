package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.BrowserManager
import com.example.data.CloudRepository
import com.example.i18n.AppLanguage
import com.example.i18n.LanguageManager
import com.example.i18n.localizedString
import com.example.security.SecurityManager
import com.example.ui.theme.AzureBlue
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
fun SettingsScreen(
  securityManager: SecurityManager,
  repository: CloudRepository,
  browserManager: BrowserManager,
  languageManager: LanguageManager
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var showResetPinDialog by remember { mutableStateOf(false) }
  var isSyncing by remember { mutableStateOf(false) }
  var isFlushing by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Title
    item {
      Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
          text = localizedString("settings_title"),
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "Security, synchronization & privacy controls",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    }

    // 1) Security Customization Section
    item {
      SettingsSectionHeader(title = localizedString("settings_security_header"))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
          // Reset Startup Security PIN
          SettingsActionRow(
            icon = Icons.Default.LockReset,
            iconTint = CyanAccent,
            title = localizedString("settings_pin_change"),
            subtitle = localizedString("settings_pin_subtitle"),
            onClick = { showResetPinDialog = true }
          )

          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NavyBorder))

          // Toggle Biometric Fingerprint Authentication
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
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(CyanAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = CyanAccent,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = localizedString("settings_biometric_toggle"),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
                Text(
                  text = localizedString("settings_biometric_subtitle"),
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Switch(
              checked = securityManager.isBiometricEnabled,
              onCheckedChange = { securityManager.toggleBiometric(it) },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NavyDark,
                checkedTrackColor = CyanAccent,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = NavyElevated
              )
            )
          }
        }
      }
    }

    // 2) Cache Synchronization Control Section
    item {
      SettingsSectionHeader(title = localizedString("settings_cache_header"))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(AzureBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Sync,
                  contentDescription = null,
                  tint = AzureBlue,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = localizedString("settings_refresh_sync"),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = localizedString("settings_refresh_sync_sub"),
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Button(
              onClick = {
                if (!isSyncing) {
                  coroutineScope.launch {
                    isSyncing = true
                    val count = repository.refreshSyncAll()
                    kotlinx.coroutines.delay(1000)
                    isSyncing = false
                    Toast.makeText(context, "${languageManager.getString("sync_completed")} ($count files)", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = AzureBlue),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
              if (isSyncing) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = NavyDark,
                  strokeWidth = 2.dp
                )
              } else {
                Text(text = "Rescan", fontSize = 12.sp, color = NavyDark, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 3) Browser Privacy Flush Section
    item {
      SettingsSectionHeader(title = localizedString("settings_privacy_header"))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(DangerRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CleaningServices,
                  contentDescription = null,
                  tint = DangerRed,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = localizedString("settings_privacy_flush"),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = localizedString("settings_privacy_flush_sub"),
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Button(
              onClick = {
                browserManager.flushBrowserPrivacy()
                Toast.makeText(context, languageManager.getString("browser_flush_success"), Toast.LENGTH_SHORT).show()
              },
              colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
              shape = RoundedCornerShape(10.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(text = "Flush Now", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 4) Dual Language Switcher Section (Bengali & English)
    item {
      SettingsSectionHeader(title = localizedString("settings_language_header"))
    }

    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = localizedString("settings_language_sub"),
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            LanguageSelectButton(
              lang = AppLanguage.ENGLISH,
              isSelected = languageManager.currentLanguage == AppLanguage.ENGLISH,
              modifier = Modifier.weight(1f),
              onClick = { languageManager.setLanguage(AppLanguage.ENGLISH) }
            )
            LanguageSelectButton(
              lang = AppLanguage.BENGALI,
              isSelected = languageManager.currentLanguage == AppLanguage.BENGALI,
              modifier = Modifier.weight(1f),
              onClick = { languageManager.setLanguage(AppLanguage.BENGALI) }
            )
          }
        }
      }
    }

    // About App Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = localizedString("settings_about_header"), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = localizedString("settings_about_version"), fontSize = 12.sp, color = SkyBlue)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Hybrid multi-cloud storage aggregator & vault engine with Google Gallery grid and isolated biometric app-lock.",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }
    }
  }

  // Reset Security PIN Dialog
  if (showResetPinDialog) {
    ResetPinDialog(
      onDismiss = { showResetPinDialog = false },
      onConfirm = { newPin ->
        securityManager.setPin(newPin)
        showResetPinDialog = false
        Toast.makeText(context, languageManager.getString("lock_pin_updated"), Toast.LENGTH_SHORT).show()
      }
    )
  }
}

@Composable
fun SettingsSectionHeader(title: String) {
  Text(
    text = title.uppercase(),
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    color = CyanAccent,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
  )
}

@Composable
fun SettingsActionRow(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(CircleShape)
        .background(iconTint.copy(alpha = 0.12f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
      Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
    }
  }
}

@Composable
fun LanguageSelectButton(
  lang: AppLanguage,
  isSelected: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (isSelected) AzureBlue else NavyElevated)
      .border(1.dp, if (isSelected) CyanAccent else NavyBorder, RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 12.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = lang.nativeName,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) NavyDark else TextPrimary
      )
      Text(
        text = lang.displayName,
        fontSize = 11.sp,
        color = if (isSelected) NavyDark.copy(alpha = 0.8f) else TextSecondary
      )
    }
  }
}

@Composable
fun ResetPinDialog(
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit
) {
  var newPin by remember { mutableStateOf("") }
  var confirmPin by remember { mutableStateOf("") }
  var error by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = localizedString("lock_reset_pin_title"),
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = newPin,
          onValueChange = { if (it.length <= 4) newPin = it },
          label = { Text(localizedString("lock_enter_new_pin")) },
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
          value = confirmPin,
          onValueChange = { if (it.length <= 4) confirmPin = it },
          label = { Text(localizedString("lock_confirm_new_pin")) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = NavyBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        if (error.isNotEmpty()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = error, fontSize = 12.sp, color = DangerRed)
        }
      }
    },
    containerColor = NavyElevated,
    confirmButton = {
      Button(
        onClick = {
          if (newPin.length < 4) {
            error = "PIN must be 4 digits"
          } else if (newPin != confirmPin) {
            error = "PINs do not match"
          } else {
            onConfirm(newPin)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AzureBlue)
      ) {
        Text(text = localizedString("save"), color = NavyDark, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(text = localizedString("cancel"), color = TextSecondary)
      }
    }
  )
}
