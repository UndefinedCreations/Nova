package com.undefined.runServer

import com.undefined.runServer.lib.DownloadResult
import com.undefined.runServer.lib.DownloadResultType
import com.undefined.runServer.lib.TaskLib
import java.io.File

abstract class RunServerTask: AbstractServer() {

    init {
        outputs.upToDateWhen { false }
    }

    private var mcVersion: String? = null

    private var allowedRam: String = "2G"

    private var port: Int = 25565

    fun mcVersion(string: String) {mcVersion = string}
    fun allowedRam(string: String) {allowedRam = string}
    fun port(port: Int) {this.port = port}


    override fun exec() {
        if (mcVersion == null) {
            logger.error("No minecraft version selected!")
            throw IllegalArgumentException("No minecraft version selected")
        }

        setup()
        createFolders()
        loadPlugin()

        logger.info("Downloading latest jar of type ${serverType.name.lowercase()} version $mcVersion...")
        val down = downloadServerJar()

        if (down.downloadResultType == DownloadResultType.SUCCESS) {

            setClass(down.jarFile!!)
            setJvmArgs(listOf("--nogui", "-Xmx$allowedRam"))
            super.exec()

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
}