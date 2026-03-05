# Kiro Skills 说明

Skills 是 Kiro 的能力扩展模块，提供特定领域的功能和工具。与 Steering（编码规范）不同，Skill 在需要时激活，提供可执行的功能。

## 已安装 Skills

| Skill | 功能 | 触发方式 |
|-------|------|---------|
| generate-image | 使用 ByteDance Seedream 4.5 生成 AI 图片 | 对话中提到图片生成相关需求时自动激活 |

### generate-image

基于文本描述生成图片，适用于架构图、流程图、序列图等场景。

使用示例：
```
帮我生成一张 MVVM 架构图
使用 generate-image 创建登录流程图
```

生成的图片保存在项目根目录 `seedream-generate/` 文件夹。

位置：`.kiro/skills/generate-image/`

## 目录结构

```
.kiro/skills/
├── SKILLS_README.md            # 本文件
└── generate-image/
    ├── SKILL.md                # Skill 核心文档
    ├── references/
    │   └── text2img.md         # 文本转图片参考
    └── scripts/
        └── text2img.py         # 图片生成脚本
```

## Skill 结构说明

每个 Skill 是一个独立目录，必须包含 `SKILL.md`，可选包含：
- `scripts/` - 可执行脚本
- `references/` - 参考文档
- `assets/` - 资源文件

## 安装与管理

- 命令面板 `Ctrl+Shift+P` → 搜索 "Kiro: Browse Skills" 浏览可用 Skill
- 手动安装：在 `.kiro/skills/` 下创建目录并添加 `SKILL.md`
- 禁用：将 Skill 目录移出 `.kiro/skills/`
- 团队共享：将 `.kiro/skills/` 提交到 Git

## 相关文档

- Steering 规范索引：`.kiro/steering/STEERING_SPEC_README.md`
- Hooks 说明：`.kiro/hooks/HOOKS_README.md`

最后更新：2026-03-05
