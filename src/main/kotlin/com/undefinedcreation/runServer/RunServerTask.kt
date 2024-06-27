package com.undefinedcreation.runServer

import com.undefinedcreation.runServer.lib.DownloadLib
import com.undefinedcreation.runServer.lib.DownloadResult
import com.undefinedcreation.runServer.lib.DownloadResultType
import com.undefinedcreation.runServer.lib.TaskLib
import org.gradle.api.tasks.Internal
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.regex.Pattern

abstract class RunServerTask: AbstractServer() {

    init {
        outputs.upToDateWhen { false }
    }

    private var mcVersion: String? = null
    private var allowedRam: String = "2G"

    private var noGui: Boolean = true
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

            super.exec()
        } else {
            logger.error("Download failed. Makes sure your version does exists.")
        }
    }

    private fun downloadServerJar(): DownloadResult = serverType.downloadJar(mcVersion!!, workingDir)

    private fun enableEula() {

        val eulaFile = File(workingDir, "eula.txt")

        if (!eulaFile.exists()) {
            eulaFile.createNewFile()
            eulaFile.writeText("eula=true")
        }

    }

    private fun loadPlugin() {
        logger.info("Creating plugin...")
        val pluginTask = TaskLib.getPluginTask(project)
        val pluginFile = pluginTask.outputs.files.singleFile
        val inServerFile = File(pluginDir!!, pluginFile.name)
        logger.info("Coping plugins...")
        pluginFile.copyTo(inServerFile, true)
        logger.info("Plugin creation finished")

        downloads.forEach {
            println(URL("https://www.spigotmc.org/resources/undefinedcombat.117598/download?version=547003").file)
            //DownloadLib.downloadFile(workingDir, it.path, URL(it.path).file)
        }
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


}