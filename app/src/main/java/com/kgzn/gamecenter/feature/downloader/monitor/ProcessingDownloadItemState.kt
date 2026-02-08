package com.kgzn.gamecenter.feature.downloader.monitor

import androidx.compose.runtime.Immutable
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadJob
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadJobStatus
import com.kgzn.gamecenter.feature.downloader.utils.calcPercent

@Immutable
data class ProcessingDownloadItemState(
    override val id: Long,
    override val folder: String,
    override val name: String,
    override val downloadLink: String,
    override val contentLength: Long,
    override val saveLocation: String,
    override val dateAdded: Long,
    override val startTime: Long,
    override val completeTime: Long,
    val status: DownloadJobStatus,
    val speed: Long,
    val parts: List<UiPart>,
    val supportResume: Boolean?,
    override val label: String,
    override val imgUrl: String,
    override val configId: Int,
    override val dataId: String,
    override val contentType: Int,
    override val dataType: String,
) : IDownloadItemState {
    val progress = parts.sumOf {
        it.howMuchProceed
    }
    val hasProgress get() = progress > 0
    val gotAnyProgress = progress > 0L
    val percent: Int? = if (contentLength == DownloadItem.LENGTH_UNKNOWN) {
        null
    } else {
        calcPercent(progress, contentLength)
    }

    //remaining time in seconds
    val remainingTime: Long? = run {
        when {
            contentLength <= 0 || speed <= 0 -> null
            else -> (contentLength - progress) / speed
        }
    }

    companion object {
        const val SPEED_PER_UNIT = "s"
        fun fromDownloadJob(
            downloadJob: DownloadJob,
            speed: Long,
        ): ProcessingDownloadItemState {
            val downloadItem = downloadJob.downloadItem
            val downloadJobStatus = downloadJob.status.value
            val parts = downloadJob.getParts()
            return ProcessingDownloadItemState(
                id = downloadItem.id,
                folder = downloadItem.folder,
                name = downloadItem.name,
                contentLength = downloadItem.contentLength ?: -1,
                dateAdded = downloadItem.dateAdded,
                startTime = downloadItem.startTime ?: -1,
                completeTime = downloadItem.completeTime ?: -1,
                status = downloadJobStatus,
                saveLocation = downloadItem.name,
                parts = parts.map {
                    UiPart.Companion.fromPart(it)
                },
                speed = speed,
                supportResume = downloadJob.supportsConcurrent,
                downloadLink = downloadItem.link,
                label = downloadItem.label,
                imgUrl = downloadItem.imgUrl,
                configId = downloadItem.configId,
                dataId = downloadItem.dataId,
                contentType = downloadItem.contentType,
                dataType = downloadItem.dataType,
            )
        }

        fun onlyDownloadItem(
            downloadItem: DownloadItem,
        ): ProcessingDownloadItemState {
            val downloadJobStatus = DownloadJobStatus.IDLE
            return ProcessingDownloadItemState(
                id = downloadItem.id,
                folder = downloadItem.folder,
                name = downloadItem.name,
                contentLength = downloadItem.contentLength ?: -1,
                dateAdded = downloadItem.dateAdded,
                startTime = downloadItem.startTime ?: -1,
                completeTime = downloadItem.completeTime ?: -1,
                status = downloadJobStatus,
                saveLocation = downloadItem.name,
                parts = emptyList(),
                speed = 0,
                downloadLink = downloadItem.link,
                supportResume = null,
                label = downloadItem.label,
                imgUrl = downloadItem.imgUrl,
                configId = downloadItem.configId,
                dataId = downloadItem.dataId,
                contentType = downloadItem.contentType,
                dataType = downloadItem.dataType,
            )
        }
    }
}