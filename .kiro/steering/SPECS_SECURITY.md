---
inclusion: always
---

# 安全规范

保护用户数据和防范漏洞是最高优先级。

## 数据存储
- **敏感信息**（Token、密码、身份证等）必须使用 **EncryptedSharedPreferences** 或 **Android Keystore** 加密存储。  
- **Token销毁**：  
  - 退出登录/注销时，必须从存储中清除Token（调用`edit().clear().apply()`）。  
  - Token过期时，自动触发清理并引导用户重新登录。  
  - 禁止将Token保存在`SharedPreferences`明文或内存中长时间保留。  
- 数据库加密：若存敏感数据，启用Room加密（SQLCipher）。

## 网络
- 全部请求使用HTTPS，Release启用**证书锁定**。  
- OkHttp配置证书锁定器，禁用明文流量，仅在Debug打印日志。

## 权限
- 最小权限原则，运行时请求必须使用`ActivityResultContracts`，处理拒绝/永久拒绝。

## 输入验证
- 验证所有外部输入（Intent、Deep Link），防止SQL注入和XSS。

## 日志
- Release版本禁止任何日志输出，用`BuildConfig.DEBUG`控制。

## 组件导出
- 未导出组件显式设置`exported=false`，必须导出的使用自定义权限。

## 混淆
- Release开启R8混淆，保留必要类（反射使用）。