package com.undefined.runServer

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URI

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
        serverFolderFile = File(project.buildFile.parentFile, serverFolder.getOrElse("run"))

        if (!serverFolderFile.exists()) serverFolderFile.mkdirs()

        val mcV = mcVersion.get()

        val latestBuild = serverType.get().getLatestBuild(mcV)

        serverType.get().download(serverFolderFile, latestBuild, mcV)

    }

}