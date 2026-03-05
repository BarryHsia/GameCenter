---
name: memory-leak-analyzer
description: "Android 内存泄漏静态分析工具。扫描 Kotlin/Java 源码，检测常见内存泄漏模式：静态持有 Context/View、未注销监听器、Handler 泄漏、内部类持有外部引用等。"
---

# Android 内存泄漏分析

扫描项目源码，检测常见的 Android 内存泄漏模式并给出修复建议。

## 检测规则

### 1. 静态引用泄漏
- `companion object` 或 `static` 字段持有 Context、Activity、View、Fragment
- 单例持有 Activity/Fragment 引用

### 2. 内部类泄漏
- 非静态内部类（inner class）持有外部 Activity/Fragment 引用
- 匿名内部类在异步回调中持有外部引用

### 3. Handler 泄漏
- 非静态 Handler 内部类
- Handler 未在 onDestroy 中 removeCallbacksAndMessages

### 4. 监听器/回调未注销
- registerReceiver 无对应 unregisterReceiver
- addObserver 无对应 removeObserver
- registerOnSharedPreferenceChangeListener 无对应 unregister

### 5. 协程/异步泄漏
- 在 Activity/Fragment 中使用 GlobalScope
- 协程未绑定生命周期（应使用 lifecycleScope/viewModelScope）

### 6. 资源未释放
- Cursor 未 close
- TypedArray 未 recycle
- Bitmap 未 recycle（大对象场景）

## 使用方式

```
分析项目中的内存泄漏风险
扫描 app/src/main 下的内存泄漏
检查 HomeActivity.kt 是否有内存泄漏
```

## 分析流程

1. 运行 `scripts/analyze.py` 扫描指定目录
2. 输出检测结果（文件、行号、规则、严重级别、修复建议）
3. 按严重级别排序：🔴 高危 → 🟡 中危 → 🟢 低危

## 输出格式

```
🔴 [HIGH] app/src/.../HomeActivity.kt:45
   规则: static-context-leak
   问题: companion object 持有 Activity Context
   修复: 改用 ApplicationContext 或 WeakReference

🟡 [MEDIUM] app/src/.../GameFragment.kt:120
   规则: handler-leak
   问题: 非静态 Handler 内部类
   修复: 改为静态内部类 + WeakReference，onDestroy 中 removeCallbacksAndMessages(null)
```
