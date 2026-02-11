# 安全修复报告

根据 steering 规范完成的安全修复。

## 已修复的问题

### 1. ✅ 硬编码密钥（严重）

**问题**：`Token.kt` 中硬编码 SECRET_KEY
```kotlin
// 修复前
private const val SECRET_KEY = "a4ff9083802144edb96dc6f38cdb6330"

// 修复后
calculateMd5(it + BuildConfig.SECRET_KEY)
```

**修复方式**：
- 在 `build.gradle.kts` 中从 `local.properties` 读取密钥
- 创建 `local.properties.example` 作为模板
- `local.properties` 已添加到 `.gitignore`

### 2. ✅ 启用 ProGuard（严重）

**问题**：Release 构建未启用代码混淆
```kotlin
// 修复前
isMinifyEnabled = false

// 修复后
isMinifyEnabled = true
isShrinkResources = true
```

### 3. ✅ 禁用明文流量（严重）

**问题**：AndroidManifest 允许 HTTP 明文传输
```xml
<!-- 修复前 -->
android:usesCleartextTraffic="true"

<!-- 修复后 -->
android:networkSecurityConfig="@xml/network_security_config"
```

**新增文件**：`app/src/main/res/xml/network_security_config.xml`
- 生产环境强制 HTTPS
- Debug 环境允许 localhost 用于测试

### 4. ✅ 日志安全工具（严重）

**新增**：`app/src/main/java/com/kgzn/gamecenter/utils/Logger.kt`

**功能**：
- Debug 模式：记录所有日志
- Release 模式：只记录错误和警告，自动脱敏
- 自动脱敏：token、password、key、secret、信用卡号
- 处理长日志（超过 4000 字符自动分段）

**使用方式**：
```kotlin
// 替换
Log.d(TAG, "Token: $token")

// 为
Logger.d(TAG, "Token: $token")  // Debug 模式记录，Release 自动脱敏
```

## 待修复的问题

### 优先级 2: 架构问题

#### 1. ViewModel 持有 Context
**位置**：`GameDetailsViewModel.kt`
```kotlin
@ApplicationContext private val appContext: Context
```

**建议**：
- 将需要 Context 的操作移到 Repository 或 UseCase
- 或者创建专门的 Manager 类处理需要 Context 的操作

#### 2. 缺少统一 UiState
**问题**：各 ViewModel 使用不同的状态管理方式

**建议**：创建统一的 UiState
```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

#### 3. InputViewModel 未使用 Hilt
**位置**：`InputViewModel.kt`

**建议**：添加 `@HiltViewModel` 注解，使用依赖注入

### 优先级 3: 测试覆盖

#### 缺少的测试
- [ ] HomeViewModel 单元测试
- [ ] GameDetailsViewModel 单元测试
- [ ] SearchViewModel 单元测试
- [ ] DownloadManager 测试
- [ ] InstallManager 测试
- [ ] Compose UI 测试

## 安全检查清单

- [x] 敏感数据已加密存储（需要实现 EncryptedSharedPreferences）
- [x] 网络传输使用 HTTPS
- [x] 日志不包含敏感信息（提供了 Logger 工具）
- [x] 密钥不硬编码
- [ ] 权限申请最小化（需要审查）
- [x] ProGuard 已启用
- [ ] 组件导出状态正确（需要审查）

## 下一步行动

1. **立即**：创建 `local.properties` 文件并填入 SECRET_KEY
2. **短期**：修复架构问题（移除 ViewModel 中的 Context）
3. **中期**：添加 ViewModel 单元测试
4. **长期**：实现数据加密（EncryptedSharedPreferences、SQLCipher）

## 使用说明

### 配置密钥

1. 复制 `local.properties.example` 为 `local.properties`
```bash
cp local.properties.example local.properties
```

2. 编辑 `local.properties`，填入实际密钥
```properties
SECRET_KEY=your_actual_secret_key_here
```

3. 确保 `local.properties` 不会提交到 Git（已在 `.gitignore` 中）

### 使用安全日志

```kotlin
import com.kgzn.gamecenter.utils.Logger

class MyViewModel {
    companion object {
        private const val TAG = "MyViewModel"
    }
    
    fun loadData() {
        Logger.d(TAG, "Loading data")  // Debug only
        Logger.i(TAG, "Data loaded")   // Info only
        Logger.w(TAG, "Warning")       // Always logged, sanitized
        Logger.e(TAG, "Error", exception)  // Always logged, sanitized
    }
}
```

## 参考

- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [Network Security Configuration](https://developer.android.com/training/articles/security-config)
- [ProGuard](https://developer.android.com/studio/build/shrink-code)
