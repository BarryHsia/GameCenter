package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.data.ContentConfig

data class GetContentConfig(
    val code: Int,
    val data: List<ContentConfig>
)