package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.uitls.AppUtils

class Search2Parameter(
    key: String,
    isPrecise: Boolean = false,
    difference: Int = 1,
    language: String = AppUtils.getLanguage(),
    channelCode: String = AppUtils.getChannelCode(),
    appType: Int = 2,
) : HashMap<String, Any>() {

    init {
        put("key", key)
        put("isPrecise", isPrecise.toString())
        put("difference", difference.toString())
        put("language", language)
        put("channel_code", channelCode)
        put("appType", appType.toString())
    }

    var key: String by this
    var isPrecise: Boolean by this
    var difference: Int by this
    var language: String by this
    var channelCode: String by this
    var appType: Int by this
}
