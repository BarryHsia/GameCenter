package com.kgzn.gamecenter.data.remote

import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Search2
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ApiService {

    @POST("/contentconfigapi/getContentConfig")
    suspend fun getContentConfig(@Body request: GetContentConfigRequest): ApiResponse<List<ContentConfig>>

    @POST("/contentconfigapi/getInfo")
    suspend fun getInfo(@Body request: GetInfoRequest): GetInfo

    @GET("/appapi/search2")
    suspend fun search2(@QueryMap parameter: Search2Parameter): ApiResponse<List<Search2>>

    @POST("/contentconfigapi/getDownloadUrl")
    suspend fun getDownloadUrl(@Body request: GetDownloadUrlRequest): ApiResponse<String>

}