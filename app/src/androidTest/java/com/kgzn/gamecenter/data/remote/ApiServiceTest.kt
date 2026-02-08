package com.kgzn.gamecenter.data.remote

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder
import com.kgzn.gamecenter.data.remote.AppApiImpl.Companion.BASE_URL
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class ApiServiceTest {

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)
    }

    @Test
    fun getInfo() {
        runBlocking {
            val response = apiService.getInfo(GetInfoRequest.test())
            assert(response.isSuccessful)
        }
    }

    @Test
    fun search2() {
        runBlocking {
            val response = apiService.search2(Search2Parameter.test())
            assert(response.isSuccessful)
        }
    }
}
