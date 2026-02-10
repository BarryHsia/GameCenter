package com.kgzn.gamecenter.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.kgzn.gamecenter.db.playrecord.PlayRecord
import com.kgzn.gamecenter.db.playrecord.PlayRecordDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayRecordDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PlayRecordDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.playRecordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndQueryRecord() = runTest {
        val record = PlayRecord(
            dataId = "1",
            title = "Test Game",
            lastPlayTime = 1000L,
        )
        dao.insertRecord(record)

        dao.getAllByLastPlayTimeDesc().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Test Game", list[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertReplaceOnConflict() = runTest {
        val record1 = PlayRecord(dataId = "1", title = "Game V1", lastPlayTime = 1000L)
        val record2 = PlayRecord(dataId = "1", title = "Game V2", lastPlayTime = 2000L)

        dao.insertRecord(record1)
        dao.insertRecord(record2)

        dao.getAllByLastPlayTimeDesc().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Game V2", list[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun recordsOrderedByLastPlayTimeDesc() = runTest {
        dao.insertRecord(PlayRecord(dataId = "1", title = "Old", lastPlayTime = 100L))
        dao.insertRecord(PlayRecord(dataId = "2", title = "New", lastPlayTime = 200L))
        dao.insertRecord(PlayRecord(dataId = "3", title = "Mid", lastPlayTime = 150L))

        dao.getAllByLastPlayTimeDesc().test {
            val list = awaitItem()
            assertEquals("New", list[0].title)
            assertEquals("Mid", list[1].title)
            assertEquals("Old", list[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertRecordWithLimitDeletesExcess() = runTest {
        dao.insertRecordWithLimit(PlayRecord(dataId = "1", title = "A", lastPlayTime = 100L), 2)
        dao.insertRecordWithLimit(PlayRecord(dataId = "2", title = "B", lastPlayTime = 200L), 2)
        dao.insertRecordWithLimit(PlayRecord(dataId = "3", title = "C", lastPlayTime = 300L), 2)

        dao.getAllByLastPlayTimeDesc().test {
            val list = awaitItem()
            assertEquals(2, list.size)
            assertEquals("C", list[0].title)
            assertEquals("B", list[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun emptyDatabaseReturnsEmptyList() = runTest {
        dao.getAllByLastPlayTimeDesc().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
