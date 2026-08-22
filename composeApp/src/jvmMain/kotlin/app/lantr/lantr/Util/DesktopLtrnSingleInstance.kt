package app.lantr.lantr.Util

import java.io.File
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

private const val LtrnSingleInstancePort = 48271

object DesktopLtrnSingleInstance {
    fun handOffToRunningInstance(args: Array<String>): Boolean {
        val filePaths = args.ltrnFilePaths()
        if (filePaths.isEmpty()) {
            return false
        }

        return runCatching {
            Socket(InetAddress.getLoopbackAddress(), LtrnSingleInstancePort).use { socket ->
                socket.getOutputStream().bufferedWriter().use { writer ->
                    filePaths.forEach { path ->
                        writer.appendLine(path)
                    }
                }
            }
        }.isSuccess
    }

    fun startListening() {
        thread(
            name = "Lantr-Ltrn-File-Handoff",
            isDaemon = true
        ) {
            runCatching {
                ServerSocket(LtrnSingleInstancePort, 1, InetAddress.getLoopbackAddress()).use { server ->
                    while (true) {
                        val socket = server.accept()
                        handleConnection(socket)
                    }
                }
            }.onFailure { throwable ->
                throwable.printStackTrace()
            }
        }
    }

    private fun handleConnection(socket: Socket) {
        socket.use {
            val paths = socket.getInputStream().bufferedReader()
                .readLines()
                .filter { path -> path.endsWith(".ltrn", ignoreCase = true) }

            if (paths.isEmpty()) {
                return
            }

            SwingUtilities.invokeLater {
                PresentationWindowState.openAll(loadLtrnSessionsFromPaths(paths))
            }
        }
    }
}

fun Array<String>.ltrnFilePaths(): List<String> {
    val resolved = linkedSetOf<String>()

    forEach { argument ->
        resolveLtrnFileArgument(argument)?.let { file ->
            resolved += file.absolutePath
        }
    }

    for (startIndex in indices) {
        val joinedArgument = buildString {
            for (currentIndex in startIndex..lastIndex) {
                if (isNotEmpty()) {
                    append(' ')
                }
                append(this@ltrnFilePaths[currentIndex])
            }
        }

        resolveLtrnFileArgument(joinedArgument)?.let { file ->
            resolved += file.absolutePath
        }
    }

    return resolved.toList()
}

private fun resolveLtrnFileArgument(argument: String): File? {
    val directFile = File(argument)
    val candidates = if (directFile.isAbsolute) {
        listOf(directFile)
    } else {
        listOf(
            directFile,
            File(System.getProperty("user.home"), argument)
        )
    }

    return candidates.firstOrNull { file ->
        file.exists() && file.isFile && file.extension.equals("ltrn", ignoreCase = true)
    }
}
