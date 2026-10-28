package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.example.browser.BrowserManager
import com.example.data.CloudDatabase
import com.example.data.CloudFileItem
import com.example.data.CloudRepository
import com.example.i18n.LanguageManager
import com.example.i18n.LocalLanguageManager
import com.example.i18n.localizedString
import com.example.security.SecurityManager
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GoogleGalleryScreen
import com.example.ui.screens.ManageAccountsScreen
import com.example.ui.screens.MediaViewerDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartSearchScreen
import com.example.ui.theme.AzureBlue
import com.example.ui.theme.CloudHunterTheme
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

enum class AppScreen {
  DASHBOARD,
  GALLERY,
  ACCOUNTS,
  BROWSER,
  SETTINGS,
  SEARCH
}

class MainActivity : FragmentActivity() {
  private lateinit var securityManager: SecurityManager
  private lateinit var repository: CloudRepository
  private lateinit var browserManager: BrowserManager
  private lateinit var languageManager: LanguageManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = CloudDatabase.getDatabase(this)
    repository = CloudRepository(database.cloudDao())
    securityManager = SecurityManager(this)
    browserManager = BrowserManager(this)
    languageManager = LanguageManager(this)

    lifecycleScope.launch {
      repository.ensureInitialized()
    }

    setContent {
      CloudHunterTheme {
        CompositionLocalProvider(LocalLanguageManager provides languageManager) {
          if (!securityManager.isUnlocked) {
            AppLockScreen(
              securityManager = securityManager,
              onUnlocked = { securityManager.unlockApp() }
            )
          } else {
            MainAppScaffold(
              repository = repository,
              securityManager = securityManager,
              browserManager = browserManager,
              languageManager = languageManager,
              onLockApp = { securityManager.lockApp() }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MainAppScaffold(
  repository: CloudRepository,
  securityManager: SecurityManager,
  browserManager: BrowserManager,
  languageManager: LanguageManager,
  onLockApp: () -> Unit
) {
  var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
  var screenStack by remember { mutableStateOf(listOf(AppScreen.DASHBOARD)) }
  var selectedMediaFile by remember { mutableStateOf<CloudFileItem?>(null) }

  fun navigateTo(screen: AppScreen) {
    if (currentScreen != screen) {
      screenStack = screenStack + screen
      currentScreen = screen
    }
  }

  fun navigateBack() {
    if (screenStack.size > 1) {
      val newStack = screenStack.dropLast(1)
      screenStack = newStack
      currentScreen = newStack.last()
    }
  }

  BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
    navigateBack()
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark),
    containerColor = NavyDark,
    bottomBar = {
      if (currentScreen != AppScreen.SEARCH) {
        NavigationBar(
          containerColor = NavySurface,
          contentColor = CyanAccent,
          tonalElevation = 8.dp,
          modifier = Modifier.height(72.dp)
        ) {
          val navItems = listOf(
            Triple(AppScreen.DASHBOARD, localizedString("nav_dashboard"), Icons.Default.Dashboard),
            Triple(AppScreen.GALLERY, localizedString("nav_gallery"), Icons.Default.PhotoLibrary),
            Triple(AppScreen.ACCOUNTS, localizedString("nav_accounts"), Icons.Default.Cloud),
            Triple(AppScreen.BROWSER, localizedString("nav_browser"), Icons.Default.Public),
            Triple(AppScreen.SETTINGS, localizedString("nav_settings"), Icons.Default.Settings)
          )

          navItems.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
              selected = isSelected,
              onClick = { navigateTo(screen) },
              icon = {
                Icon(
                  imageVector = icon,
                  contentDescription = label,
                  modifier = Modifier.size(22.dp)
                )
              },
              label = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyDark,
                selectedTextColor = CyanAccent,
                indicatorColor = CyanAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
              )
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screenTransition"
      ) { screen ->
        when (screen) {
          AppScreen.DASHBOARD -> DashboardScreen(
            repository = repository,
            onNavigateToAccounts = { navigateTo(AppScreen.ACCOUNTS) },
            onNavigateToGallery = { navigateTo(AppScreen.GALLERY) },
            onNavigateToBrowser = { navigateTo(AppScreen.BROWSER) },
            onNavigateToSearch = { navigateTo(AppScreen.SEARCH) },
            onFileClick = { selectedMediaFile = it },
            onLockApp = onLockApp
          )
          AppScreen.GALLERY -> GoogleGalleryScreen(
            repository = repository,
            onNavigateToSearch = { navigateTo(AppScreen.SEARCH) }
          )
          AppScreen.ACCOUNTS -> ManageAccountsScreen(
            repository = repository,
            onNavigateBack = { navigateBack() }
          )
          AppScreen.BROWSER -> BrowserScreen(
            browserManager = browserManager
          )
          AppScreen.SETTINGS -> SettingsScreen(
            securityManager = securityManager,
            repository = repository,
            browserManager = browserManager,
            languageManager = languageManager
          )
          AppScreen.SEARCH -> SmartSearchScreen(
            repository = repository,
            onNavigateBack = { navigateBack() },
            onFileClick = { selectedMediaFile = it }
          )
        }
      }

      // Full-screen media viewer if opened from Dashboard / Search
      selectedMediaFile?.let { file ->
        val context = androidx.compose.ui.platform.LocalContext.current
        MediaViewerDialog(
          file = file,
          onDismiss = { selectedMediaFile = null },
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
}
