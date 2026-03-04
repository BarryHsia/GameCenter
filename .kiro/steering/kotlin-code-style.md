---
inclusion: always
---

# Android Code Conventions

核心 Kotlin/Android 编码规范（精简版）

## 技术栈
- Kotlin + JVM 11+, Min SDK 21-24, Target SDK 最新
- UI: Jetpack Compose
- DI: Hilt/Dagger
- 网络: Retrofit + OkHttp
- 数据库: Room
- 状态: ViewModel + StateFlow

## 架构规范

### 分层架构 (推荐)
```
UI Layer (Compose/Activity)
    ↓
Presentation Layer (ViewModel)
    ↓
Domain Layer (UseCase) [可选]
    ↓
Data Layer (Repository)
    ↓
Data Sources (Remote/Local)
```

### 核心原则
- **单向数据流**: UI → ViewModel → Repository → DataSource
- **关注点分离**: UI 只负责显示，ViewModel 管理状态，Repository 处理数据
- **依赖注入**: 使用 Hilt，避免手动创建依赖
- **响应式编程**: 使用 Flow/StateFlow 传递数据

### 层级职责

**UI Layer** (Composable/Activity)
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 只负责 UI 渲染和用户交互
}
```

**ViewModel** (状态管理)
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // 处理业务逻辑，管理 UI 状态
}
```

**Repository** (数据协调)
```kotlin
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : UserRepository {
    // 协调本地和远程数据源，实现缓存策略
    override fun getUsers(): Flow<Result<List<User>>> = flow {
        // 先发送缓存，再获取网络数据
    }
}
```

**DataSource** (数据获取)
```kotlin
class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {
    // 只负责从 API 获取数据
}
```

### 常见模式

**MVI (Model-View-Intent)** - 适合复杂交互
```kotlin
sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Item>) : UiState
    data class Error(val message: String) : UiState
}

sealed interface UiEvent {
    data object Refresh : UiEvent
    data class ItemClick(val id: Int) : UiEvent
}
```

**MVVM (Model-View-ViewModel)** - 标准模式
```kotlin
// ViewModel 暴露 StateFlow，UI 观察状态变化
val uiState: StateFlow<UiState>
```

### 目录结构
```
app/src/main/java/com/company/app/
├── data/
│   ├── remote/      # API, DataSource
│   ├── local/       # Database, DAO
│   └── repository/  # Repository 实现
├── domain/          # UseCase (可选)
├── ui/
│   ├── home/        # HomeScreen, HomeViewModel
│   ├── details/     # DetailsScreen, DetailsViewModel
│   └── components/  # 可复用组件
└── di/              # Hilt Modules
```

### 依赖规则
- ✅ UI → ViewModel → Repository → DataSource
- ✅ 上层依赖下层（通过接口）
- ❌ 下层不依赖上层
- ❌ 跨层调用（UI 直接调用 Repository）

## 命名规范

| 类型 | 规则 | 示例 |
|------|------|------|
| 类/接口 | PascalCase | `UserRepository`, `ApiService` |
| 函数 | camelCase | `loadData()`, `fetchUsers()` |
| Composable | PascalCase | `HomeScreen()`, `UserCard()` |
| 变量 | camelCase | `val userName = "test"` |
| 私有 StateFlow | `_` 前缀 | `private val _uiState` |
| 公开 StateFlow | 无前缀 | `val uiState` |
| 常量 | UPPER_SNAKE_CASE | `const val MAX_RETRY = 3` |

## 核心规则

### Kotlin
```kotlin
// ✅ 优先使用 val
val name = "test"

// ✅ 避免 !!
val length = name?.length ?: 0

// ✅ 使用 sealed interface 定义状态
sealed interface UiState {
    data object Loading : UiState
    data class Success(val data: List<Item>) : UiState
    data class Error(val message: String) : UiState
}

// ✅ 使用 data class
data class User(val id: Int, val name: String)
```

### 协程
```kotlin
// ✅ 使用 viewModelScope
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // 自动取消
        }
    }
}

// ✅ Flow 异常处理
repository.getData()
    .catch { e -> emit(emptyList()) }
    .collect { data -> _uiState.value = UiState.Success(data) }
```

### Compose
```kotlin
// ✅ 状态提升
@Composable
fun ParentScreen(viewModel: MyViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ChildComponent(data = uiState.data, onItemClick = viewModel::onItemClick)
}

// ✅ 使用 key
LazyColumn {
    items(items = users, key = { it.id }) { user ->
        UserCard(user)
    }
}
```

## 日志安全
```kotlin
// ❌ 禁止记录敏感信息
Log.d(TAG, "Token: $token")  // 错误

// ✅ 脱敏或不记录
Log.d(TAG, "User logged in: ${user.id}")  // 正确
```

## 代码审查要点
- [ ] 命名符合规范
- [ ] 优先使用 `val`，避免 `!!`
- [ ] 使用 `sealed interface` 定义状态
- [ ] 协程使用 `viewModelScope`
- [ ] Flow 使用 `.catch` 处理异常
- [ ] Compose 状态提升，LazyList 提供 `key`
- [ ] 日志不包含敏感信息
- [ ] 遵循分层架构，避免跨层调用
- [ ] ViewModel 不持有 Context 引用
- [ ] Repository 是数据协调层，不包含业务逻辑

详细规范参考项目文档或 Android 官方指南。
