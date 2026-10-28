package com.example.i18n

object Translations {
  private val en = mapOf(
    // App & Navigation
    "app_name" to "Cloud Hunter",
    "app_tagline" to "Unified Multi-Cloud Engine & Vault",
    "nav_dashboard" to "Dashboard",
    "nav_gallery" to "Gallery",
    "nav_accounts" to "Accounts",
    "nav_browser" to "Browser",
    "nav_settings" to "Settings",
    "nav_search" to "Search",

    // Dashboard & Aggregator
    "storage_aggregator_title" to "Global Cloud Storage",
    "storage_used_total" to "Used across network",
    "storage_total_capacity" to "Total Capacity",
    "storage_free" to "Free",
    "active_accounts" to "Active Connections",
    "recent_media" to "Recent Media Shelf",
    "view_all" to "View All",
    "quick_sync" to "Sync All Clouds",
    "resyncing" to "Rescanning cloud assets...",
    "sync_completed" to "Local index up to date",

    // Accounts Screen
    "accounts_title" to "Manage Accounts",
    "accounts_subtitle" to "Concurrent multi-account management with unlimited nodes",
    "add_account" to "Add Account",
    "category_gdrive" to "Google Drive",
    "category_terabox" to "TeraBox Cloud",
    "category_mega" to "MEGA Encrypted",
    "category_telegram" to "Telegram Channels",
    "account_nickname" to "Account Nickname",
    "account_identifier" to "Email / Identifier / Channel ID",
    "account_quota" to "Quota Allocation",
    "account_status_active" to "Active & Synced",
    "account_status_connecting" to "Syncing...",
    "btn_add_connection" to "Add Storage Connection",
    "btn_remove" to "Remove",
    "btn_edit_nickname" to "Edit Nickname",
    "confirm_remove_account" to "Remove this cloud connection and clear its cached index?",
    "cancel" to "Cancel",
    "save" to "Save",
    "delete" to "Delete",
    "no_accounts" to "No accounts connected in this category. Tap '+' to connect.",

    // Gallery Screen
    "gallery_title" to "Google Gallery",
    "filter_all" to "All",
    "filter_photos" to "Photos",
    "filter_videos" to "Videos",
    "filter_docs" to "Documents",
    "filter_audio" to "Audio",
    "filter_archives" to "Archives",
    "file_size" to "Size",
    "file_date" to "Date",
    "file_cloud" to "Provider",
    "file_download" to "Download",
    "file_share" to "Share",
    "file_delete" to "Delete File",
    "no_media_found" to "No media files cached yet. Tap 'Refresh Sync' to scan.",

    // Search Screen
    "search_hint" to "Search file names and dates in local cache...",
    "search_instant_results" to "Local Cache Instant Results",
    "search_no_results" to "No matching cloud files found in local index",
    "search_filter_provider" to "Filter by Cloud",

    // Web Browser Screen
    "browser_title" to "Via Browser",
    "browser_subtitle" to "Ultra-lightweight, Anti-tracking & Ad-free",
    "browser_search_or_url" to "Search or enter web URL...",
    "browser_desktop_mode" to "Desktop Mode",
    "browser_mobile_mode" to "Mobile Mode",
    "browser_adblock_active" to "Ad-Blocker Active",
    "browser_cookies_blocked" to "3rd-Party Trackers Blocked",
    "browser_vpn_inherited" to "Inheriting System VPN",
    "browser_new_tab" to "New Tab",
    "browser_close_tab" to "Close Tab",
    "browser_tabs" to "Tabs",
    "browser_clear_cookies" to "Privacy Flush",
    "browser_flush_success" to "All browsing cache, cookies & tracking data wiped!",

    // Settings Screen
    "settings_title" to "App Settings",
    "settings_security_header" to "Security & App-Lock",
    "settings_pin_change" to "Reset Security PIN",
    "settings_pin_subtitle" to "Update your 4-digit startup lock PIN",
    "settings_biometric_toggle" to "Biometric Fingerprint Unlock",
    "settings_biometric_subtitle" to "Prompt for fingerprint on application launch",
    "settings_cache_header" to "Cache Synchronization",
    "settings_refresh_sync" to "Refresh Sync",
    "settings_refresh_sync_sub" to "Rescan and index all connected cloud nodes into Room database",
    "settings_privacy_header" to "Browser Privacy Control",
    "settings_privacy_flush" to "Browser Privacy Flush",
    "settings_privacy_flush_sub" to "Instantly wipe WebView history, cookies, cached scripts & DOM data",
    "settings_language_header" to "Interface Language / ভাষা",
    "settings_language_sub" to "Instant dynamic interface language switching",
    "settings_about_header" to "About Cloud Hunter",
    "settings_about_version" to "Version 1.0.0 (Production Build)",

    // App Lock Screen
    "lock_title" to "Security Lock",
    "lock_enter_pin" to "Enter Security PIN to Access Cloud Hunter",
    "lock_wrong_pin" to "Incorrect PIN. Please try again.",
    "lock_use_biometrics" to "Unlock with Biometrics",
    "lock_reset_pin_title" to "Set New Security PIN",
    "lock_enter_new_pin" to "Enter new 4-digit PIN",
    "lock_confirm_new_pin" to "Confirm new 4-digit PIN",
    "lock_pin_mismatch" to "PINs do not match",
    "lock_pin_updated" to "Security PIN successfully updated!",
    "lock_biometric_prompt_title" to "Cloud Hunter Biometric Authentication",
    "lock_biometric_prompt_subtitle" to "Scan your fingerprint to unlock"
  )

  private val bn = mapOf(
    // App & Navigation
    "app_name" to "ক্লাউড হান্টার",
    "app_tagline" to "ইউনিফাইড মাল্টি-ক্লাউড ইঞ্জিন ও ভল্ট",
    "nav_dashboard" to "ড্যাশবোর্ড",
    "nav_gallery" to "গ্যালারি",
    "nav_accounts" to "অ্যাকাউন্টস",
    "nav_browser" to "ব্রাউজার",
    "nav_settings" to "সেটিংস",
    "nav_search" to "অনুসন্ধান",

    // Dashboard & Aggregator
    "storage_aggregator_title" to "গ্লোবাল ক্লাউড স্টোরেজ",
    "storage_used_total" to "নেটওয়ার্কে মোট ব্যবহৃত জায়গা",
    "storage_total_capacity" to "সর্বমোট ধারণক্ষমতা",
    "storage_free" to "খালি আছে",
    "active_accounts" to "সক্রিয় কানেকশনসমূহ",
    "recent_media" to "সাম্প্রতিক মিডিয়া শেلف",
    "view_all" to "সব দেখুন",
    "quick_sync" to "সব ক্লাউড সিঙ্ক করুন",
    "resyncing" to "ক্লাউড ফাইল ইন্ডেক্স করা হচ্ছে...",
    "sync_completed" to "লোকাল ডাটাবেস সম্পূর্ণ আপডেট",

    // Accounts Screen
    "accounts_title" to "অ্যাকাউন্ট ব্যবস্থাপনা",
    "accounts_subtitle" to "একসাথে একাধিক একাউন্ট যুক্ত করুন (আনলিমিটেড)",
    "add_account" to "অ্যাকাউন্ট যোগ করুন",
    "category_gdrive" to "গুগল ড্রাইভ",
    "category_terabox" to "টেরাবক্স ক্লাউড",
    "category_mega" to "মেগা এনক্রিপ্টেড",
    "category_telegram" to "টেলিগ্রাম চ্যানেল স্টোরেজ",
    "account_nickname" to "অ্যাকাউন্টের ডাকনাম (Nickname)",
    "account_identifier" to "ইমেইল / ইউজার আইডি / চ্যানেল লিংক",
    "account_quota" to "বরাদ্দকৃত স্টোরেজ কোটা",
    "account_status_active" to "সক্রিয় ও সিঙ্কড",
    "account_status_connecting" to "সিঙ্ক হচ্ছে...",
    "btn_add_connection" to "কানেকশন যুক্ত করুন",
    "btn_remove" to "মুছে ফেলুন",
    "btn_edit_nickname" to "নাম পরিবর্তন",
    "confirm_remove_account" to "আপনি কি এই ক্লাউড কানেকশন এবং এর লোকাল ক্যাশ মুছতে চান?",
    "cancel" to "বাতিল",
    "save" to "সংরক্ষণ",
    "delete" to "মুছুন",
    "no_accounts" to "এই ক্যাটাগরিতে কোনো অ্যাকাউন্ট নেই। '+' বাটনে ট্যাপ করে যোগ করুন।",

    // Gallery Screen
    "gallery_title" to "গুগল গ্যালারি ভিউ",
    "filter_all" to "সকল ফাইল",
    "filter_photos" to "ছবি",
    "filter_videos" to "ভিডিও",
    "filter_docs" to "ডকুমেন্টস",
    "filter_audio" to "অডিও",
    "filter_archives" to "আর্কাইভ (Zip/Rar)",
    "file_size" to "সাইজ",
    "file_date" to "তারিখ",
    "file_cloud" to "প্রোভাইডার",
    "file_download" to "ডাউনলোড",
    "file_share" to "শেয়ার",
    "file_delete" to "ফাইল মুছুন",
    "no_media_found" to "ক্যাশে কোনো মিডিয়া পাওয়া যায়নি। 'Refresh Sync' চাপুন।",

    // Search Screen
    "search_hint" to "লোকাল ক্যাশে ফাইল নাম ও তারিখ দিয়ে তাৎক্ষণিক খুঁজুন...",
    "search_instant_results" to "লোকাল ক্যাশ দ্রুত ফলাফল",
    "search_no_results" to "লোকাল ইন্ডেক্সে কোনো ফাইল পাওয়া যায়নি",
    "search_filter_provider" to "ক্লাউড প্রোভাইডার নির্বাচন",

    // Web Browser Screen
    "browser_title" to "ভায়া ব্রাউজার",
    "browser_subtitle" to "হালকা, ট্র্যাকার-মুক্ত এবং বিজ্ঞাপন-বিহীন ব্রাউজার",
    "browser_search_or_url" to "ওয়েব ঠিকানা লিখুন বা খুঁজুন...",
    "browser_desktop_mode" to "ডেস্কটপ মোড",
    "browser_mobile_mode" to "মোবাইল মোড",
    "browser_adblock_active" to "বিজ্ঞাপন ব্লকার সক্রিয়",
    "browser_cookies_blocked" to "ট্র্যাকিং কুকিজ ব্লক করা হয়েছে",
    "browser_vpn_inherited" to "সিস্টেম VPN কানেক্টিভিটি সহ",
    "browser_new_tab" to "নতুন ট্যাব",
    "browser_close_tab" to "ট্যাব বন্ধ করুন",
    "browser_tabs" to "ট্যাবসমূহ",
    "browser_clear_cookies" to "প্রাইভেসি ফ্লাশ",
    "browser_flush_success" to "সমস্ত হিস্ট্রি, কুকি এবং ট্র্যাকিং স্ক্রিপ্ট মুছে ফেলা হয়েছে!",

    // Settings Screen
    "settings_title" to "অ্যাপ সেটিংস",
    "settings_security_header" to "নিরাপত্তা ও অ্যাপ-লক",
    "settings_pin_change" to "সিকিউরিটি পিন রিসেট করুন",
    "settings_pin_subtitle" to "অ্যাপ শুরুর ৪-সংখ্যার পিন পরিবর্তন করুন",
    "settings_biometric_toggle" to "বায়োমেট্রিক ফিঙ্গারপ্রিন্ট আনলক",
    "settings_biometric_subtitle" to "অ্যাপ খোলার সময় ফিঙ্গারপ্রিন্ট দিয়ে আনলক করুন",
    "settings_cache_header" to "ক্যাশ ও ডাটাবেস সিনক্রোনাইজেশন",
    "settings_refresh_sync" to "রিফ্রেশ সিঙ্ক (Refresh Sync)",
    "settings_refresh_sync_sub" to "সকল যুক্ত ক্লাউড থেকে ফাইল লোকাল SQLite/Room এ ইন্ডেক্স করুন",
    "settings_privacy_header" to "ব্রাউজার প্রাইভেসি কন্ট্রোল",
    "settings_privacy_flush" to "ব্রাউজার প্রাইভেসি ফ্লাশ (Wipe)",
    "settings_privacy_flush_sub" to "WebView হিস্ট্রি, কুকিজ ও ক্যাশ ডাটা এক ক্লিকে মুছে ফেলুন",
    "settings_language_header" to "ইন্টারফেস ভাষা নির্বাচন (Language)",
    "settings_language_sub" to "বাংলা ও ইংরেজির মধ্যে তাৎক্ষণিক পরিবর্তন করুন",
    "settings_about_header" to "ক্লাউড হান্টার সম্পর্কে",
    "settings_about_version" to "ভার্সন ১.০.০ (রিলিজ বিল্ড)",

    // App Lock Screen
    "lock_title" to "সিকিউরিটি লক",
    "lock_enter_pin" to "ক্লাউড হান্টার খুলতে ৪-সংখ্যার পিন দিন",
    "lock_wrong_pin" to "ভুল পিন! অনুগ্রহ করে পুনরায় চেষ্টা করুন।",
    "lock_use_biometrics" to "বায়োমেট্রিক দিয়ে আনলক করুন",
    "lock_reset_pin_title" to "নতুন সিকিউরিটি পিন দিন",
    "lock_enter_new_pin" to "নতুন ৪-সংখ্যার পিন লিখুন",
    "lock_confirm_new_pin" to "নতুন পিনটি আবার লিখুন",
    "lock_pin_mismatch" to "উভয় পিন মেলেনি!",
    "lock_pin_updated" to "সিকিউরিটি পিন সফলভাবে আপডেট করা হয়েছে!",
    "lock_biometric_prompt_title" to "ক্লাউড হান্টার বায়োমেট্রিক যাচাইকরণ",
    "lock_biometric_prompt_subtitle" to "আনলক করতে আপনার ফিঙ্গারপ্রিন্ট স্ক্যান করুন"
  )

  fun get(key: String, lang: AppLanguage): String {
    return when (lang) {
      AppLanguage.BENGALI -> bn[key] ?: en[key] ?: key
      AppLanguage.ENGLISH -> en[key] ?: key
    }
  }
}
