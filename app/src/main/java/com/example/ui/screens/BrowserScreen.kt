package com.example.ui.screens

import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.AdBlocker
import com.example.browser.BrowserManager
import com.example.browser.DESKTOP_USER_AGENT
import com.example.browser.MOBILE_USER_AGENT
import com.example.browser.TabItem
import com.example.i18n.LocalLanguageManager
import com.example.i18n.localizedString
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
  browserManager: BrowserManager
) {
  val context = LocalContext.current
  val langManager = LocalLanguageManager.current
  val currentTab = browserManager.currentTab ?: return
  var urlInput by remember(currentTab.id) { mutableStateOf(currentTab.url) }
  var showTabManager by remember { mutableStateOf(false) }
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var loadingProgress by remember { mutableIntStateOf(0) }

  // Back handling inside browser
  BackHandler(enabled = webViewInstance?.canGoBack() == true) {
    webViewInstance?.goBack()
  }

  fun navigateTo(input: String) {
    var target = input.trim()
    if (!target.startsWith("http://") && !target.startsWith("https://")) {
      target = if (target.contains(".") && !target.contains(" ")) {
        "https://$target"
      } else {
        "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(target, "UTF-8")}"
      }
    }
    urlInput = target
    currentTab.url = target
    webViewInstance?.loadUrl(target)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(NavyDark)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Browser Top Bar: Address & Privacy Indicators
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Shield & VPN Status
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(NavyElevated)
                .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Shield",
                tint = CyanAccent,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "${currentTab.blockedAdsCount}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyanAccent
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // URL Search Bar
            OutlinedTextField(
              value = urlInput,
              onValueChange = { urlInput = it },
              singleLine = true,
              placeholder = { Text(localizedString("browser_search_or_url"), fontSize = 11.sp, color = TextMuted) },
              modifier = Modifier.weight(1f),
              trailingIcon = {
                IconButton(onClick = { navigateTo(urlInput) }) {
                  Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go", tint = CyanAccent, modifier = Modifier.size(18.dp))
                }
              },
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = NavyBorder,
                focusedContainerColor = NavyDark,
                unfocusedContainerColor = NavyDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              )
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Desktop / Mobile Mode Toggle
            IconButton(
              onClick = {
                browserManager.toggleDesktopMode(currentTab.id)
                webViewInstance?.settings?.userAgentString = if (currentTab.isDesktopMode) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                webViewInstance?.reload()
              },
              modifier = Modifier.size(34.dp)
            ) {
              Icon(
                imageVector = if (currentTab.isDesktopMode) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
                contentDescription = "Mode",
                tint = if (currentTab.isDesktopMode) CyanAccent else TextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }

            // Tab Switcher Button with count
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(NavyElevated)
                .clickable { showTabManager = true },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${browserManager.tabs.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }
          }

          // Privacy Indicator Sub-bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SuccessGreen))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = localizedString("browser_adblock_active"), fontSize = 9.sp, color = SuccessGreen)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "•", fontSize = 9.sp, color = TextMuted)
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = localizedString("browser_cookies_blocked"), fontSize = 9.sp, color = SkyBlue)
            }

            // Privacy Flush Button
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clickable {
                  browserManager.flushBrowserPrivacy(webViewInstance)
                  Toast.makeText(context, langManager.getString("browser_flush_success"), Toast.LENGTH_SHORT).show()
                }
            ) {
              Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, tint = DangerRed, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(text = localizedString("browser_clear_cookies"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DangerRed)
            }
          }
        }
      }

      // Progress bar
      if (isLoading && loadingProgress in 1..99) {
        LinearProgressIndicator(
          progress = { loadingProgress / 100f },
          modifier = Modifier.fillMaxWidth().height(2.dp),
          color = CyanAccent,
          trackColor = NavyDark
        )
      }

      // Main WebView View
      Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        AndroidView(
          modifier = Modifier.fillMaxSize(),
          factory = { ctx ->
            WebView(ctx).apply {
              layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
              )

              // Strict Anti-Tracking Cookie Policy
              val cookieManager = CookieManager.getInstance()
              cookieManager.setAcceptCookie(true)
              cookieManager.setAcceptThirdPartyCookies(this, false)

              // WebSettings
              settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                userAgentString = if (currentTab.isDesktopMode) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
              }

              // WebViewClient with Built-In AdBlocker
              webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                  if (request != null && AdBlocker.isAdOrTracker(request)) {
                    browserManager.recordBlockedAd(currentTab.id)
                    return AdBlocker.createEmptyResponse()
                  }
                  return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                  super.onPageStarted(view, url, favicon)
                  isLoading = true
                  url?.let {
                    urlInput = it
                    currentTab.url = it
                  }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                  super.onPageFinished(view, url)
                  isLoading = false
                  url?.let {
                    urlInput = it
                    currentTab.url = it
                  }
                  currentTab.title = view?.title ?: "Web Page"
                }
              }

              webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                  super.onProgressChanged(view, newProgress)
                  loadingProgress = newProgress
                  if (newProgress == 100) isLoading = false
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                  super.onReceivedTitle(view, title)
                  if (!title.isNullOrBlank()) {
                    currentTab.title = title
                  }
                }
              }

              loadUrl(currentTab.url)
              webViewInstance = this
            }
          },
          update = { webView ->
            webViewInstance = webView
            // Update user-agent if mode changed
            val targetUA = if (currentTab.isDesktopMode) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
            if (webView.settings.userAgentString != targetUA) {
              webView.settings.userAgentString = targetUA
              webView.reload()
            }
          }
        )
      }

      // Bottom Navigation Controls
      Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 56.dp),
        color = NavySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { webViewInstance?.goBack() },
            enabled = webViewInstance?.canGoBack() == true
          ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = if (webViewInstance?.canGoBack() == true) TextPrimary else TextMuted)
          }

          IconButton(
            onClick = { webViewInstance?.goForward() },
            enabled = webViewInstance?.canGoForward() == true
          ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward", tint = if (webViewInstance?.canGoForward() == true) TextPrimary else TextMuted)
          }

          IconButton(
            onClick = { navigateTo("https://duckduckgo.com") }
          ) {
            Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = CyanAccent)
          }

          IconButton(
            onClick = { webViewInstance?.reload() }
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = TextPrimary)
          }

          IconButton(
            onClick = { browserManager.openNewTab() }
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "New Tab", tint = SkyBlue)
          }
        }
      }
    }

    // Multi-Tab Management Bottom Sheet
    if (showTabManager) {
      ModalBottomSheet(
        onDismissRequest = { showTabManager = false },
        containerColor = NavyElevated
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${localizedString("browser_tabs")} (${browserManager.tabs.size})",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Button(
              onClick = {
                browserManager.openNewTab()
                showTabManager = false
              },
              colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AzureBlue),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = NavyDark, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = localizedString("browser_new_tab"), fontSize = 12.sp, color = NavyDark, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(260.dp)
          ) {
            itemsIndexed(browserManager.tabs) { index, tab ->
              val isSelected = index == browserManager.activeTabIndex
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(110.dp)
                  .clickable {
                    browserManager.selectTab(index)
                    showTabManager = false
                  },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) NavyHighlight else NavySurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else NavyBorder)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                  verticalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Tab #${index + 1}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) CyanAccent else TextSecondary
                    )
                    IconButton(
                      onClick = { browserManager.closeTab(index) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                  }

                  Text(
                    text = tab.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )

                  Text(
                    text = tab.url,
                    fontSize = 9.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
