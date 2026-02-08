package com.kgzn.gamecenter.data

data class Resource(
    val id: Int?,
    val type: String?,
    val configId: Int,
    val imgUrl: String,
    val languageList: List<String>?,
    val descLanguageList: List<String>?,
    val tabId: String?,
    val skipPar: String?,
    val title: String,
    val dataId: String,
    val contentType: Int,
    val img: String?,
    val thirdId: String?,
    val contentTagId: String?,
    val dataType: String,
    val componentId: String?,
    val remark: String,
    val infoImgVUrl: String? = null,
    val infoImgHUrl: String? = null,
)