---
inclusion: always
---

# 错误处理规范

统一错误处理，提升稳定性和用户体验。

## 统一返回类型
- 定义密封类`Result<T>`：`Success<T>`、`Error(exception, message)`、`Loading`。  
- UseCase和Repository返回`Flow<Result<T>>`或挂起函数返回`Result<T>`。

## 异常捕获
- ViewModel协程中用`try-catch`或`catch`操作符捕获异常，转换为`Result.Error`。  
- 禁止空catch块，至少记录日志。

## 用户反馈
- 所有UI层错误必须显示友好提示（Snackbar/Dialog），避免技术堆栈。  
- 可恢复错误提供重试。

## 日志
- 使用Timber.e()记录错误，Release可上报Crashlytics。