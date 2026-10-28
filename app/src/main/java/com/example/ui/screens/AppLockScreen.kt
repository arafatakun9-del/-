package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.i18n.localizedString
import com.example.security.SecurityManager
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppLockScreen(
  securityManager: SecurityManager,
  onUnlocked: () -> Unit
) {
  val context = LocalContext.current
  var inputPin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }

  // Auto trigger biometrics if available and enabled
  LaunchedEffect(Unit) {
    if (securityManager.isBiometricEnabled && securityManager.isBiometricHardwareAvailable()) {
      val activity = context as? FragmentActivity
      if (activity != null) {
        securityManager.authenticateWithBiometrics(
          activity = activity,
          title = "Cloud Hunter Security",
          subtitle = "Biometric fingerprint authentication",
          cancelLabel = "Use PIN",
          onSuccess = { onUnlocked() },
          onError = { /* fallback to pin */ }
        )
      }
    }
  }

  fun handleDigit(digit: String) {
    if (inputPin.length < 4) {
      val next = inputPin + digit
      inputPin = next
      isError = false
      if (next.length == 4) {
        if (securityManager.validatePin(next)) {
          onUnlocked()
        } else {
          isError = true
          errorMessage = "Incorrect PIN. Please try again."
          inputPin = ""
        }
      }
    }
  }

  fun handleBackspace() {
    if (inputPin.isNotEmpty()) {
      inputPin = inputPin.dropLast(1)
      isError = false
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(NavyDark, Color(0xFF071120))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 28.dp, vertical = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Header & Icon
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(NavySurface)
            .border(2.dp, AzureBlue.copy(alpha = 0.5f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Shield",
            tint = CyanAccent,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = localizedString("app_name"),
          fontSize = 26.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = localizedString("lock_enter_pin"),
          fontSize = 14.sp,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // PIN Indicator Dots
        Row(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 0 until 4) {
            val filled = i < inputPin.length
            Box(
              modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(
                  if (isError) DangerRed
                  else if (filled) CyanAccent
                  else NavyElevated
                )
                .border(
                  width = 1.5.dp,
                  color = if (isError) DangerRed else if (filled) CyanAccent else NavyBorder,
                  shape = CircleShape
                )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(visible = isError, enter = fadeIn(), exit = fadeOut()) {
          Text(
            text = localizedString("lock_wrong_pin"),
            color = DangerRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Numeric Keypad
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val rows = listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9"),
          listOf("BIO", "0", "DEL")
        )

        for (row in rows) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            for (key in row) {
              when (key) {
                "DEL" -> {
                  KeypadButton(
                    onClick = { handleBackspace() }
                  ) {
                    Icon(
                      imageVector = Icons.Default.Backspace,
                      contentDescription = "Backspace",
                      tint = TextSecondary,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                "BIO" -> {
                  if (securityManager.isBiometricHardwareAvailable()) {
                    KeypadButton(
                      onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                          securityManager.authenticateWithBiometrics(
                            activity = activity,
                            title = "Cloud Hunter",
                            subtitle = "Scan your fingerprint to unlock",
                            cancelLabel = "Cancel",
                            onSuccess = { onUnlocked() },
                            onError = { /* fallback */ }
                          )
                        }
                      }
                    ) {
                      Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometrics",
                        tint = CyanAccent,
                        modifier = Modifier.size(30.dp)
                      )
                    }
                  } else {
                    Spacer(modifier = Modifier.size(72.dp))
                  }
                }
                else -> {
                  KeypadButton(
                    onClick = { handleDigit(key) }
                  ) {
                    Text(
                      text = key,
                      fontSize = 26.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = TextPrimary
                    )
                  }
                }
              }
            }
          }
        }
      }

      // Footer
      Text(
        text = "256-BIT ISOLATED APP-LOCK SECURITY",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.2.sp
      )
    }
  }
}

@Composable
private fun KeypadButton(
  onClick: () -> Unit,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = Modifier
      .size(72.dp)
      .clip(CircleShape)
      .clickable { onClick() },
    shape = CircleShape,
    color = NavySurface,
    tonalElevation = 2.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      content()
    }
  }
}
