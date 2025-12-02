package lzdev42.kotlintoolbox

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.buffered

/**
 * 简单易用的文件管理工具
 * 基于 kotlinx-io 实现跨平台文件操作
 * 
 * 所有方法都是 suspend 函数，应在协程中调用
 * 所有方法都返回 FileResult，包含成功/失败状态和详细信息
 */
class LZFileManager {
    private val fileSystem = SystemFileSystem
    
    /**
     * 列出指定路径下的所有文件（不包含子目录）
     * @param directoryPath 目录路径
     * @return FileResult<Map<文件名, 完整路径>>
     */
    suspend fun listFiles(directoryPath: String): FileResult<Map<String, String>> {
        return try {
            val path = Path(directoryPath)
            if (!fileSystem.exists(path)) {
                return FileResult.Error(
                    "目录不存在: $directoryPath",
                    ErrorCode.FILE_NOT_FOUND
                )
            }
            
            val metadata = fileSystem.metadataOrNull(path)
            if (metadata?.isDirectory != true) {
                return FileResult.Error(
                    "路径不是目录: $directoryPath",
                    ErrorCode.IO_ERROR
                )
            }
            
            val files = mutableMapOf<String, String>()
            fileSystem.list(path).forEach { childPath ->
                val childMetadata = fileSystem.metadataOrNull(childPath)
                // 只包含文件，不包含目录
                if (childMetadata != null && !childMetadata.isDirectory) {
                    files[childPath.name] = childPath.toString()
                }
            }
            FileResult.Success(files)
        } catch (e: Exception) {
            FileResult.Error(
                "列出文件失败: ${e.message}",
                ErrorCode.IO_ERROR
            )
        }
    }
    
    /**
     * 删除指定文件
     * @param filePath 文件路径
     * @return FileResult<Unit>
     */
    suspend fun deleteFile(filePath: String): FileResult<Unit> {
        return try {
            val path = Path(filePath)
            if (!fileSystem.exists(path)) {
                return FileResult.Error(
                    "文件不存在: $filePath",
                    ErrorCode.FILE_NOT_FOUND
                )
            }
            
            fileSystem.delete(path)
            FileResult.Success(Unit)
        } catch (e: Exception) {
            FileResult.Error(
                "删除文件失败: ${e.message}",
                ErrorCode.IO_ERROR
            )
        }
    }
    
    /**
     * 移动文件
     * @param sourcePath 源文件路径
     * @param destinationPath 目标文件路径
     * @return FileResult<String> 成功时返回目标路径
     */
    suspend fun moveFile(sourcePath: String, destinationPath: String): FileResult<String> {
        return try {
            val source = Path(sourcePath)
            val destination = Path(destinationPath)
            
            if (!fileSystem.exists(source)) {
                return FileResult.Error(
                    "源文件不存在: $sourcePath",
                    ErrorCode.FILE_NOT_FOUND
                )
            }
            
            // 确保目标目录存在
            destination.parent?.let { parent ->
                if (!fileSystem.exists(parent)) {
                    fileSystem.createDirectories(parent)
                }
            }
            
            fileSystem.atomicMove(source, destination)
            FileResult.Success(destinationPath)
        } catch (e: Exception) {
            FileResult.Error(
                "移动文件失败: ${e.message}",
                ErrorCode.IO_ERROR
            )
        }
    }
    
    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param destinationPath 目标文件路径
     * @return FileResult<String> 成功时返回目标路径
     */
    suspend fun copyFile(sourcePath: String, destinationPath: String): FileResult<String> {
        return try {
            val source = Path(sourcePath)
            val destination = Path(destinationPath)
            
            if (!fileSystem.exists(source)) {
                return FileResult.Error(
                    "源文件不存在: $sourcePath",
                    ErrorCode.FILE_NOT_FOUND
                )
            }
            
            // 确保目标目录存在
            destination.parent?.let { parent ->
                if (!fileSystem.exists(parent)) {
                    fileSystem.createDirectories(parent)
                }
            }
            
            // 读取源文件并写入目标文件
            fileSystem.source(source).buffered().use { sourceBuffer ->
                fileSystem.sink(destination).buffered().use { destinationBuffer ->
                    sourceBuffer.transferTo(destinationBuffer)
                }
            }
            FileResult.Success(destinationPath)
        } catch (e: Exception) {
            FileResult.Error(
                "复制文件失败: ${e.message}",
                ErrorCode.IO_ERROR
            )
        }
    }
    
    /**
     * 解压缩 ZIP 文件到所在目录
     * @param zipFilePath zip 文件路径
     * @return FileResult<String> 成功时返回解压后的目录路径
     */
    suspend fun unzip(zipFilePath: String): FileResult<String> {
        return unzipPlatform(zipFilePath)
    }
}
