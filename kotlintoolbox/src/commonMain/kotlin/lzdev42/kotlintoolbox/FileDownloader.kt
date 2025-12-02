package lzdev42.kotlintoolbox

import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.buffered
import kotlinx.io.files.SystemPathSeparator

sealed class DownloadStatus {
    data object Idle : DownloadStatus()
    data object Pending : DownloadStatus()
    data class Downloading(
        val progress: Float,
        val bytesDownloaded: Long,
        val totalBytes: Long?
    ) : DownloadStatus()
    data class Completed(val path: String) : DownloadStatus()
    data class Error(
        val message: String,
        val code: ErrorCode = ErrorCode.UNKNOWN,
        val cause: Throwable? = null
    ) : DownloadStatus()
}

/**
 * 文件下载器
 * 基于 Ktor 实现的流式下载，支持进度跟踪
 */
class LZFileDownloader(private val client: HttpClient) {
    /**
     * 下载文件
     * @param url 下载地址
     * @param destinationPath 保存路径
     * @return Flow<DownloadStatus> 下载状态流
     */
    fun download(url: String, destinationPath: String): Flow<DownloadStatus> = flow {
        emit(DownloadStatus.Pending)

        try {
            client.prepareGet(url).execute { response ->
                if (!response.status.isSuccess()) {
                    emit(DownloadStatus.Error(
                        "下载失败，HTTP 状态: ${response.status}",
                        ErrorCode.IO_ERROR
                    ))
                    return@execute
                }

                val totalBytes = response.contentLength()
                val channel = response.bodyAsChannel()
                val path = Path(destinationPath)
                val fileSystem = SystemFileSystem

                // Ensure parent directory exists
                path.parent?.let { parent ->
                    if (!fileSystem.exists(parent)) {
                        fileSystem.createDirectories(parent)
                    }
                }

                fileSystem.sink(path).buffered().use { sink ->
                    var bytesDownloaded = 0L

                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(8 * 1024)
                        if (!packet.exhausted()) {
                            val packetSize = packet.remaining
                            packet.transferTo(sink)
                            bytesDownloaded += packetSize
                        }
                        
                        val progress = if (totalBytes != null && totalBytes > 0) {
                            (bytesDownloaded.toFloat() / totalBytes).coerceIn(0f, 1f)
                        } else {
                            0f
                        }

                        emit(DownloadStatus.Downloading(progress, bytesDownloaded, totalBytes))

                        if (channel.isClosedForRead && channel.availableForRead == 0) break
                    }
                }
                
                emit(DownloadStatus.Completed(destinationPath))
            }
        } catch (e: Exception) {
            emit(DownloadStatus.Error(
                "下载失败: ${e.message}",
                ErrorCode.IO_ERROR,
                e
            ))
        }
    }
}
