package com.kgzn.gamecenter.feature.downloader.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface IDownloadListDb {
    // modification/add implementations must be thread safe

    @Query("SELECT * FROM DownloadItem")
    suspend fun getAll(): List<DownloadItem>

    @Query("SELECT * FROM DownloadItem WHERE id = :id")
    suspend fun getById(id: Long): DownloadItem?

    @Insert
    suspend fun add(item: DownloadItem)

    @Update
    suspend fun update(item: DownloadItem)

    @Delete
    suspend fun remove(item: DownloadItem)

    @Query("DELETE FROM DownloadItem WHERE id = :itemId")
    suspend fun removeById(itemId: Long)

    @Query("SELECT MAX(id) FROM DownloadItem")
    suspend fun getLastId(): Long

    @Query("SELECT * FROM DownloadItem WHERE dataId = :dataId")
    fun getByDataId(dataId: String): Flow<DownloadItem?>

    @Query("SELECT * FROM DownloadItem WHERE packageName = :packageName AND versionCode = :versionCode")
    fun getByPackage(packageName: String, versionCode: Int): Flow<DownloadItem?>

//    suspend fun allAsFlow(): Flow<List<DownloadItem>>
}

