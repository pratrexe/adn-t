package com.example.adnt.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class AdSkipperService : AccessibilityService() {

    companion object {
        private const val TAG = "AdSkipperService"
        private val SKIP_PATTERNS = listOf(
            "Skip Ad", "Skip", "Skip Video", "Skip Intro",
            "Close", "Close Ad", "Dismiss", "No thanks",
            "Saltar anuncio", "Skip advertisement"
        )
        private val SKIP_IDS = listOf(
            "com.google.android.youtube:id/skip_ad_button",
            "com.google.android.youtube:id/ad_skip_button",
            "com.google.android.youtube:id/modern_skip_ad_button"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        
        // Try searching by common IDs first (efficient)
        for (id in SKIP_IDS) {
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
            for (node in nodes) {
                if (tryClick(node)) return
            }
        }

        // Search by text patterns
        for (pattern in SKIP_PATTERNS) {
            val nodes = rootNode.findAccessibilityNodeInfosByText(pattern)
            for (node in nodes) {
                if (tryClick(node)) return
            }
        }

        // Look for tiny "X" close buttons (often found in interstitial ads)
        findAndClickXButton(rootNode)
    }

    private fun tryClick(node: AccessibilityNodeInfo): Boolean {
        if (node.isClickable && node.isEnabled) {
            Log.i(TAG, "Auto-clicking ad button: ${node.text ?: node.viewIdResourceName}")
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }
        return false
    }

    private fun findAndClickXButton(node: AccessibilityNodeInfo) {
        // Broad search for small buttons that might be "X" close buttons
        if (node.className == "android.widget.Button" || node.className == "android.widget.ImageView") {
            val desc = node.contentDescription?.toString()?.lowercase()
            if (desc != null && (desc == "close" || desc == "skip" || desc == "dismiss" || desc == "x")) {
                if (tryClick(node)) return
            }
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndClickXButton(child)
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service Interrupted")
    }
}
