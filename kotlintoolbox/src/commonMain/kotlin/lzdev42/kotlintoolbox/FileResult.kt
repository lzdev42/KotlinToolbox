package lzdev42.kotlintoolbox

/**
 * 通用的 Result 类型，类似 Rust 的 Result<T, E>
 * 用于优雅地处理成功和失败情况
 */
sealed class FileResult<out T> {
    /**
     * 操作成功
     * @param data 成功时返回的数据
     */
    data class Success<T>(val data: T) : FileResult<T>()
    
    /**
     * 操作失败
     * @param message 错误信息
     * @param code 错误代码（可选）
     */
    data class Error(
        val message: String,
        val code: ErrorCode = ErrorCode.UNKNOWN
    ) : FileResult<Nothing>()
    
    /**
     * 判断是否成功
     */
    fun isSuccess(): Boolean = this is Success
    
    /**
     * 判断是否失败
     */
    fun isError(): Boolean = this is Error
    
    /**
     * 获取成功的数据，如果失败则返回 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
    
    /**
     * 获取错误信息，如果成功则返回 null
     */
    fun errorOrNull(): String? = when (this) {
        is Success -> null
        is Error -> message
    }
}

/**
 * 错误代码枚举
 */
enum class ErrorCode {
    /** 文件或目录不存在 */
    FILE_NOT_FOUND,
    
    /** 权限不足 */
    PERMISSION_DENIED,
    
    /** 平台不支持该操作 */
    UNSUPPORTED_PLATFORM,
    
    /** I/O 错误 */
    IO_ERROR,
    
    /** 未知错误 */
    UNKNOWN
}
