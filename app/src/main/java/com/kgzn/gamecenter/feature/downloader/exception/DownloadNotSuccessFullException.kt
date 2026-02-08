package com.kgzn.gamecenter.feature.downloader.exception

import java.io.IOException

class UnSuccessfulResponseException(val code: Int, msg: String) : IOException(
    "$code | $msg"
)