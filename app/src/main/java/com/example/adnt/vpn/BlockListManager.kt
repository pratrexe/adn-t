package com.example.adnt.vpn

object BlockListManager {
    private val blackList = setOf(
        "googleads.g.doubleclick.net",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "ads.youtube.com",
        "app-measurement.com",
        "analytics.google.com",
        "click.redditmail.com",
        "pixel.facebook.com",
        "graph.facebook.com",
        "ads.facebook.com",
        "advertising.amazon.com",
        "track.amazon.com"
    )

    fun shouldBlock(domain: String?): Boolean {
        if (domain == null) return false
        return blackList.any { domain.contains(it, ignoreCase = true) }
    }
}
