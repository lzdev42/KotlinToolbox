package lzdev42.kotlintoolbox

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class LZHttpClientTest {
    
    @Test
    fun testHttpClientCreation() = runTest {
        val http = LZHttpClient()
        
        // 验证 client 属性可以访问
        assertTrue(http.client != null, "HttpClient should be initialized")
        
        // 清理
        http.close()
    }
    
    @Test
    fun testHttpClientClose() = runTest {
        val http = LZHttpClient()
        
        // 测试关闭不会抛出异常
        try {
            http.close()
            assertTrue(true, "Close should execute without exception")
        } catch (e: Exception) {
            assertTrue(false, "Close should not throw exception: ${e.message}")
        }
    }
}
