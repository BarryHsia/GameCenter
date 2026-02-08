package com.kgzn.gamecenter.data.remote

data class ApiException(
    val code: Int,
    val msg: String?,
) : Exception(msg)
