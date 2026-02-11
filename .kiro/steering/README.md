---
inclusion: manual
---

# Android Steering Files

精简版 Android 开发规范，优化 token 消耗。

## 文件列表

| 文件 | 加载方式 | 大小 | 用途 |
|------|---------|------|------|
| code-conventions.md | 始终加载 | ~3KB | 核心编码规范 + 架构规范 |
| security-policies.md | 始终加载 | ~2KB | 安全规范 |
| api-standards.md | 编辑 data 层时 | ~3KB | 网络层规范 |
| testing-standards.md | 编辑测试时 | ~3KB | 测试规范 |
| deployment-workflow.md | 手动引用 | ~2KB | 构建发布流程 |

## 加载策略

### 始终加载 (Always)
- `code-conventions.md` - 核心规范，保持精简
- `security-policies.md` - 安全规范，必须遵守

### 按需加载 (FileMatch)
- `api-standards.md` - 编辑 `**/data/**` 时自动加载
- `testing-standards.md` - 编辑 `**/test/**` 时自动加载

### 手动引用 (Manual)
- `deployment-workflow.md` - 需要时用 `#deployment-workflow` 引用
- `README.md` - 本文件，仅供人类阅读

## 优化说明

### Token 消耗优化
1. **精简内容**：只保留核心规则和关键示例
2. **按需加载**：使用 `fileMatch` 模式，只在相关文件时加载
3. **手动引用**：不常用的内容设为 manual，需要时才加载

### 对比原版
- 原版总大小: ~50KB (每次对话消耗大量 tokens)
- 精简版总大小: ~12KB
- 始终加载: ~4KB (减少 90%)
- 按需加载: ~6KB (仅在需要时)

## 使用方式

### 自动加载
编辑代码时自动加载相应规范：
- 编辑 `UserRepository.kt` → 自动加载 `api-standards.md`
- 编辑 `UserViewModelTest.kt` → 自动加载 `testing-standards.md`

### 手动引用
在对话中使用 `#` 引用：
```
#deployment-workflow  # 查看构建发布流程
#api-standards        # 查看完整 API 规范
#testing-standards    # 查看完整测试规范
```

## 完整文档

如需详细规范，可以：
1. 创建 `docs/` 目录存放完整文档
2. Steering 文件保持精简，链接到详细文档
3. 需要时手动查阅完整文档

## 自定义建议

### 项目特定规范
在 steering 文件末尾添加项目特定规则：
```markdown
## 项目特定规则
- 使用 Coil 3 加载图片
- 使用 TV Material3 组件
- 焦点导航遵循 D-pad 规范
```

### 调整加载策略
根据项目需要调整 front-matter：
```yaml
---
inclusion: fileMatch
fileMatchPattern: '**/ui/**'  # 编辑 UI 层时加载
---
```

## 维护建议

1. **定期审查**：每月检查规范是否过时
2. **保持精简**：新增内容前考虑是否必要
3. **团队对齐**：团队讨论后统一修改
4. **版本控制**：重要修改记录在 git commit

---

**优化效果**：
- Token 消耗减少 ~80%
- 加载速度更快
- 保持核心规范完整性
- 按需加载详细内容

**最后更新**: 2024-01-15
