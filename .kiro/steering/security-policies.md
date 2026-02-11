---
inclusion: always
---

# Android Security Policies

核心安全规范（精简版）

## 数据存储安全

```kotlin
// ✅ 使用 EncryptedSharedPreferences
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context, "secure_prefs", masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

// ✅ Room 数据库加密（SQLCipher）
val passphrase = SQLiteDatabase.getBytes("passphrase".toCharArray())
val factory = SupportFactory(passphrase)
Room.databaseBuilder(context, AppDatabase::class.java, "db")
    .openHelperFactory(factory)
    .build()
```

## 网络安全

```kotlin
// ✅ 强制 HTTPS
val client = OkHttpClient.Builder()
    .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
    .build()
```

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

## 日志安全

```kotlin
// ❌ 禁止记录
Log.d(TAG, "Token: $token")
Log.d(TAG, "Password: $password")

// ✅ 脱敏或不记录
Log.d(TAG, "Token: ${token.take(4)}***")
Log.d(TAG, "User logged in: ${user.id}")
```

## 密钥管理

```kotlin
// ❌ 禁止硬编码
const val API_KEY = "1234567890"

// ✅ 使用 BuildConfig
val apiKey = BuildConfig.API_KEY
```

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "API_KEY", "\"${project.findProperty("API_KEY")}\"")
    }
}
```

## 代码混淆

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

## 权限管理

```kotlin
// ✅ 检查并请求权限
if (ContextCompat.checkSelfPermission(context, permission) 
    != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
}
```

## 安全检查清单
- [ ] 敏感数据已加密存储
- [ ] 网络传输使用 HTTPS
- [ ] 日志不包含敏感信息
- [ ] 密钥不硬编码
- [ ] 权限申请最小化
- [ ] ProGuard 已启用
- [ ] 组件导出状态正确

详细安全规范参考完整文档。
