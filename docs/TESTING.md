# GameCenter 测试指南

## 测试架构概览

项目采用多层测试策略，覆盖从数据模型到 UI 交互的各个层面。

```
app/src/test/          ← 本地单元测试（JVM，无需设备）
app/src/androidTest/   ← 仪器测试（需要 Android 设备/模拟器）
```

## 测试类型

### 1. 单元测试（Unit Tests）

在 JVM 上运行，速度快，不依赖 Android 框架。

| 测试文件 | 测试目标 | 说明 |
|---|---|---|
| `ApiResponseTest` | `ApiResponse` 数据类 | 验证构造、默认值、null 处理 |
| `PlayRecordTest` | `PlayRecord` 实体 | 验证默认值、InfoParam 接口实现 |
| `GameRepositoryTest` | `GameRepository` | 使用 MockK mock AppApi，验证委托调用 |

运行命令：
```bash
./gradlew test
```

### 2. 集成测试（Integration Tests）

验证多个组件协同工作的正确性。

| 测试文件 | 测试目标 | 说明 |
|---|---|---|
| `MockWebServerApiTest` | Retrofit + Gson 解析 | 使用 MockWebServer 模拟 HTTP 响应，验证 API 解析 |

运行命令（包含在单元测试中）：
```bash
./gradlew test
```

### 3. 数据库测试（Database / DAO Tests）

在真实 Android 环境中使用 Room 内存数据库验证 DAO 操作。

| 测试文件 | 测试目标 | 说明 |
|---|---|---|
| `PlayRecordDaoTest` | `PlayRecordDao` | 插入、更新、排序、限制数量删除 |

运行命令：
```bash
./gradlew connectedAndroidTest
```

### 4. API 端到端测试（E2E API Tests）

直接调用真实服务器接口，验证网络连通性和响应格式。

| 测试文件 | 测试目标 | 说明 |
|---|---|---|
| `ApiServiceTest` | `ApiService` | 真实网络请求测试 getInfo、search2 |

> 注意：这类测试依赖网络和服务器可用性，不适合 CI 环境。

## 测试依赖

| 库 | 用途 | 作用域 |
|---|---|---|
| JUnit 4 | 测试框架 | test / androidTest |
| MockK | Kotlin mock 框架 | test |
| MockK Android | Android 环境 mock | androidTest |
| kotlinx-coroutines-test | 协程测试工具 | test / androidTest |
| Turbine | Flow 测试工具 | test / androidTest |
| MockWebServer | HTTP mock 服务器 | test |
| Arch Core Testing | LiveData 测试 | test |
| Hilt Testing | DI 测试支持 | androidTest |
| Compose UI Test | Compose UI 测试 | androidTest |
| Room Testing | 数据库迁移测试 | androidTest |

## 测试目录结构

```
app/src/test/java/com/kgzn/gamecenter/
├── data/
│   ├── remote/
│   │   ├── ApiResponseTest.kt          # 数据类单元测试
│   │   └── MockWebServerApiTest.kt     # API 集成测试
│   └── repository/
│       └── GameRepositoryTest.kt       # Repository 单元测试
├── db/
│   └── playrecord/
│       └── PlayRecordTest.kt           # 实体单元测试
└── ExampleUnitTest.kt

app/src/androidTest/java/com/kgzn/gamecenter/
├── data/
│   └── remote/
│       └── ApiServiceTest.kt           # API E2E 测试
├── db/
│   └── PlayRecordDaoTest.kt            # DAO 仪器测试
└── ExampleInstrumentedTest.kt
```

## 编写新测试的建议

- ViewModel 测试：使用 MockK mock Repository，用 Turbine 测试 StateFlow
- Repository 测试：使用 MockK mock AppApi 接口
- API 解析测试：使用 MockWebServer 避免真实网络依赖
- DAO 测试：使用 `Room.inMemoryDatabaseBuilder` 创建内存数据库
- Compose UI 测试：使用 `createComposeRule()` 配合 `composeTestRule.setContent {}`
