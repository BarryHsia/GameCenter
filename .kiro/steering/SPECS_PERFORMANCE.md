---
inclusion: manual
---

# 性能规范

优化响应速度和资源占用。

## 内存
- 避免持有Activity/Context引用，使用ApplicationContext。  
- 图片用Glide/Coil，配置缓存。  
- 使用LeakCanary检测泄漏。

## 布局
- 减少层级，用ConstraintLayout或Compose。  
- 长列表用RecyclerView/LazyColumn，复用视图。

## 线程
- 主线程禁止IO，协程中使用`Dispatchers.IO`/`Default`。

## 启动
- Application中减少初始化，用App Startup按需加载。

## 网络
- 合并请求，分页加载，设置合理超时。