package com.undefinedcreations.runServer

import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import java.io.File

/**
 * This class is extending `JavaExec`. Witch is a task to run jar files
 *
 * @since 1.0.0
 */
abstract class AbstractServer : JavaExec() {

    @get:Internal
    protected var runDir: File? = null
    @get:Internal
    protected var pluginDir: File? = null

    @get:Internal
    protected var serverType: ServerType = ServerType.SPIGOT
    @get:Internal
    protected var minecraftVersion: String? = null

    @get:Internal
    protected var versionFolder: Boolean = false

    /**
     * This is an option to change if there should be a folder for every version
     *
     * @param boolean If a folder should be created per version
     */
    fun perVersionFolder(boolean: Boolean) { versionFolder = boolean }

    /**
     * The minecraft version the server should run
     *
     * @param minecraftVersion The minecraft version
     */
    fun minecraftVersion(minecraftVersion: String) { this.minecraftVersion = minecraftVersion }

    /**
     * This option allowed you to set the folder where the server is running
     *
     * @param folder This gives you the folder data and returns the file to place the server folder
     */
    fun serverFolder(folder: (FolderData).() -> File) { runDir = folder(FolderData(minecraftVersion, serverType, project.layout.projectDirectory.asFile)) }

    /**
     * This option allowed you to set the folder where the server is running
     *
     * @param folder This gives you the folder data and returns the file to place the server folder
     */
    fun serverFolderName(folder: (FolderData).() -> String) { runDir = File(project.layout.projectDirectory.asFile, folder(FolderData(minecraftVersion, serverType, project.layout.projectDirectory.asFile)))}

    /**
     * This option allows you to set what type of server you will be running.
     *
     * @param serverType The server type
     */
    fun serverType(serverType: ServerType) { if (serverType != ServerType.CUSTOM) this.serverType = serverType }

    protected fun setRunningDir(file: File) = file.also { runDir = it }
    protected fun setClass(file: File) = classpath(file.path)
    protected fun setJvmArgs(args: List<String>) = jvmArgs(args)

    /**
     * This sets up the `runDir` of JavaExec
     */
    protected fun setup() {
        standardInput = System.`in`
        if (runDir == null) {
            runDir = File(project.layout.projectDirectory.asFile, "run${if (versionFolder) "/$minecraftVersion" else ""}/${serverType.name}")
        }
        pluginDir = File(runDir, "plugins")
        workingDir(runDir!!.path)
    }

}

/**
 * The folder data that is giving when selecting the folder to make the server go to.
 *
 * @param minecraftVersion The minecraft version the server is running
 * @param serverType The server type that its running
 * @param buildFolder The build folder of your project
 * @since 1.0.0
 */
class FolderData(val minecraftVersion: String?, val serverType: ServerType, val buildFolder: File)