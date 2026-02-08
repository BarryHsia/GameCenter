package com.kgzn.gamecenter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kgzn.gamecenter.db.playrecord.PlayRecord
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import com.kgzn.gamecenter.feature.downloader.db.IDownloadListDb
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem


@Database(entities = [DownloadItem::class, PlayRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun downloadListDb(): IDownloadListDb
    abstract fun playRecordDao(): PlayRecordDao
}
