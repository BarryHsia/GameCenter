package com.kgzn.gamecenter.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ApiResponseTest {

    @Test
    fun `ApiResponse with data`() {
        val response = ApiResponse(code = 200, data = "hello", msg = "ok")
        assertEquals(200, response.code)
        assertEquals("hello", response.data)
        assertEquals("ok", response.msg)
    }

    @Test
    fun `ApiResponse with null data`() {
        val response = ApiResponse<String>(code = 404, data = null, msg = "not found")
        assertEquals(404, response.code)
        assertNull(response.data)
    }

    @Test
    fun `ApiResponse default msg is null`() {
        val response = ApiResponse(code = 200, data = 42)
        assertNull(response.msg)
    }
}
