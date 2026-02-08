package com.kgzn.gamecenter.feature.installer

data class UninstallState(
    val packageName: String,
    val status: Int? = null,
    val msg: String? = null,
) {
    fun isFinished() = status != null
}
