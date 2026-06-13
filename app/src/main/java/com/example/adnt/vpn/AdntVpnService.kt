package com.example.adnt.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.adnt.data.SettingsManager
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class AdntVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private val isRunning = AtomicBoolean(false)
    private var vpnThread: Thread? = null

    companion object {
        const val ACTION_START = "com.example.adnt.START"
        const val ACTION_STOP = "com.example.adnt.STOP"
        private const val TAG = "AdntVpnService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startVpn()
            ACTION_STOP -> stopVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        if (isRunning.getAndSet(true)) return

        createNotificationChannel()
        startForeground(1, createNotification())

        vpnThread = Thread({
            try {
                setupVpn()
                runPacketLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Error in VPN thread", e)
            } finally {
                stopVpn()
            }
        }, "AdntVpnThread")
        vpnThread?.start()
    }

    private fun setupVpn() {
        val settings = SettingsManager(this)
        val isHardcore = settings.hardcoreMode
        
        val builder = Builder()
        
        if (isHardcore) {
            // Triple-layer aggressive DNS
            builder.addDnsServer("94.140.14.14") // AdGuard
            builder.addDnsServer("45.90.28.0")   // NextDNS
            builder.addDnsServer("76.76.2.2")    // Control D
            Log.i(TAG, "VPN established in HARDCORE MODE")
        } else {
            val dns = if (settings.dnsProvider == SettingsManager.DNS_GOOGLE) {
                SettingsManager.DNS_ADGUARD 
            } else {
                settings.dnsProvider
            }
            builder.addDnsServer(dns)
            Log.i(TAG, "VPN established in Normal Mode (DNS: $dns)")
        }
        
        // Add split tunneling exclusions
        val excluded = settings.excludedApps
        for (app in excluded) {
            try {
                builder.addDisallowedApplication(app)
            } catch (e: Exception) {
                Log.w(TAG, "Could not exclude app: $app", e)
            }
        }

        vpnInterface = builder
            .addAddress("10.0.0.2", 32)
            .addRoute("10.0.0.0", 8) // Only route the VPN's own network to capture DNS
            .setSession("Adnt AdBlocker")
            .establish()
    }

    private fun runPacketLoop() {
        val settings = SettingsManager(this)
        val inputStream = FileInputStream(vpnInterface?.fileDescriptor)
        val outputStream = FileOutputStream(vpnInterface?.fileDescriptor)
        val packet = ByteBuffer.allocate(32768)

        while (isRunning.get()) {
            packet.clear()
            val length = inputStream.read(packet.array())
            if (length > 0) {
                val domain = DnsPacketParser.getDomainFromPacket(packet.array(), length)
                if (domain != null) {
                    if (BlockListManager.shouldBlock(domain)) {
                        Log.i(TAG, "Blocking domain: $domain")
                        settings.addBlockedDomain(domain, domain.contains("track") || domain.contains("analytics"))
                        continue 
                    } else {
                        Log.d(TAG, "Allowing domain: $domain")
                    }
                }
                // Forward the packet back to the TUN interface
                outputStream.write(packet.array(), 0, length)
            }
        }
    }

    private fun stopVpn() {
        isRunning.set(false)
        vpnInterface?.close()
        vpnInterface = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "vpn_channel",
            "VPN Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, "vpn_channel")
            .setContentTitle("Adn't Active")
            .setContentText("Protecting your device from ads")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .build()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
