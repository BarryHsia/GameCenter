package com.kgzn.gamecenter.feature.downloader

import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItemContext
import com.kgzn.gamecenter.feature.downloader.downloaditem.EmptyContext
import kotlinx.coroutines.flow.SharedFlow

sealed interface DownloadManagerEvents {
    val downloadItem: DownloadItem
    val context: DownloadItemContext

    data class OnJobAdded(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents

    data class OnJobChanged(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents

    data class OnJobStarting(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents

    data class OnJobStarted(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents

    data class OnJobCompleted(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents

    data class OnJobCanceled(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext,
        val e: Throwable
    ) : DownloadManagerEvents

    data class OnJobRemoved(
        override val downloadItem: DownloadItem,
        override val context: DownloadItemContext
    ) : DownloadManagerEvents
}

interface DownloadManagerMinimalControl {
    suspend fun startJob(id: Long, context: DownloadItemContext = EmptyContext)
    suspend fun stopJob(id: Long, context: DownloadItemContext = EmptyContext)
    fun canActivateJob(id: Long): Boolean
    val listOfJobsEvents: SharedFlow<DownloadManagerEvents>
}
