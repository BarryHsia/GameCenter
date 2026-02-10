package com.kgzn.gamecenter.data.remote

import com.google.gson.GsonBuilder
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Tests API response parsing using MockWebServer.
 * Avoids constructing request classes that depend on Android context (AppUtils).
 */
class MockWebServerApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `search2 parses successful response`() = runTest {
        val json = """{"code":200,"data":[{"name":"Game1"}],"msg":"ok"}"""
        mockWebServer.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val params = Search2Parameter(
            key = "test",
            isPrecise = false,
            difference = 1,
            language = "en",
            channelCode = "default",
            appType = 2,
        )
        val response = apiService.search2(params)
        assertEquals(200, response.code)
    }

    @Test
    fun `server error returns HTTP exception`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))

        val params = Search2Parameter(
            key = "test",
            isPrecise = false,
            difference = 1,
            language = "en",
            channelCode = "default",
            appType = 2,
        )
        try {
            apiService.search2(params)
            // Should not reach here — server returned 500
            assertTrue("Expected HttpException but call succeeded", false)
        } catch (e: retrofit2.HttpException) {
            assertEquals(500, e.code())
        }
    }

    @Test
    fun `ApiResponse deserialization with Gson`() {
        val gson = GsonBuilder().create()
        val json = """{"code":200,"data":["item1","item2"],"msg":"success"}"""
        val type = com.google.gson.reflect.TypeToken
            .getParameterized(ApiResponse::class.java, List::class.java).type
        val response: ApiResponse<List<String>> = gson.fromJson(json, type)

        assertEquals(200, response.code)
        assertNotNull(response.data)
        assertEquals(2, response.data?.size)
        assertEquals("success", response.msg)
    }

    @Test
    fun `ApiResponse deserialization with null data`() {
        val gson = GsonBuilder().create()
        val json = """{"code":404,"data":null,"msg":"not found"}"""
        val type = com.google.gson.reflect.TypeToken
            .getParameterized(ApiResponse::class.java, String::class.java).type
        val response: ApiResponse<String> = gson.fromJson(json, type)

        assertEquals(404, response.code)
        assertEquals(null, response.data)
        assertEquals("not found", response.msg)
    }
}
