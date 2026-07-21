package com.v2ray.ang.dto.entities

data class ServerAffiliationInfo(
    var testDelayMillis: Long = 0L,
    // Cumulative bytes proxied through this server (guid), accumulated live from the core's
    // stats API every time this server is the active connection. Persists across restarts.
    var trafficUploadBytes: Long = 0L,
    var trafficDownloadBytes: Long = 0L
) {
    fun getTestDelayString(): String {
        if (testDelayMillis == 0L) {
            return ""
        }
        return testDelayMillis.toString() + "ms"
    }
}