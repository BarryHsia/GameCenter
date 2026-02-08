package com.kgzn.gamecenter.data

data class Info(
    val tagList: List<String>?,
    val control: List<String>?,
    val version: String?,
    val packetType: String,
    val category: String?,
    val versionCode: Int?,
    val size: String?,
    val packageName: String?,
    val imgUrl: String,
    override val contentType: Int,
    val remark: String,
    val tabId: String?,
    val languageList: List<String>?,
    override val dataId: String,
    override val dataType: String,
    val img: String?,
    val skipPar: String?,
    val title: String,
    val contentTagId: String?,
    val descLanguageList: List<String>?,
    val componentId: String?,
    val thirdId: String?,
    override val configId: Int,
    val id: String?,
    val type: String?,
    val dataList: List<Info>,
    val infoImgVUrl: String? = null,
    val infoImgHUrl: String? = null,
    val isGoogleplay: Int? = null,
) : InfoParam {

    val requireGoogleplay: Boolean
        get() = isGoogleplay == 1
}
