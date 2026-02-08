package com.kgzn.gamecenter.data

import kotlinx.coroutines.flow.Flow

interface AppApi {

    fun getAllContentConfigs(): Flow<List<ContentConfig>>

    fun getInfo(param: InfoParam): Flow<Info?>

    fun search2(key: String): Flow<List<Search2>> {
        TODO("Not yet implemented")
    }

    fun getDownloadUrl(param: InfoParam): Flow<String> {
        TODO("Not yet implemented")
    }
}