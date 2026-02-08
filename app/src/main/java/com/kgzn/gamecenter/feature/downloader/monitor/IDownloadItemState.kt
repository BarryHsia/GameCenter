package com.kgzn.gamecenter.feature.downloader.monitor

import androidx.compose.runtime.Immutable
import com.kgzn.gamecenter.data.InfoParam
import java.io.File

@Immutable
sealed interface IDownloadItemState: InfoParam {
    val id: Long
    val folder: String
    val name: String
    val contentLength: Long
    val saveLocation: String
    val dateAdded: Long
    val startTime: Long
    val completeTime: Long
    val downloadLink: String
    val label: String
    val imgUrl: String

    fun getFullPath() = File(folder, name)
}