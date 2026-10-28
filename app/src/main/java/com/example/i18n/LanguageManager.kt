package com.example.i18n

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
  ENGLISH("en", "English", "English"),
  BENGALI("bn", "Bengali", "বাংলা")
}

class LanguageManager(context: Context) {
  private val prefs = context.getSharedPreferences("cloud_hunter_lang_prefs", Context.MODE_PRIVATE)

  var currentLanguage by mutableStateOf(loadLanguage())
    private set

  private fun loadLanguage(): AppLanguage {
    val code = prefs.getString("selected_lang", AppLanguage.ENGLISH.code)
    return if (code == AppLanguage.BENGALI.code) AppLanguage.BENGALI else AppLanguage.ENGLISH
  }

  fun setLanguage(lang: AppLanguage) {
    currentLanguage = lang
    prefs.edit().putString("selected_lang", lang.code).apply()
  }

  fun getString(key: String): String {
    return Translations.get(key, currentLanguage)
  }
}

val LocalLanguageManager = compositionLocalOf<LanguageManager> {
  error("LanguageManager not provided")
}

@Composable
fun localizedString(key: String): String {
  return LocalLanguageManager.current.getString(key)
}
