package com.kgzn.gamecenter.db.playrecord

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kgzn.gamecenter.data.InfoParam

@Entity
data class PlayRecord(
    @PrimaryKey
    override val dataId: String = "",
    override val configId: Int = 0,
    override val contentType: Int = 0,
    override val dataType: String = "",
    val title: String,
    val imgUrl: String? = null,
    val lastPlayTime: Long = 0,
) : InfoParam
