package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.data.Info

data class GetInfo(
    val code: Int,
    val dataList: List<Info>?,
    val info: Info?,
    val msg: String?,
)