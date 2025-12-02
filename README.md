# KotlinToolbox

我的 Kotlin Multiplatform 工具箱，包含文件操作、HTTP 请求和系统信息。

## 包含的工具

### 1. LZHttpClient - HTTP 客户端
基于 Ktor 的 HTTP 请求封装，提供简洁的 API。

**功能：**
- `get<T>(url, parameters)` - GET 请求
- `post<T>(url, body)` - POST 请求
- 自动 JSON 序列化/反序列化
- 内置日志记录
- 返回 `Result<T>` 统一错误处理

**使用：**
```kotlin
val http = LZHttpClient()

lifecycleScope.launch {
    // GET 请求
    val result = http.get<MyData>("https://api.example.com/data")
    result.onSuccess { data ->
        println("成功: $data")
    }.onFailure { error ->
        println("失败: ${error.message}")
    }
    
    // POST 请求
    val postResult = http.post<Response>(
        "https://api.example.com/submit",
        body = mapOf("key" to "value")
    )
    
    // 用完记得关闭
    http.close()
}
```

### 2. LZFileManager - 文件管理
基于 kotlinx-io 的跨平台文件操作。**所有方法都是 `suspend` 函数**。

**功能：**
- `listFiles(path)` - 列出目录文件
- `deleteFile(path)` - 删除文件
- `moveFile(src, dest)` - 移动文件
- `copyFile(src, dest)` - 复制文件
- `unzip(zipPath)` - 解压 ZIP（JVM/Android）

**使用：**
```kotlin
val fm = LZFileManager()

lifecycleScope.launch {
    // 列出文件
    val result = fm.listFiles("/path/to/dir")
    when (result) {
        is FileResult.Success -> result.data.forEach { (name, path) -> ... }
        is FileResult.Error -> println("${result.code}: ${result.message}")
    }
    
    // 删除/移动/复制
    fm.deleteFile("/file.txt")
    fm.moveFile("/src.txt", "/dest.txt")
    fm.copyFile("/src.txt", "/copy.txt")
    
    // 解压
    val unzipResult = fm.unzip("/archive.zip")
}
```

### 3. LZFileDownloader - 文件下载
基于 Ktor 的流式下载，带进度。

**使用：**
```kotlin
val http = LZHttpClient()
val downloader = LZFileDownloader(http.client)  // 复用 HttpClient

lifecycleScope.launch {
    downloader.download("https://example.com/file.zip", "/save/path.zip")
        .collect { status ->
            when (status) {
                is DownloadStatus.Downloading -> {
                    println("${(status.progress * 100).toInt()}%")
                }
                is DownloadStatus.Completed -> println("完成")
                is DownloadStatus.Error -> println(status.message)
                else -> {}
            }
        }
    
    http.close()  // 关闭 HttpClient
}
```

### 4. LZSystemInfo - 系统信息
获取当前运行环境的系统信息。

**使用：**
```kotlin
val info = LZSystemInfo.get()
println("OS: ${info.osName} ${info.osVersion}")
println("Arch: ${info.arch}")
println("Model: ${info.model}")
```

## 错误处理

所有操作都返回结构化结果，不会乱打 log：

```kotlin
// LZFileManager 返回 FileResult
when (result) {
    is FileResult.Success -> // 拿 result.data
    is FileResult.Error -> // 拿 result.message 和 result.code
}

// LZFileDownloader 通过 Flow 发射 DownloadStatus
// Error 状态包含 message 和 code
```

**错误代码：**
- `FILE_NOT_FOUND` - 文件不存在
- `PERMISSION_DENIED` - 权限不足
- `UNSUPPORTED_PLATFORM` - 平台不支持（iOS 解压）
- `IO_ERROR` - I/O 错误
- `UNKNOWN` - 其他错误

## 平台支持

| 功能 | JVM | Android | iOS |
|------|-----|---------|-----|
| HTTP 请求 | ✅ | ✅ | ✅ |
| 文件列表/删除/移动/复制 | ✅ | ✅ | ✅ |
| ZIP 解压 | ✅ | ✅ | ❌ |
| 文件下载 | ✅ | ✅ | ✅ |
| 系统信息 | ✅ | ✅ | ✅ |

iOS 解压返回 `UNSUPPORTED_PLATFORM` 错误，需要自己用原生库。

## 依赖

```kotlin
// gradle/libs.versions.toml
kotlinx-io = "0.8.2"
ktor = "3.3.3"

// build.gradle.kts - commonMain
implementation(libs.kotlinx.io.core)
implementation(libs.ktor.client.core)
implementation(libs.ktor.client.cio)
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.serialization.kotlinx.json)
implementation(libs.ktor.client.logging)
```

## 使用方式

**注意**：`LZFileManager` 和 `LZHttpClient` 的所有方法都是 `suspend` 函数，需要在协程中调用：
```kotlin
import lzdev42.kotlintoolbox.LZHttpClient
import lzdev42.kotlintoolbox.LZFileManager
import lzdev42.kotlintoolbox.LZFileDownloader
import lzdev42.kotlintoolbox.LZSystemInfo
```

示例用法：
```kotlin
lifecycleScope.launch {
    val http = LZHttpClient()
    val fm = LZFileManager()
    val downloader = LZFileDownloader(http.client)
    
    // 使用...
    
    http.close()  // 记得关闭
}
```

## 依赖

添加到 `build.gradle.kts`：
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("lzdev42:kotlintoolbox:0.2.0")
}

就这些。