package lzdev42.kotlintoolbox

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * HTTP 客户端封装
 * 提供简单易用的 HTTP 请求接口，类似 Alamofire
 * 
 * 使用示例：
 * ```kotlin
 * val http = LZHttpClient()
 * val result = http.get<MyData>("https://api.example.com/data")
 * http.close()  // 用完关闭
 * ```
 */
class LZHttpClient : AutoCloseable {
    /**
     * 暴露 HttpClient 实例，可以传给其他工具（如 LZFileDownloader）
     */
    val client: HttpClient = HttpClient {
        // JSON 序列化
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        // 日志
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    
    /**
     * GET 请求
     * @param url 请求地址
     * @param parameters 查询参数
     * @return Result<T> 成功时返回解析后的数据，失败时返回错误
     */
    suspend inline fun <reified T> get(
        url: String,
        parameters: Map<String, Any?> = emptyMap()
    ): Result<T> {
        return try {
            val response = client.get(url) {
                parameters.forEach { (key, value) ->
                    value?.let { parameter(key, it) }
                }
            }
            Result.success(response.body<T>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * POST 请求
     * @param url 请求地址
     * @param body 请求体
     * @return Result<T> 成功时返回解析后的数据，失败时返回错误
     */
    suspend inline fun <reified T> post(
        url: String,
        body: Any? = null
    ): Result<T> {
        return try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                body?.let { setBody(it) }
            }
            Result.success(response.body<T>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 关闭 HttpClient，释放资源
     */
    override fun close() {
        client.close()
    }
}
