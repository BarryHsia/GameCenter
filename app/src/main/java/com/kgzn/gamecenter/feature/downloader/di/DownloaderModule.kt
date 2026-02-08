package com.kgzn.gamecenter.feature.downloader.di

import android.content.Context
import com.kgzn.gamecenter.feature.downloader.DownloadManager
import com.kgzn.gamecenter.feature.downloader.DownloadSettings
import com.kgzn.gamecenter.feature.downloader.connection.DownloaderClient
import com.kgzn.gamecenter.feature.downloader.connection.OkHttpDownloaderClient
import com.kgzn.gamecenter.feature.downloader.connection.UserAgentProvider
import com.kgzn.gamecenter.feature.downloader.db.IDownloadListDb
import com.kgzn.gamecenter.feature.downloader.db.IDownloadPartListDb
import com.kgzn.gamecenter.feature.downloader.db.PartListFileStorage
import com.kgzn.gamecenter.feature.downloader.db.TransactionalFileSaver
import com.kgzn.gamecenter.feature.downloader.monitor.DownloadMonitor
import com.kgzn.gamecenter.feature.downloader.monitor.IDownloadMonitor
import com.kgzn.gamecenter.feature.downloader.utils.EmptyFileCreator
import com.kgzn.gamecenter.feature.downloader.utils.IDiskStat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloaderModule {

    @Provides
    @Singleton
    fun provideDownloadSettings(): DownloadSettings {
        return DownloadSettings()
    }

    @Provides
    @Singleton
    fun provideDownloaderClient(): DownloaderClient {
        return OkHttpDownloaderClient(
            okHttpClient = OkHttpClient
                .Builder()
                .dispatcher(Dispatcher().apply {
                    //bypass limit on concurrent connections!
                    maxRequests = Int.MAX_VALUE
                    maxRequestsPerHost = Int.MAX_VALUE
                })
                .build(),
            defaultUserAgentProvider = object : UserAgentProvider {
                override fun getUserAgent(): String? = null
            },
        )
    }

    @Provides
    @Singleton
    fun provideIDownloadPartListDb(@ApplicationContext context: Context): IDownloadPartListDb {
        return PartListFileStorage(
            context.cacheDir.resolve("parts"),
            TransactionalFileSaver(Json {
                encodeDefaults = true
                prettyPrint = true
                ignoreUnknownKeys = true
            }),
        )
    }

    @Provides
    @Singleton
    fun provideIDiskStat(): IDiskStat {
        return object : IDiskStat {
            override fun getRemainingSpace(path: File): Long = path.freeSpace - 50L * 1024 * 1024 // 50MB
        }
    }

    @Provides
    @Singleton
    fun provideEmptyFileCreator(
        diskStat: IDiskStat,
        downloadSettings: DownloadSettings,
    ): EmptyFileCreator {
        return EmptyFileCreator(diskStat) { downloadSettings.useSparseFileAllocation }
    }

    @Provides
    @Singleton
    fun provideDownloadManager(
        downloadListDb: IDownloadListDb,
        downloadPartListDb: IDownloadPartListDb,
        downloadSettings: DownloadSettings,
        diskStat: IDiskStat,
        emptyFileCreator: EmptyFileCreator,
        downloaderClient: DownloaderClient,
    ): DownloadManager {
        return DownloadManager(
            downloadListDb,
            downloadPartListDb,
            downloadSettings,
            diskStat,
            emptyFileCreator,
            downloaderClient
        )
    }

    @Provides
    @Singleton
    fun provideDownloadMonitor(downloadManager: DownloadManager): IDownloadMonitor {
        return DownloadMonitor(downloadManager)
    }
}