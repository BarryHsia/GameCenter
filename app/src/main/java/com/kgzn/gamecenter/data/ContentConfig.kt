package com.kgzn.gamecenter.data

data class ContentConfig(
    val name: String?,
    val icon: String?,
    val selIconUrl: String?,
    val categoryName: String?,
    val tabId: Int,
    val languageList: List<String>?,
    val selIcon: String,
    val isHome: Int?,
    val componentList: List<Component>,
    val categoryId: String?,
    val updateBy: String?,
    val updateTime: String?,
    val createBy: String?,
    val createTime: String?,
)