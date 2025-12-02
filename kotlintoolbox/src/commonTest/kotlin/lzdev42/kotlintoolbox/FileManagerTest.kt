package lzdev42.kotlintoolbox

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.buffered
import kotlinx.io.writeString
import kotlinx.io.readString
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class FileManagerTest {
    private val fileManager = LZFileManager()
    private val fileSystem = SystemFileSystem
    
    @Test
    fun testListFiles() = runTest {
        // 创建测试目录
        val testDir = Path("build/test-files")
        if (fileSystem.exists(testDir)) {
            deleteRecursively(testDir)
        }
        fileSystem.createDirectories(testDir)
        
        // 创建测试文件
        val file1 = Path(testDir, "file1.txt")
        val file2 = Path(testDir, "file2.txt")
        fileSystem.sink(file1).buffered().use { it.writeString("test1") }
        fileSystem.sink(file2).buffered().use { it.writeString("test2") }
        
        // 测试列出文件
        val result = fileManager.listFiles(testDir.toString())
        assertTrue(result.isSuccess(), "列出文件应该成功")
        
        val files = result.getOrNull()!!
        assertEquals(2, files.size)
        assertTrue(files.containsKey("file1.txt"))
        assertTrue(files.containsKey("file2.txt"))
        
        // 清理
        deleteRecursively(testDir)
    }
    
    @Test
    fun testListFilesNotExist() = runTest {
        val result = fileManager.listFiles("build/nonexistent-dir")
        assertTrue(result.isError(), "目录不存在应该返回错误")
        assertEquals(ErrorCode.FILE_NOT_FOUND, (result as FileResult.Error).code)
    }
    
    @Test
    fun testDeleteFile() = runTest {
        // 创建测试文件
        val testFile = Path("build/test-delete.txt")
        fileSystem.sink(testFile).buffered().use { it.writeString("test") }
        
        // 测试删除
        assertTrue(fileSystem.exists(testFile))
        val result = fileManager.deleteFile(testFile.toString())
        assertTrue(result.isSuccess(), "删除应该成功")
        assertFalse(fileSystem.exists(testFile))
    }
    
    @Test
    fun testDeleteFileNotExist() = runTest {
        val result = fileManager.deleteFile("build/nonexistent.txt")
        assertTrue(result.isError(), "删除不存在的文件应该返回错误")
        assertEquals(ErrorCode.FILE_NOT_FOUND, (result as FileResult.Error).code)
    }
    
    @Test
    fun testMoveFile() = runTest {
        // 创建测试文件
        val sourceFile = Path("build/test-move-source.txt")
        val destFile = Path("build/test-move-dest.txt")
        
        // 清理旧文件
        if (fileSystem.exists(destFile)) {
            fileSystem.delete(destFile)
        }
        
        fileSystem.sink(sourceFile).buffered().use { it.writeString("test content") }
        
        // 测试移动
        val result = fileManager.moveFile(sourceFile.toString(), destFile.toString())
        assertTrue(result.isSuccess(), "移动应该成功")
        assertEquals(destFile.toString(), result.getOrNull())
        
        assertFalse(fileSystem.exists(sourceFile))
        assertTrue(fileSystem.exists(destFile))
        
        // 验证内容
        val content = fileSystem.source(destFile).buffered().use { it.readString() }
        assertEquals("test content", content)
        
        // 清理
        fileSystem.delete(destFile)
    }
    
    @Test
    fun testCopyFile() = runTest {
        // 创建测试文件
        val sourceFile = Path("build/test-copy-source.txt")
        val destFile = Path("build/test-copy-dest.txt")
        
        // 清理旧文件
        if (fileSystem.exists(destFile)) {
            fileSystem.delete(destFile)
        }
        
        fileSystem.sink(sourceFile).buffered().use { it.writeString("copy test") }
        
        // 测试复制
        val result = fileManager.copyFile(sourceFile.toString(), destFile.toString())
        assertTrue(result.isSuccess(), "复制应该成功")
        assertEquals(destFile.toString(), result.getOrNull())
        
        assertTrue(fileSystem.exists(sourceFile))
        assertTrue(fileSystem.exists(destFile))
        
        // 验证内容
        val content = fileSystem.source(destFile).buffered().use { it.readString() }
        assertEquals("copy test", content)
        
        // 清理
        fileSystem.delete(sourceFile)
        fileSystem.delete(destFile)
    }
    
    @Test
    fun testUnzipNotExist() = runTest {
        val result = fileManager.unzip("build/nonexistent.zip")
        assertTrue(result.isError(), "解压不存在的文件应该返回错误")
        assertEquals(ErrorCode.FILE_NOT_FOUND, (result as FileResult.Error).code)
    }
    
    // Helper function for recursive delete
    private fun deleteRecursively(path: Path) {
        if (fileSystem.exists(path)) {
            val metadata = fileSystem.metadataOrNull(path)
            if (metadata?.isDirectory == true) {
                fileSystem.list(path).forEach { child ->
                    deleteRecursively(child)
                }
            }
            fileSystem.delete(path)
        }
    }
}
