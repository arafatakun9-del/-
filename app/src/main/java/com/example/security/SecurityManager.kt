package com.example.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.MessageDigest

class SecurityManager(private val context: Context) {
  private val prefs = context.getSharedPreferences("cloud_hunter_security_prefs", Context.MODE_PRIVATE)

  var isUnlocked by mutableStateOf(false)
    private set

  var isBiometricEnabled by mutableStateOf(prefs.getBoolean("biometric_enabled", true))
    private set

  init {
    if (!prefs.contains("pin_hash")) {
      // Default initial PIN is 1234
      setPin("1234")
    }
  }

  fun validatePin(pin: String): Boolean {
    val hash = hashPin(pin)
    val saved = prefs.getString("pin_hash", "")
    val match = hash == saved
    if (match) {
      isUnlocked = true
    }
    return match
  }

  fun setPin(newPin: String) {
    val hash = hashPin(newPin)
    prefs.edit().putString("pin_hash", hash).apply()
  }

  fun toggleBiometric(enabled: Boolean) {
    isBiometricEnabled = enabled
    prefs.edit().putBoolean("biometric_enabled", enabled).apply()
  }

  fun unlockApp() {
    isUnlocked = true
  }

  fun lockApp() {
    isUnlocked = false
  }

  fun isBiometricHardwareAvailable(): Boolean {
    val biometricManager = BiometricManager.from(context)
    return biometricManager.canAuthenticate(
      BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
    ) == BiometricManager.BIOMETRIC_SUCCESS
  }

  fun authenticateWithBiometrics(
    activity: FragmentActivity,
    title: String,
    subtitle: String,
    cancelLabel: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        unlockApp()
        onSuccess()
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        onError(errString.toString())
      }

      override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        onError("Biometric authentication failed")
      }
    })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle(title)
      .setSubtitle(subtitle)
      .setNegativeButtonText(cancelLabel)
      .setAllowedAuthenticators(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
      )
      .build()

    prompt.authenticate(promptInfo)
  }

  private fun hashPin(pin: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }
}
