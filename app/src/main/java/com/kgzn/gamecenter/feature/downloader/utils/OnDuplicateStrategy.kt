package com.kgzn.gamecenter.feature.downloader.utils

enum class OnDuplicateStrategy {
    AddNumbered,
    OverrideDownload,
    Abort, ;

    companion object {
        fun default() = AddNumbered
    }
}

fun OnDuplicateStrategy?.orDefault() = this ?: OnDuplicateStrategy.default()