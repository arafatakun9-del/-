package com.example.browser

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlocker {
  private val adDomains = hashSetOf(
    "doubleclick.net",
    "googleadservices.com",
    "googlesyndication.com",
    "adservice.google.com",
    "ads.pubmatic.com",
    "adnxs.com",
    "scorecardresearch.com",
    "outbrain.com",
    "taboola.com",
    "criteo.com",
    "adcolony.com",
    "applovin.com",
    "unityads.unity3d.com",
    "popads.net",
    "propellerads.com",
    "moatads.com",
    "bidswitch.net",
    "rubiconproject.com",
    "openx.net",
    "smartadserver.com",
    "quantserve.com",
    "chartbeat.com",
    "adform.net",
    "amazon-adsystem.com",
    "casalemedia.com"
  )

  private val trackingKeywords = listOf(
    "/ads/", "/adserver/", "/tracker/", "/telemetry/", "google-analytics.com",
    "analytics.", "pixel.", "statcounter.com", "hotjar.com", "facebook.com/tr"
  )

  fun isAdOrTracker(request: WebResourceRequest): Boolean {
    val host = request.url.host?.lowercase() ?: return false
    val urlStr = request.url.toString().lowercase()

    // Host check
    for (adHost in adDomains) {
      if (host == adHost || host.endsWith(".$adHost")) {
        return true
      }
    }

    // Keyword & Path check
    for (kw in trackingKeywords) {
      if (urlStr.contains(kw)) {
        return true
      }
    }

    return false
  }

  fun createEmptyResponse(): WebResourceResponse {
    return WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(ByteArray(0)))
  }
}
