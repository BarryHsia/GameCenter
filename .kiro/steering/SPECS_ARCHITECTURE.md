---
inclusion: always
---

# 架构规范

采用 **MVVM + Clean Architecture**，强制依赖注入和单向数据流。

## 分层
- **presentation**：Activity/Fragment/Compose + ViewModel（持有状态），委托业务给domain层。  
- **domain**：UseCase（单一职责）、Repository接口、领域模型。不依赖Android框架。  
- **data**：Repository实现、本地/远程数据源、DTO与Mapper。

## 组件通信
- UI → ViewModel：调用ViewModel方法。  
- ViewModel → UI：通过`StateFlow`/`LiveData`暴露状态。  
- ViewModel → UseCase：调用UseCase（返回`Flow<Result<T>>`或挂起函数）。  
- UseCase → Repository：调用Repository接口。  
- Repository → DataSource：内部调用本地/远程源，转换数据。

## 依赖注入
- 使用 **Hilt**，构造函数注入为主，禁止手动new对象。  
- 在`@Module`中提供依赖，合理使用作用域。

## 异步
- 使用**协程**，禁止回调。ViewModel中使用`viewModelScope`，UseCase/Repository中使用挂起函数或`Flow`。