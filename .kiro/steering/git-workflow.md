---
inclusion: always
---

# Git 提交工作流规范

## CHANGELOG 更新规则

**重要**: 每次代码修改必须同时更新 CHANGELOG.md

### 何时更新 CHANGELOG

✅ **需要更新**:
- 修复 Bug
- 添加新功能
- 重构代码
- 性能优化
- 安全修复

❌ **无需更新**:
- 纯文档更新（README、注释）
- 格式化代码（无逻辑变更）
- 依赖版本更新（无功能影响）

### 提交流程

**在使用 `mcp_github_push_files` 或 `mcp_github_create_or_update_file` 前**:

1. **检查**: 本次修改是否需要更新 CHANGELOG？
2. **更新**: 如果需要，在 `docs/CHANGELOG.md` 开头添加条目
3. **格式**: `## YYYY-MM-DD HH:MM | [本次提交] | 描述（中文）`
4. **推送**: 将代码文件和 CHANGELOG.md 一起推送

### 示例

```markdown
# 错误做法 ❌
1. 推送代码修改
2. 单独推送 CHANGELOG 更新  # 产生两个 commit

# 正确做法 ✅
1. 修改代码文件
2. 更新 docs/CHANGELOG.md
3. 使用 mcp_github_push_files 同时推送  # 一个 commit
```

### CHANGELOG 格式

```markdown
## 2026-02-11 19:45 | [本次提交] | 修复：解决登录页面崩溃问题

## 2026-02-11 18:30 | [本次提交] | 新增：添加用户头像上传功能

## 2026-02-11 17:15 | [本次提交] | 重构：优化网络请求重试逻辑
```

### 提交信息规范

使用约定式提交（Conventional Commits）:

- `feat:` - 新功能
- `fix:` - Bug 修复
- `refactor:` - 重构
- `perf:` - 性能优化
- `docs:` - 文档更新
- `test:` - 测试相关
- `chore:` - 构建/工具相关

**示例**:
```
feat: 添加用户头像上传功能
fix: 解决登录页面崩溃问题
refactor: 优化网络请求重试逻辑
```

## 自动检查清单

在推送前，我会自动检查：

- [ ] 是否有代码文件被修改？
- [ ] 修改是否需要记录到 CHANGELOG？
- [ ] CHANGELOG.md 是否已更新？
- [ ] CHANGELOG 条目格式是否正确？
- [ ] 提交信息是否符合规范？

如果检查不通过，我会提醒你补充 CHANGELOG。
