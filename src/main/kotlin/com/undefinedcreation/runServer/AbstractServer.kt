package com.undefinedcreation.runServer

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import java.io.File

abstract class AbstractServer: JavaExec() {

    @get:Internal
    protected var runDir: File? = null
    @get:Internal
    protected var pluginDir: File? = null

    @get:Internal
    protected var serverType: ServerType = ServerType.SPIGOT
    @get:Internal
    protected var mcVersion: String? = null

    @get:Internal
    protected var versionFolder: Boolean = false

    fun perVersionFolder(boolean: Boolean) { versionFolder = boolean }
    fun mcVersion(string: String) { mcVersion = string }
    fun serverFolder(string: String) { runDir = File(project.layout.buildDirectory.get().asFile, string) }
    fun serverType(serverType: ServerType) { if (serverType != ServerType.CUSTOM) this.serverType = serverType }

    protected fun setRunningDir(file: File) = file.also { runDir = it }
    protected fun setClass(file: File) = classpath(file.path)
    protected fun setJvmArgs(args: List<String>) = jvmArgs(args)

    protected fun setup() {
        standardInput = System.`in`
        if (runDir == null) {
            runDir = File(project.layout.projectDirectory.asFile, "run${if (versionFolder) "/$mcVersion" else ""}/${serverType.name}")
        }
        pluginDir = File(runDir, "plugins")
        workingDir(runDir!!.path)
    }

}