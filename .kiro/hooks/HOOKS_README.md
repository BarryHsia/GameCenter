# Agent Hooks 说明

本项目配置的 Kiro Agent Hooks，用于自动化代码质量保障。

## Hook 列表

| Hook | 触发方式 | 用途 |
|------|---------|------|
| kotlin-save-check | 保存 .kt 文件 | 自动检查编译错误和类型错误 |
| security-audit | 编辑 data 层代码 | 安全审查（硬编码密钥、日志泄露、加密存储） |
| xml-resource-check | 编辑 XML 文件 | 资源引用检查、命名规范、exported 属性 |
| gradle-dependency-check | 编辑 Gradle 文件 | 依赖兼容性、重复依赖、版本统一管理 |
| proguard-reminder | 新建 .kt 文件 | 提醒反射/序列化类添加混淆规则 |
| code-quality-review | Agent 停止时 | 构建成功后自动代码质量审查 |

## 触发方式说明

- `fileEdited` - 用户保存文件时自动触发
- `fileCreated` - 用户新建文件时自动触发
- `agentStop` - Agent 完成任务后自动触发

## 设计原则

1. 无问题不输出：所有 hook 在检查通过时保持静默，不打扰开发流程
2. 按需触发：通过文件匹配模式精确控制触发范围
3. 聚焦单一职责：每个 hook 只关注一个检查维度

## 启用/禁用

在 hook 文件中修改 `"enabled"` 字段：
- `true` - 启用
- `false` - 禁用（保留配置但不触发）

如果 `kotlin-save-check` 触发过于频繁影响体验，建议优先禁用它。
