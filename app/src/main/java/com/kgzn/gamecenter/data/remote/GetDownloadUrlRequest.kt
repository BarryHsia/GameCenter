package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.data.remote.request.Token

data class GetDownloadUrlRequest(
    val dataId: String,
    val contentType: Int,
    val dataType: String,
    override var token: String = ""
) : Token {

    init {
        token = token()
    }
}
