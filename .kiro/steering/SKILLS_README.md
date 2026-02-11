---
inclusion: manual
---

# Kiro Skills 使用指南

Skills 是 Kiro 的专业能力扩展包，提供特定领域的功能和工具。

## 什么是 Skill？

Skill 是一个独立的功能模块，包含：
- **SKILL.md**: Skill 的核心文档和使用说明
- **scripts/**: 可执行脚本（如果需要）
- **references/**: 参考文档和示例
- **assets/**: 资源文件（图片、模板等）

## 已安装的 Skills

### generate-image
**功能**: 使用 ByteDance Seedream 4.5 生成 AI 图片

**适用场景**:
1. 根据文本描述创建图片
2. 生成项目工程架构图
3. 生成序列图
4. 其他需要图片增强展示的场景

**使用方式**:
```
请帮我生成一张 Android 应用架构图
```

**位置**: `.kiro/skills/generate-image/`

## Skill vs Steering 的区别

| 特性 | Steering | Skill |
|------|----------|-------|
| 用途 | 编码规范、最佳实践 | 专业功能、工具集成 |
| 加载方式 | 自动/按需/手动 | 需要时激活 |
| 内容类型 | 文档、规范、指南 | 功能、脚本、工具 |
| 示例 | 代码规范、安全策略 | 图片生成、代码分析 |

## 如何使用 Skill

### 方式 1: 直接请求
直接描述你的需求，Kiro 会自动识别并激活相应的 Skill：
```
帮我生成一张展示 MVVM 架构的图片
```

### 方式 2: 明确指定
明确提到 Skill 名称：
```
使用 generate-image skill 创建一张登录流程图
```

### 方式 3: 查看 Skill 文档
手动引用 Skill 文档：
```
#generate-image  # 查看图片生成 Skill 的详细说明
```

## Skill 目录结构

```
.kiro/skills/
├── generate-image/
│   ├── SKILL.md           # Skill 核心文档
│   ├── references/        # 参考文档
│   │   └── text2img.md   # 文本转图片说明
│   ├── scripts/           # 可执行脚本
│   └── assets/            # 资源文件
└── [其他 skills]/
```

## 安装新的 Skill

### 从 Kiro Marketplace 安装
1. 打开命令面板 (Ctrl+Shift+P)
2. 搜索 "Kiro: Browse Skills"
3. 选择并安装需要的 Skill

### 手动安装
1. 在 `.kiro/skills/` 创建新目录
2. 添加 `SKILL.md` 文件（必需）
3. 按需添加 `scripts/`, `references/`, `assets/` 目录
4. 重启 Kiro 或重新加载窗口

## 创建自定义 Skill

### 基本结构
```markdown
---
name: my-skill
description: "简短描述 Skill 的功能和用途"
---

# My Skill

详细说明 Skill 的功能、使用方法和示例。

## 使用场景
列出适用的场景

## 使用方法
提供具体的使用步骤和示例
```

### 最佳实践
1. **清晰的描述**: 在 front-matter 中提供简洁的功能描述
2. **详细的文档**: 在 SKILL.md 中提供完整的使用说明
3. **实用的示例**: 提供真实的使用场景和代码示例
4. **组织良好**: 使用子目录组织脚本、文档和资源

## 常见问题

### Q: Skill 和 Steering 可以一起使用吗？
A: 可以。Steering 提供规范指导，Skill 提供功能工具，两者互补。

### Q: 如何禁用某个 Skill？
A: 重命名或移动 Skill 目录到 `.kiro/skills/` 之外。

### Q: Skill 会自动更新吗？
A: 从 Marketplace 安装的 Skill 可以自动更新，手动安装的需要手动更新。

### Q: 可以创建团队共享的 Skill 吗？
A: 可以。将 `.kiro/skills/` 目录提交到 Git，团队成员克隆后即可使用。

## 推荐 Skills（示例）

根据你的项目类型，可能需要的 Skills：

**Android 开发**:
- `generate-image`: 生成架构图、流程图
- `code-analyzer`: 代码质量分析
- `dependency-checker`: 依赖版本检查

**通用开发**:
- `git-helper`: Git 操作辅助
- `doc-generator`: 文档生成
- `test-generator`: 测试用例生成

## 维护建议

1. **定期清理**: 删除不再使用的 Skill
2. **保持更新**: 定期检查 Skill 更新
3. **文档同步**: 修改 Skill 后更新文档
4. **团队共享**: 有用的自定义 Skill 分享给团队

---

**相关文档**:
- [Steering 使用指南](./STEERING_README.md)
- [Kiro 官方文档](https://kiro.dev/docs)

**最后更新**: 2026-02-11
