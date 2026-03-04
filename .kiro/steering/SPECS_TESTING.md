---
inclusion: fileMatch
fileMatchPattern: '**/test/**,**/androidTest/**'
---

# 测试规范

保证核心逻辑质量。

## 测试类型
- **单元测试**：UseCase、ViewModel、Repository逻辑，依赖使用Mock或Fake。  
- **集成测试**：数据库、网络真实交互（用MockWebServer、内存数据库）。  
- **UI测试**：Compose UI测试或Espresso。

## 要求
- 每个UseCase至少一个成功/失败测试。  
- ViewModel测试用`runTest`和`Molecule`验证状态流。  
- 核心逻辑覆盖率目标>80%。

## 命名
- 测试方法名：`should_expected_when_condition` 或反引号包围的描述句。