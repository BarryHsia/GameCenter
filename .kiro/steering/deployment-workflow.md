---
inclusion: manual
---

# Android Deployment Workflow

构建、测试、发布流程（按需加载）

## 版本管理

### 语义化版本
`MAJOR.MINOR.PATCH` (例: 1.0.0)

```kotlin
// app/build.gradle.kts
android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

### Git Tag
```bash
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

## 构建类型

```kotlin
android {
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## 构建命令

```bash
# 清理
./gradlew clean

# 测试
./gradlew test

# Lint
./gradlew lint

# 构建
./gradlew assembleDebug
./gradlew assembleRelease
```

## 签名配置

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

## 发布检查清单
- [ ] 测试通过
- [ ] Lint 检查通过
- [ ] 版本号已更新
- [ ] ProGuard 已启用
- [ ] 签名配置正确
- [ ] 敏感信息已移除

## CI/CD (GitHub Actions)

```yaml
name: Android CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
      - name: Run tests
        run: ./gradlew test
      - name: Build APK
        run: ./gradlew assembleDebug
```

详细流程参考完整文档。
