package com.kgzn.gamecenter.feature.downloader.downloaditem

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kgzn.gamecenter.data.InfoParam
import kotlinx.serialization.Serializable

@Serializable
@Entity
@TypeConverters(DownloadItemTypeConverters::class)
data class DownloadItem(
    override var link: String,
    override var headers: Map<String, String>? = null,
    override var username: String? = null,
    override var password: String? = null,
    override var downloadPage: String? = null,
    override var userAgent: String? = null,

    @PrimaryKey
    var id: Long,
    var folder: String,
    var name: String,

    var contentLength: Long = LENGTH_UNKNOWN,
    var serverETag: String? = null,

    var dateAdded: Long = 0,
    var startTime: Long? = null,
    var completeTime: Long? = null,

    var status: DownloadStatus = DownloadStatus.Added,
    var preferredConnectionCount: Int? = null,
    var speedLimit: Long = 0,//0 is unlimited

    var fileChecksum: String? = null,

    val label: String,
    val imgUrl: String,
    val versionCode: Int,
    val packageName: String,

    override val configId: Int,
    override val dataId: String,
    override val contentType: Int,
    override val dataType: String,
) : IDownloadCredentials, InfoParam {
    companion object {
        const val LENGTH_UNKNOWN = -1L
    }

}

fun DownloadItem.applyFrom(other: DownloadItem) {
    link = other.link
    headers = other.headers
    username = other.username
    password = other.password
    downloadPage = other.downloadPage
    userAgent = other.userAgent

    id = other.id
    folder = other.folder
    name = other.name

    contentLength = other.contentLength
    serverETag = other.serverETag

    dateAdded = other.dateAdded
    startTime = other.startTime
    completeTime = other.completeTime
    status = other.status
    preferredConnectionCount = other.preferredConnectionCount
    speedLimit = other.speedLimit

    fileChecksum = other.fileChecksum
}

fun DownloadItem.withCredentials(credentials: IDownloadCredentials) = apply {
    link = credentials.link
    headers = credentials.headers
    username = credentials.username
    password = credentials.password
    downloadPage = credentials.downloadPage
    userAgent = credentials.userAgent
}

enum class DownloadStatus {
    Error,
    Added,
    Paused,
    Downloading,
    Completed,
}

class DownloadItemTypeConverters {
    @TypeConverter
    fun fromMap(map: Map<String, String>): String {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun toMap(jsonString: String): Map<String, String> {
        return Gson().fromJson(jsonString, object : TypeToken<Map<String, String>>() {}.type)
    }

    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus): Int {
        return when (status) {
            DownloadStatus.Added -> 0
            DownloadStatus.Paused -> 1
            DownloadStatus.Downloading -> 2
            DownloadStatus.Completed -> 3
            DownloadStatus.Error -> 4
        }
    }

    @TypeConverter
    fun toDownloadStatus(status: Int): DownloadStatus {
        return when (status) {
            0 -> DownloadStatus.Added
            1 -> DownloadStatus.Paused
            2 -> DownloadStatus.Downloading
            3 -> DownloadStatus.Completed
            4 -> DownloadStatus.Error
            else -> DownloadStatus.Error
        }
    }
}