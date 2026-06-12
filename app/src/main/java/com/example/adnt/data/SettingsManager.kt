package com.example.adnt.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("adnt_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_BLOCK_TRACKERS = "block_trackers"
        const val KEY_DNS_PROVIDER = "dns_provider"
        const val KEY_START_ON_BOOT = "start_on_boot"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_BLOCKED_COUNT = "blocked_count"
        const val KEY_TRACKERS_COUNT = "trackers_count"
        const val KEY_DATA_SAVED = "data_saved"
        const val KEY_RECENT_BLOCKED = "recent_blocked"
        const val KEY_EXCLUDED_APPS = "excluded_apps"
        
        const val DNS_GOOGLE = "8.8.8.8"
        const val DNS_CLOUDFLARE = "1.1.1.1"
        const val DNS_ADGUARD = "94.140.14.14"
    }

    var blockTrackers: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_TRACKERS, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_TRACKERS, value).apply()

    var dnsProvider: String
        get() = prefs.getString(KEY_DNS_PROVIDER, DNS_GOOGLE) ?: DNS_GOOGLE
        set(value) = prefs.edit().putString(KEY_DNS_PROVIDER, value).apply()

    var startOnBoot: Boolean
        get() = prefs.getBoolean(KEY_START_ON_BOOT, false)
        set(value) = prefs.edit().putBoolean(KEY_START_ON_BOOT, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "Auto") ?: "Auto"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var blockedCount: Int
        get() = prefs.getInt(KEY_BLOCKED_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_BLOCKED_COUNT, value).apply()

    var trackersCount: Int
        get() = prefs.getInt(KEY_TRACKERS_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_TRACKERS_COUNT, value).apply()

    var dataSaved: Float
        get() = prefs.getFloat(KEY_DATA_SAVED, 0f)
        set(value) = prefs.edit().putFloat(KEY_DATA_SAVED, value).apply()

    var recentBlocked: String
        get() = prefs.getString(KEY_RECENT_BLOCKED, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECENT_BLOCKED, value).apply()

    var excludedApps: Set<String>
        get() = prefs.getString(KEY_EXCLUDED_APPS, "")?.split(",")?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
        set(value) = prefs.edit().putString(KEY_EXCLUDED_APPS, value.joinToString(",")).apply()

    fun addExcludedApp(packageName: String) {
        excludedApps = excludedApps + packageName
    }

    fun removeExcludedApp(packageName: String) {
        excludedApps = excludedApps - packageName
    }

    fun addBlockedDomain(domain: String, isTracker: Boolean) {
        blockedCount++
        if (isTracker) trackersCount++
        dataSaved += 0.1f // Estimate 100KB saved per blocked ad
        
        val currentList = recentBlocked.split(",").filter { it.isNotEmpty() }.toMutableList()
        currentList.add(0, domain)
        recentBlocked = currentList.take(10).joinToString(",")
    }
}
