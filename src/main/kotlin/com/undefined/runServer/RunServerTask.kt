package com.undefined.runServer

import com.undefined.runServer.lib.TaskLib
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class RunServerTask: DefaultTask() {

    init {
        outputs.upToDateWhen { false }
    }

    @get:Input
    abstract val serverType: Property<ServerType>

    @get:Input
    abstract val mcVersion: Property<String>

    @get:Input
    @get:Optional
    abstract val serverFolder: Property<String>

    private lateinit var serverFolderFile: File

    @TaskAction
    fun execute() {
        serverFolderFile = File(project.buildFile.parentFile, "${serverFolder.getOrElse("run")}/${serverType.get().name}")
        val pluginFolder = File(serverFolderFile, "plugins")

        if (!serverFolderFile.exists()) {
            serverFolderFile.mkdirs()
            pluginFolder.mkdirs()
        }

        val mcV = mcVersion.get()

        val futures = serverType.get().downloadJar(mcV, serverFolderFile)
            .thenAccept {



            }
        futures.join()

        val pluginTask = TaskLib.getPluginTask(project)
        val pluginFile = pluginTask.outputs.files.singleFile
        val inServerFile = File(pluginFolder, pluginFile.name)
        pluginFile.copyTo(inServerFile)



    }




}