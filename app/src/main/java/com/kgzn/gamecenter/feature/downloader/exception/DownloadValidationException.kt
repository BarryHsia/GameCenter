package com.kgzn.gamecenter.feature.downloader.exception

abstract class DownloadValidationException(
    msg: String,
    cause: Throwable? = null,
) : Exception(msg, cause) {
    abstract fun isCritical(): Boolean
}
