package com.undefinedcreation.runServer

import com.undefinedcreation.runServer.exception.CustomJarNotFoundException
import com.undefinedcreation.runServer.exception.VersionNotFoundException
import com.undefinedcreation.runServer.lib.links.DownloadResult
import com.undefinedcreation.runServer.lib.links.DownloadResultType
import com.undefinedcreation.runServer.lib.TaskLib
import com.undefinedcreation.runServer.lib.links.PluginLib
import org.gradle.internal.impldep.com.google.common.io.Files
import java.io.File
import java.net.URI

abstract class RunServerTask: AbstractServer() {

    init {
        outputs.upToDateWhen { false }
    }


    private var allowedRam: String = "2G"

    private var noGui: Boolean = true
    private var downloads: MutableList<String> = mutableListOf()
    private var acceptEula: Boolean = false

    private var customJarPath: String? = null
    private var replaceCustomJar: Boolean = false

    private var customJarName: String? = null


    fun allowedRam(amount: Int, ramAmount: RamAmount) { allowedRam = "$amount${ramAmount.flag}" }

    fun noGui(boolean: Boolean) { noGui = boolean }
    fun plugins(vararg links: String) { links.forEach { downloads.add(it) } }

    fun acceptMojangEula(boolean: Boolean) {acceptEula = boolean}

    fun customJar(path: String, alwaysReplace: Boolean = false) {
        serverType = ServerType.CUSTOM
        customJarPath = path
        replaceCustomJar = alwaysReplace
    }


    override fun exec() {
        if (mcVersion == null) {
            logger.error("No minecraft version selected!")
            throw IllegalArgumentException("No minecraft version selected")
        }

        checkJarVersion()
        setup()
        createFolders()
        setUpCustomJar()
        loadPlugin()

        var download: DownloadResult? = null

        if (serverType != ServerType.CUSTOM) {
            logger.info("Downloading latest jar of type ${serverType.name.lowercase()} version $mcVersion...")
            download = downloadServerJar()
        }


        if (download == null || download.resultType == DownloadResultType.SUCCESS) {
            setClass(download?.jarFile ?: File(workingDir, customJarName!!))
            if (noGui) args("--nogui")

            val jvmFlags = mutableListOf("-Xmx$allowedRam")
            if (serverType == ServerType.SPIGOT) jvmFlags.add("-DIReallyKnowWhatIAmDoingISwear")
            if (acceptEula) jvmFlags.add("-Dcom.mojang.eula.agree=true")
            setJvmArgs(jvmFlags)

            super.exec()
        } else {
            logger.error("Download failed. Error [${download.errorMessage}]")
        }
    }

    private fun checkJarVersion() {
        if (serverType == ServerType.CUSTOM) return
        serverType.versions().let {
            if (!it.contains(mcVersion)) {
                throw VersionNotFoundException(mcVersion!!, it)
            }
        }
    }

    private fun downloadServerJar(): DownloadResult? = serverType.downloadJar(mcVersion!!, workingDir)

    private fun setUpCustomJar() {
        if (serverType != ServerType.CUSTOM) return

        logger.info("Checking custom jar")

        val copyFile = File(customJarPath!!)
        if (!copyFile.exists()) {
            logger.warn("Didn't find [$customJarPath]. Default to jar in run folder")
            customJarName = workingDir.listFiles()?.filter { it.name.contains(".jar") }?.getOrNull(0)?.name ?: throw CustomJarNotFoundException()
            return
        }
        customJarName = copyFile.name

        val serverJarFile = File(workingDir, customJarName!!)

        if (!serverJarFile.exists() || replaceCustomJar) {
            serverJarFile.writeBytes(copyFile.readBytes())
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
            val pluginName = PluginLib.fileName(it, serverType);

            if (!pluginDir!!.listFiles()!!.map { file -> file.name }.contains(pluginName)) {
                logger.info("Downloading $pluginName")
                PluginLib.download(pluginDir!!, it, serverType)
            }
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