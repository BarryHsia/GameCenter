package com.kgzn.gamecenter.data.repository

import app.cash.turbine.test
import com.kgzn.gamecenter.data.AppApi
import com.kgzn.gamecenter.data.ContentConfig
import com.kgzn.gamecenter.data.Info
import com.kgzn.gamecenter.data.InfoParam
import com.kgzn.gamecenter.data.Search2
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameRepositoryTest {

    private lateinit var appApi: AppApi
    private lateinit var repository: GameRepository

    @Before
    fun setup() {
        appApi = mockk()
        repository = GameRepository(appApi)
    }

    @Test
    fun `getAllContentConfigs delegates to appApi`() = runTest {
        val configs = listOf(mockk<ContentConfig>())
        every { appApi.getAllContentConfigs() } returns flowOf(configs)

        repository.getAllContentConfigs().test {
            assertEquals(configs, awaitItem())
            awaitComplete()
        }
        verify { appApi.getAllContentConfigs() }
    }

    @Test
    fun `getInfo delegates to appApi with correct param`() = runTest {
        val param = mockk<InfoParam>()
        val info = mockk<Info>()
        every { appApi.getInfo(param) } returns flowOf(info)

        repository.getInfo(param).test {
            assertEquals(info, awaitItem())
            awaitComplete()
        }
        verify { appApi.getInfo(param) }
    }

    @Test
    fun `search delegates to appApi with correct key`() = runTest {
        val results = listOf(mockk<Search2>())
        every { appApi.search2("test") } returns flowOf(results)

        repository.search("test").test {
            assertEquals(results, awaitItem())
            awaitComplete()
        }
        verify { appApi.search2("test") }
    }

    @Test
    fun `getDownloadUrl delegates to appApi`() = runTest {
        val param = mockk<InfoParam>()
        every { appApi.getDownloadUrl(param) } returns flowOf("https://example.com/download")

        repository.getDownloadUrl(param).test {
            assertEquals("https://example.com/download", awaitItem())
            awaitComplete()
        }
    }
}
