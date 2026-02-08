package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.data.remote.request.Token
import com.kgzn.gamecenter.uitls.AppUtils

data class GetContentConfigRequest(
    val customer: String = AppUtils.getCustomer(),
    val model: String = AppUtils.getModel(),
    val mac: String = AppUtils.getMac(),
    val sda: String = AppUtils.getSda(),
    val appid: String = AppUtils.getAppId(),
    val region: String = AppUtils.getRegion(),
    val version: String = "2.0",
    val language: String = AppUtils.getLanguage(),
    val appType: Int = 2,
    val channelCode: String = AppUtils.getChannelCode(),
    override var token: String = ""
) : Token {

    init {
        token = token()
    }
}