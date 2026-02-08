package com.kgzn.gamecenter.data.remote

data class ApiResponse<T>(
    val code: Int,
    val data: T?,
    val msg: String? = null,
)