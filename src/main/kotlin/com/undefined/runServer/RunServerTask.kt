package com.undefined.runServer

import com.undefined.runServer.lib.DownloadLib
import com.undefined.runServer.lib.DownloadResult
import com.undefined.runServer.lib.DownloadResultType
import com.undefined.runServer.lib.TaskLib
import org.gradle.api.tasks.Internal
import java.io.File
import java.net.URI

abstract class RunServerTask: AbstractServer() {

    init {
        outputs.upToDateWhen { false }
    }

    private var mcVersion: String? = null
    private var allowedRam: String = "2G"

    private var noGui: Boolean = true
    @get:Internal
    private var downloads: MutableList<URI> = mutableListOf()

    fun mcVersion(string: String) { mcVersion = string }
    fun allowedRam(string: String) { allowedRam = string }

    fun noGui(boolean: Boolean) { noGui = boolean }
    fun downloads(vararg links: URI) { downloads.addAll(links) }
    fun downloads(vararg links: String) { links.forEach { downloads.add(URI(it)) } }

    override fun exec() {
        if (mcVersion == null) {
            logger.error("No minecraft version selected!")
            throw IllegalArgumentException("No minecraft version selected")
        }

        enableEula()
        setup()
        createFolders()
        loadPlugin()

        logger.info("Downloading latest jar of type ${serverType.name.lowercase()} version $mcVersion...")
        val download = downloadServerJar()

        if (download.downloadResultType == DownloadResultType.SUCCESS) {
            setClass(download.jarFile!!)
            if (noGui) args("--nogui")
            setJvmArgs(listOf("-Xmx$allowedRam"))

            downloads.forEach {
                DownloadLib.downloadFile(workingDir, it.path, File(it).name)
            }

            super.exec()
        } else {
            logger.error("Download failed. Makes sure your version does exists.")
        }
    }

    private fun downloadServerJar(): DownloadResult = serverType.downloadJar(mcVersion!!, workingDir)

    private fun loadPlugin() {
        logger.info("Creating plugin...")
        val pluginTask = TaskLib.getPluginTask(project)
        val pluginFile = pluginTask.outputs.files.singleFile
        val inServerFile = File(pluginDir!!, pluginFile.name)
        logger.info("Coping plugins...")
        pluginFile.copyTo(inServerFile, true)
        logger.info("Plugin creation finished")
    }

    private fun createFolders(){
        logger.info("Creating server folders...")
        if (!runDir!!.exists()) {
            runDir!!.mkdirs()
        }
        if (!pluginDir!!.exists()) {
            pluginDir!!.mkdirs()
        }
        logger.info("Created server folders!")
    }

    private fun enableEula() {
        val eulaFile = File(workingDir, "eula.txt")

        if (!eulaFile.exists()) {
            eulaFile.createNewFile()
            eulaFile.writeText("eula=true")
        }

    }
}