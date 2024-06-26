package com.undefined.runServer

import com.undefined.runServer.lib.DownloadResult
import com.undefined.runServer.lib.DownloadResultType
import com.undefined.runServer.lib.TaskLib
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.concurrent.CompletableFuture

abstract class RunServerTask: AbstractServer() {

    init {
        outputs.upToDateWhen { false }
    }

    private var mcVersion: String? = null

    private var serverFolder: String = "run"


    fun mcVersion(string: String) {mcVersion = string}


    override fun exec() {
        if (mcVersion == null) {
            logger.error("No minecraft version selected!")
            throw IllegalArgumentException("No minecraft version selected")
        }

        setup()

        createFolders()

        loadPlugin()

        logger.info("Downloading latest jar of type ${serverType.name.lowercase()} version $mcVersion...")
        downloadServerJar().thenAccept {
            if (it.downloadResultType == DownloadResultType.SUCCESS) {
                logger.info("Downloaded!")

                setClass(it.jarFile!!)

                super.exec()
            }

        }.join()
    }


    private fun downloadServerJar(): CompletableFuture<DownloadResult> = serverType.downloadJar(mcVersion!!, workingDir)

    private fun loadPlugin() {
        logger.info("Creating plugin...")
        val pluginTask = TaskLib.getPluginTask(project)
        val pluginFile = pluginTask.outputs.files.singleFile
        val inServerFile = File(pluginDir!!, pluginFile.name)
        logger.info("Coping plugins...")
        pluginFile.copyTo(inServerFile)
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