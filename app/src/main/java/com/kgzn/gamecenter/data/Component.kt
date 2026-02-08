package com.kgzn.gamecenter.data

data class Component(
    val name: String,
    val showType: String?,
    val id: Int,
    val configId: String?,
    val labelId: String?,
    val tabId: String?,
    val resourceList: List<Resource>,
    val num: String?,
    val setType: String?,
    val languageList: List<String>?,
)