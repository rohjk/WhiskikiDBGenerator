import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import model.Whisky
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class ImageDownloader {

    private val defaultPath = System.getProperty("user.home") + "/Desktop"
    private val brewPath = "/opt/homebrew/bin"

    private val originDirPath = defaultPath + "/" + DIR_ORIGIN
    private val preRemoveBgDirPath = defaultPath + "/" + DIR_PRE_REMOVE_GB
    private val resizeDirPath = defaultPath + "/" + DIR_RESIZE
    private val removeBgDirPath = defaultPath + "/" + DIR_REMOVE_GB
    private val imagesDirPath = defaultPath + "/" + DIR_IMAGES

    suspend fun downloadImages(whiskies: List<Whisky>): String = withContext(Dispatchers.IO) {
        try {
            initDirectories()
            whiskies.map {
                async { downloadOriginImage(id = it.id, originImageUrl = it.originImageUrl) }
            }.awaitAll()
            preRemoveBackGround()
            resize()
            removeBackGround()
            convertWebp()
            cleanDirectories()
            "완료"
        } catch (e: Exception) {
            println("download Error : $e")
            e.toString()
        }
    }

    private fun initDirectories() {
        listOf(originDirPath, preRemoveBgDirPath, resizeDirPath, removeBgDirPath, imagesDirPath)
            .forEach {
                File(it).apply {
                    deleteRecursively()
                    mkdir()
                }
            }
    }

    private fun cleanDirectories() {
        listOf(originDirPath, preRemoveBgDirPath, resizeDirPath, removeBgDirPath)
            .forEach {
                File(it).deleteRecursively()
            }
    }

    private fun downloadOriginImage(id: String, originImageUrl: String) {
        val imageUrl = originImageUrl.replace(" ", "%20")
        val url = URL(imageUrl)
        val extention = imageUrl.substringAfterLast(".").substringBefore("?")
        val fileName = "${id}.$extention"
        url.openStream().use { Files.copy(it, Paths.get("$originDirPath/$fileName")) }
    }

    private fun preRemoveBackGround() {
        val process =
            ProcessBuilder().command("$brewPath/rembg", "p", originDirPath, preRemoveBgDirPath).start()
        process.waitFor()
    }

    private suspend fun resize() {
        coroutineScope {
            File(preRemoveBgDirPath).listFiles()?.map { file ->
                val resizePath = resizeDirPath + "/" + file.nameWithoutExtension+".jpg"
                async {
                    resize(
                        source = file.absolutePath,
                        output = resizePath
                    )
                }
            }?.awaitAll()
        }
    }

    private fun resize(
        source: String,
        output: String
    ) {
        val process =
            ProcessBuilder().command("$brewPath/convert", "-trim", source, "-resize", "530x530", "-gravity", "center", "-extent", "600x600", output).start()
        process.waitFor()
    }

    private fun removeBackGround() {
        val process =
            ProcessBuilder().command("$brewPath/rembg", "p", resizeDirPath, removeBgDirPath).start()
        process.waitFor()
    }

    private suspend fun convertWebp() {
        coroutineScope {
            File(removeBgDirPath).listFiles()?.map { file ->
                async {
                    convertWebp(
                        source = file.absolutePath,
                        output = "$imagesDirPath/${file.nameWithoutExtension}.webp"
                    )
                }
            }?.awaitAll()
        }
    }

    private fun convertWebp(source: String, output: String) {
        val process = ProcessBuilder().command("$brewPath/cwebp", source, "-o", output).start()
        process.waitFor()
    }

    companion object {
        private const val DIR_ORIGIN = "origin"
        private const val DIR_RESIZE = "resize"
        private const val DIR_PRE_REMOVE_GB = "preRemoveBg"
        private const val DIR_REMOVE_GB = "removeBg"
        private const val DIR_IMAGES = "images"
    }

}