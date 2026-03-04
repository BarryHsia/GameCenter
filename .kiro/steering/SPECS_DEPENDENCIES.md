---
inclusion: fileMatch
fileMatchPattern: '**/build.gradle.kts,**/libs.versions.toml'
---

# 依赖管理规范

统一、安全、可维护的依赖管理。

## 库选择
- 优先Google/Jetpack官方库。  
- 第三方库需活跃维护、广泛使用、兼容许可证。  
- 避免功能重叠（如RxJava+协程同时用）。

## 版本管理
- 所有版本号统一在`gradle/libs.versions.toml`中定义。  
- 定期更新，但重大版本升级需谨慎。

## 冲突解决
- 优先升级兼容版本，而不是排除。  
- 使用`./gradlew :app:dependencies`分析冲突。

## 模块依赖
- 模块间用`implementation project(':core')`，避免循环依赖。  
- 对外暴露API用`api`需谨慎。

## 安全
- 启用Dependabot或OWASP检查漏洞，高危漏洞立即修复。