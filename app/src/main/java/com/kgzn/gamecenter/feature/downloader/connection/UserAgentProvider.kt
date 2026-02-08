package com.kgzn.gamecenter.feature.downloader.connection

interface UserAgentProvider {
    fun getUserAgent(): String?
}
