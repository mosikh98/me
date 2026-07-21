package com.v2ray.ang.dto.entities

data class ServersCache(
    val guid: String,
    val profile: ProfileItem,
    val testDelayMillis: Long = 0L,
    val testDelayString: String = "",
    val trafficUploadBytes: Long = 0L,
    val trafficDownloadBytes: Long = 0L,
)
