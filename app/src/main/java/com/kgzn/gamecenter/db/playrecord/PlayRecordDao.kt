package com.kgzn.gamecenter.db.playrecord

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PlayRecord)

    @Transaction
    suspend fun insertRecordWithLimit(record: PlayRecord, limitCount: Int) {
        insertRecord(record)
        deleteExcessRecords(limitCount)
    }

    @Query("DELETE FROM PlayRecord WHERE dataId NOT IN (SELECT dataId FROM PlayRecord ORDER BY lastPlayTime DESC LIMIT :count)")
    suspend fun deleteExcessRecords(count: Int)

    @Query("SELECT * FROM PlayRecord ORDER BY lastPlayTime DESC")
    fun getAllByLastPlayTimeDesc(): Flow<List<PlayRecord>>
}
