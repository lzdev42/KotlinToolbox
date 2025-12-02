package lzdev42.kotlintoolbox

import java.io.File
import java.util.zip.ZipFile

/**
 * JVM 平台的解压缩实现
 * 使用 java.util.zip.ZipFile（Java 标准库）
 */
actual fun unzipPlatform(zipFilePath: String): FileResult<String> {
    return try {
        val zipFile = File(zipFilePath)
        if (!zipFile.exists()) {
            return FileResult.Error(
                "ZIP 文件不存在: $zipFilePath",
                ErrorCode.FILE_NOT_FOUND
            )
        }
        
        if (!zipFile.canRead()) {
            return FileResult.Error(
                "无法读取 ZIP 文件: $zipFilePath",
                ErrorCode.PERMISSION_DENIED
            )
        }
        
        // 解压到同目录下，去掉 .zip 扩展名
        val outputDir = File(zipFile.parent, zipFile.nameWithoutExtension)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        
        ZipFile(zipFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val outputFile = File(outputDir, entry.name)
                
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    // 确保父目录存在
                    outputFile.parentFile?.mkdirs()
                    
                    // 解压文件
                    zip.getInputStream(entry).use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
        
        FileResult.Success(outputDir.absolutePath)
    } catch (e: Exception) {
        FileResult.Error(
            "解压失败: ${e.message}",
            ErrorCode.IO_ERROR
        )
    }
}
