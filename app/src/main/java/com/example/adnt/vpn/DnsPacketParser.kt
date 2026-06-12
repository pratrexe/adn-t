package com.example.adnt.vpn

import java.nio.ByteBuffer

object DnsPacketParser {
    fun parseDomainName(data: ByteArray, offset: Int): String? {
        val domain = StringBuilder()
        var currentOffset = offset
        var first = true

        while (true) {
            val length = data[currentOffset].toInt() and 0xFF
            if (length == 0) break
            if (length > 63) return null // Likely compression or invalid

            if (!first) domain.append(".")
            first = false

            currentOffset++
            if (currentOffset + length > data.size) return null

            domain.append(String(data, currentOffset, length))
            currentOffset += length
        }
        return domain.toString()
    }

    /**
     * Extracts domain from a UDP packet if it's a DNS query on port 53.
     * IP header (20 bytes) + UDP header (8 bytes) = 28 bytes offset to DNS header.
     */
    fun getDomainFromPacket(packet: ByteArray, length: Int): String? {
        if (length < 28 + 12) return null // Min size: IP+UDP+DNS Header

        // Check if it's UDP (protocol 17 at index 9)
        if (packet[9].toInt() != 17) return null

        // Check destination port (53 at index 22-23)
        val destPort = ((packet[22].toInt() and 0xFF) shl 8) or (packet[23].toInt() and 0xFF)
        if (destPort != 53) return null

        // DNS header is 12 bytes. Question starts at index 28 + 12 = 40.
        return parseDomainName(packet, 40)
    }
}
