package com.example.browser

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

data class TabItem(
  val id: String = UUID.randomUUID().toString(),
  var title: String = "New Tab",
  var url: String = "https://duckduckgo.com",
  var isDesktopMode: Boolean = false,
  var isLoading: Boolean = false,
  var progress: Int = 0,
  var canGoBack: Boolean = false,
  var canGoForward: Boolean = false,
  var blockedAdsCount: Int = 0
)

class BrowserManager(private val context: Context) {
  val tabs = mutableStateListOf<TabItem>()
  var activeTabIndex by mutableIntStateOf(0)
    private set

  var privacyFlushTimestamp by mutableStateOf(System.currentTimeMillis())

  init {
    // Open initial clean private tab
    tabs.add(
      TabItem(
        title = "DuckDuckGo Privacy Search",
        url = "https://duckduckgo.com",
        isDesktopMode = false
      )
    )
  }

  val currentTab: TabItem?
    get() = if (tabs.isNotEmpty() && activeTabIndex in tabs.indices) tabs[activeTabIndex] else null

  fun openNewTab(url: String = "https://duckduckgo.com") {
    val newTab = TabItem(
      title = "New Tab",
      url = url,
      isDesktopMode = false
    )
    tabs.add(newTab)
    activeTabIndex = tabs.size - 1
  }

  fun closeTab(index: Int) {
    if (tabs.size <= 1) {
      // If closing last tab, reset to clean blank/home
      tabs[0] = TabItem(
        title = "DuckDuckGo Privacy Search",
        url = "https://duckduckgo.com",
        isDesktopMode = false
      )
      activeTabIndex = 0
    } else {
      tabs.removeAt(index)
      if (activeTabIndex >= tabs.size) {
        activeTabIndex = tabs.size - 1
      }
    }
  }

  fun selectTab(index: Int) {
    if (index in tabs.indices) {
      activeTabIndex = index
    }
  }

  fun toggleDesktopMode(tabId: String) {
    val idx = tabs.indexOfFirst { it.id == tabId }
    if (idx != -1) {
      val tab = tabs[idx]
      tabs[idx] = tab.copy(isDesktopMode = !tab.isDesktopMode)
    }
  }

  fun recordBlockedAd(tabId: String) {
    val idx = tabs.indexOfFirst { it.id == tabId }
    if (idx != -1) {
      val tab = tabs[idx]
      tabs[idx] = tab.copy(blockedAdsCount = tab.blockedAdsCount + 1)
    }
  }

  fun flushBrowserPrivacy(webView: WebView? = null) {
    // Wipe all WebView cookies
    val cookieManager = CookieManager.getInstance()
    cookieManager.removeAllCookies(null)
    cookieManager.flush()

    // Wipe WebStorage (localStorage, sessionStorage, indexedDB)
    WebStorage.getInstance().deleteAllData()

    // Clear WebView cache and history
    webView?.clearCache(true)
    webView?.clearHistory()
    webView?.clearFormData()

    privacyFlushTimestamp = System.currentTimeMillis()
  }
}

val DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
val MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
