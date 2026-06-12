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
}
