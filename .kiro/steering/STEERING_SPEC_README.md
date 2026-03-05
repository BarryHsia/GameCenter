---
inclusion: manual
---

# Steering 规范文件说明

本项目的 AI 编码规范分为两类：**SPECS 规范**（核心开发规范）和**工具规范**（Git/发布流程）。

## SPECS 规范文件

| 文件 | 加载方式 | 用途 |
|------|---------|------|
| SPECS_ARCHITECTURE.md | 始终加载 | 架构：MVVM + Clean Architecture、分层、DI、协程 |
| SPECS_CODE_STYLE.md | 始终加载 | 代码风格：命名、格式、Kotlin 特性 |
| SPECS_SECURITY.md | 始终加载 | 安全：加密存储、Token 销毁、HTTPS、证书锁定、混淆 |
| SPECS_ERROR_HANDLING.md | 始终加载 | 错误处理：Result 密封类、异常捕获、用户反馈 |
| SPECS_TESTING.md | 按需加载 | 测试：单元/集成/UI 测试规范（编辑测试文件时加载） |
| SPECS_DEPENDENCIES.md | 按需加载 | 依赖管理：库选择、版本管理、冲突解决（编辑 gradle 文件时加载） |
| SPECS_PERFORMANCE.md | 手动引用 | 性能：内存、布局、线程、启动优化 |
| SPECS_COMMIT.md | 始终加载 | Git 提交规则 + CHANGELOG 格式 |

## 工具规范文件

无额外工具规范文件。

## 加载策略说明

### 始终加载 (Always)
每次对话自动加载，确保核心规范始终生效：
- `SPECS_ARCHITECTURE.md` - 架构
- `SPECS_CODE_STYLE.md` - 代码风格
- `SPECS_SECURITY.md` - 安全
- `SPECS_ERROR_HANDLING.md` - 错误处理
- `SPECS_COMMIT.md` - Git 提交规则

### 按需加载 (FileMatch)
编辑特定文件时自动加载：
- `SPECS_TESTING.md` → 编辑 `**/test/**` 或 `**/androidTest/**`
- `SPECS_DEPENDENCIES.md` → 编辑 `**/build.gradle.kts` 或 `**/libs.versions.toml`

### 手动引用 (Manual)
在对话中用 `#` 引用：
```
#SPECS_PERFORMANCE    # 性能优化规范
```

## AI 生成代码自检清单

- [ ] 是否符合架构分层（UI/Domain/Data）？
- [ ] 敏感数据是否加密？Token 是否安全存储并正确处理销毁？
- [ ] 命名和格式是否符合代码风格？
- [ ] 异常是否被捕获并转换为 Result 返回？
- [ ] 是否为核心逻辑编写了单元测试？
- [ ] 是否有明显的性能问题？

## 其他文件

| 文件 | 位置 | 用途 |
|------|------|------|
| SKILLS_README.md | `.kiro/skills/` | Kiro Skills 功能扩展说明 |
| STEERING_SPEC_README.md | `.kiro/steering/` | 本文件，规范文件索引 |
| HOOKS_README.md | `.kiro/hooks/` | Agent Hooks 说明 |
