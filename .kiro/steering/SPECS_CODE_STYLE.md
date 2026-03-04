---
inclusion: always
---

# 代码风格规范

遵循Kotlin官方约定，附加项目规则。

## 命名
- 包：全小写，按功能分层（`feature.home`），禁止按类型分包。  
- 类/接口：PascalCase。  
- 函数/变量：camelCase。  
- 常量：UPPER_SNAKE_CASE。  
- Compose组件：PascalCase，`@Composable`。

## 格式
- 缩进4空格，行宽120。  
- 表达式体函数优先用`=`。  
- 公开API必须加KDoc。

## Kotlin特性
- 数据容器用`data class`。  
- 受限状态用`sealed class/interface`。  
- 避免`!!`，用安全调用或Elvis。  
- 优先不可空类型。

## 禁止
- 硬编码字符串（除资源文件）。  
- `Any`作为泛型。  
- 不必要的`lateinit var`。