---
inclusion: fileMatch
fileMatchPattern: '**/data/**'
---

# Android API Standards

网络层实现规范（编辑 data 层时自动加载）

## 架构层次
```
UI → ViewModel → Repository → DataSource → ApiService
```

## Retrofit 配置

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
            })
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

## API Service

```kotlin
interface ApiService {
    @GET("users")
    suspend fun getUsers(): ApiResponse<List<User>>
    
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): ApiResponse<User>
}
```

## 错误处理

```kotlin
sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Exception) : Result<Nothing>
    data object Loading : Result<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> ApiResponse<T>): Result<T> {
    return try {
        val response = apiCall()
        if (response.code == 0) {
            Result.Success(response.data!!)
        } else {
            Result.Error(Exception(response.message))
        }
    } catch (e: IOException) {
        Result.Error(e)
    }
}
```

## Repository 模式

```kotlin
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : UserRepository {
    
    override fun getUsers(): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)
        
        // 先发送缓存
        localDataSource.getUsers().first().let { cached ->
            if (cached.isNotEmpty()) emit(Result.Success(cached))
        }
        
        // 再获取网络数据
        when (val result = remoteDataSource.getUsers()) {
            is Result.Success -> {
                localDataSource.saveUsers(result.data)
                emit(result)
            }
            is Result.Error -> emit(result)
            else -> {}
        }
    }.flowOn(Dispatchers.IO)
}
```

## 最佳实践
- ✅ 使用 suspend 函数
- ✅ 用 Result/Resource 包装响应
- ✅ 使用 Flow 处理数据流
- ✅ 实现缓存策略
- ✅ 添加日志拦截器（仅 Debug）
- ✅ 使用依赖注入
- ❌ 不在主线程调用 API
- ❌ 不忽略错误响应
- ❌ 不硬编码 URL
- ❌ 不向 UI 层暴露 Retrofit 类型

详细规范参考完整文档。
