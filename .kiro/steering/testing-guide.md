---
inclusion: fileMatch
fileMatchPattern: '**/test/**'
---

# Android Testing Standards

测试规范（编辑测试文件时自动加载）

## 测试策略
- Unit Tests: 80% (快速、隔离)
- Integration Tests: 15% (组件交互)
- E2E Tests: 5% (完整流程)

## ViewModel 测试

```kotlin
@ExperimentalCoroutinesApi
class UserViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: UserViewModel
    private lateinit var repository: UserRepository
    
    @Before
    fun setup() {
        repository = mockk()
        viewModel = UserViewModel(repository)
    }
    
    @Test
    fun `should emit success when data loads`() = runTest {
        // Given
        val users = listOf(User(1, "John"))
        coEvery { repository.getUsers() } returns flowOf(users)
        
        // When
        viewModel.loadUsers()
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(users, (state as UiState.Success).data)
    }
}
```

## Repository 测试

```kotlin
class UserRepositoryTest {
    
    private lateinit var repository: UserRepository
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource
    
    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        repository = UserRepositoryImpl(remoteDataSource, localDataSource)
    }
    
    @Test
    fun `should return cached data when available`() = runTest {
        // Given
        val cached = listOf(User(1, "John"))
        coEvery { localDataSource.getUsers() } returns flowOf(cached)
        
        // When
        val result = repository.getUsers().first()
        
        // Then
        assertEquals(cached, result)
    }
}
```

## Compose UI 测试

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun displaysUserList() {
        // Given
        val users = listOf(User(1, "John"))
        
        // When
        composeTestRule.setContent {
            HomeScreen(uiState = UiState.Success(users))
        }
        
        // Then
        composeTestRule.onNodeWithText("John").assertIsDisplayed()
    }
}
```

## 测试工具

```kotlin
// MainDispatcherRule
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

// Test Data Factory
object TestDataFactory {
    fun createUser(id: Int = 1, name: String = "Test") = User(id, name)
    fun createUsers(count: Int = 3) = List(count) { createUser(it + 1) }
}
```

## 最佳实践
- ✅ 使用 Given-When-Then 结构
- ✅ 测试命名清晰描述性
- ✅ 每个测试独立
- ✅ Mock 外部依赖
- ✅ 使用 runTest 处理协程
- ❌ 不 Mock 被测对象
- ❌ 不依赖测试执行顺序

## 依赖
```kotlin
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("androidx.arch.core:core-testing:2.2.0")

androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
```

详细测试规范参考完整文档。
