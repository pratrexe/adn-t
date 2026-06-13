package com.example.adnt.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import com.example.adnt.data.SettingsManager

class AdSkipperService : AccessibilityService() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
    }

    companion object {
        private const val TAG = "AdSkipperService"
        
        private const val PKG_YOUTUBE = "com.google.android.youtube"
        private const val PKG_YT_MUSIC = "com.google.android.apps.youtube.music"
        
        private val SKIP_PATTERNS = listOf(
            "Skip Ad", "Skip", "Skip Video", "Saltar anuncio", "Skip advertisement",
            "Skip 1 of 2", "Skip all", "Skip Intro", "Skip in", "Bonus in", "Reward in"
        )
        
        private val SKIP_IDS = listOf(
            "com.google.android.youtube:id/skip_ad_button",
            "com.google.android.youtube:id/ad_skip_button",
            "com.google.android.youtube:id/modern_skip_ad_button",
            "com.google.android.apps.youtube.music:id/skip_ad_button"
        )
        
        private val AD_CONTAINERS = setOf(
            "com.google.android.gms.ads",
            "com.mopub.mobileads",
            "com.unity3d.ads",
            "com.applovin",
            "com.facebook.ads",
            "com.google.android.youtube",
            "com.google.android.apps.youtube.music"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val packageName = event.packageName?.toString() ?: ""
        val isHardcore = settingsManager.hardcoreMode

        // Universal Aggressive Skipping for Hardcore Mode
        if (isHardcore) {
            findAndClickAggressively(rootNode)
            return
        }
        
        // Precision Mode (Normal)
        if (packageName == PKG_YOUTUBE || packageName == PKG_YT_MUSIC) {
            handleVideoAds(rootNode)
            return
        }

        if (AD_CONTAINERS.any { packageName.contains(it) }) {
            findAndClickCloseButtons(rootNode)
        }
    }

    private fun handleVideoAds(rootNode: AccessibilityNodeInfo) {
        for (id in SKIP_IDS) {
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                if (tryClick(node)) return
            }
        }

        for (pattern in SKIP_PATTERNS) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(pattern)
            for (node in nodes) {
                if (tryClick(node)) return
            }
        }
    }

    private fun findAndClickCloseButtons(node: AccessibilityNodeInfo) {
        if (node.className == "android.widget.Button" || node.className == "android.widget.ImageView") {
            val desc = node.contentDescription?.toString()?.lowercase()
            val text = node.text?.toString()?.lowercase()
            
            if ((desc != null && (desc == "close" || desc == "dismiss" || desc == "close ad")) ||
                (text != null && (text == "close" || text == "dismiss"))) {
                tryClick(node)
            }
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndClickCloseButtons(child)
            }
        }
    }

    private fun findAndClickAggressively(node: AccessibilityNodeInfo) {
        // Broad search for ANY skip/close indicators in any app
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        
        val isSkipText = SKIP_PATTERNS.any { text.contains(it.lowercase()) || desc.contains(it.lowercase()) }
        val isCloseIcon = desc == "x" || desc == "close" || desc == "dismiss" || text == "x"

        if (node.isClickable && (isSkipText || isCloseIcon)) {
            // Safety: Ignore if it's the YouTube miniplayer control
            if (!desc.contains("minimized player")) {
                tryClick(node)
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndClickAggressively(child)
            }
        }
    }

    private fun tryClick(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable && node.isEnabled) {
            val label = node.text ?: node.contentDescription ?: node.viewIdResourceName ?: "ad button"
            Log.i(TAG, "⚡ HARDCORE SKIP: $label")
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service Interrupted")
    }
}
