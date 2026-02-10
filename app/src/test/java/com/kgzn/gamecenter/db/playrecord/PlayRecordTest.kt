package com.kgzn.gamecenter.db.playrecord

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayRecordTest {

    @Test
    fun `PlayRecord default values`() {
        val record = PlayRecord(title = "Test Game")
        assertEquals("", record.dataId)
        assertEquals(0, record.configId)
        assertEquals(0, record.contentType)
        assertEquals("", record.dataType)
        assertEquals("Test Game", record.title)
        assertEquals(null, record.imgUrl)
        assertEquals(0L, record.lastPlayTime)
    }

    @Test
    fun `PlayRecord implements InfoParam`() {
        val record = PlayRecord(
            dataId = "123",
            configId = 1,
            contentType = 2,
            dataType = "game",
            title = "Test",
        )
        assertEquals("123", record.dataId)
        assertEquals(1, record.configId)
        assertEquals(2, record.contentType)
        assertEquals("game", record.dataType)
    }

    @Test
    fun `PlayRecord with all fields`() {
        val time = System.currentTimeMillis()
        val record = PlayRecord(
            dataId = "abc",
            configId = 10,
            contentType = 3,
            dataType = "app",
            title = "My App",
            imgUrl = "https://example.com/img.png",
            lastPlayTime = time,
        )
        assertEquals("abc", record.dataId)
        assertEquals("https://example.com/img.png", record.imgUrl)
        assertEquals(time, record.lastPlayTime)
    }
}
