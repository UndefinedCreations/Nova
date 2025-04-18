package com.undefinedcreations.nova

import com.undefinedcreations.nova.exception.CustomJarNotFoundException
import com.undefinedcreations.nova.exception.VersionNotFoundException
import com.undefinedcreations.nova.lib.TaskLib
import com.undefinedcreations.nova.lib.links.DownloadResult
import com.undefinedcreations.nova.lib.links.DownloadResultType
import com.undefinedcreations.nova.lib.links.PluginLib
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * This is the main class for the runServer task
 *
 * @since 1.0.0
 */
abstract class RunServerTask : AbstractServer() {

    /**
     * On init with make sure the task will run every time.
     * I was very slow when writing this (mentally)
     */
    init {
        outputs.upToDateWhen { false }
    }

    private var allowedRam: String = "2G"
    private var noGui: Boolean = true
    private var downloads: MutableList<Pair<String, Boolean>> = mutableListOf()
    private var filePlugins: MutableList<Pair<File, Boolean>> = mutableListOf()
    private var acceptEula: Boolean = false
    private var customJarPath: String? = null
    private var replaceCustomJar: Boolean = false
    private var customJarName: String? = null
    private var inputTask: TaskProvider<*>? = null
    private var debug: Boolean = false

    /**
     * This is an option to select the input task
     *
     * @param task The new input task
     */
    fun inputTask(task: TaskProvider<*>) {
        inputTask = task
        setDependsOn(mutableListOf(task))
    }

    /**
     * This is an option to set the amount of ram the server is allowed to use.
     *
     * @param amount This amount
     * @param ramAmount What type of ram
     */
    fun allowedRam(amount: Int, ramAmount: RamAmount) { allowedRam = "$amount${ramAmount.flag}" }

    /**
     * This is an option is allowing some server jar to create a gui on start
     *
     * No gui will not have any effect on versions before 1.15.2
     *
     * @param boolean allow or disallow
     */
    fun noGui(boolean: Boolean) { noGui = boolean }

    /**
     * This option will throw out exceptions
     */
    fun debugMessage(debug: Boolean) { this.debug = debug }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from.
     */
    fun plugins(vararg links: String) { downloads.addAll(links.map { Pair(it, false) }) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from and able to set if it will overwrite the old file
     */
    fun plugins(links: List<Pair<String, Boolean>>) { downloads.addAll(links) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param links The page links to download from
     * @param overwrite If it should overwrite every time
     */
    fun plugins(links: List<String>, overwrite: Boolean) { downloads.addAll(links.map { Pair(it, overwrite) }) }

    /**
     * This is an option to download plugin from an external website.
     *
     * @param url The url to the page to down from
     * @param overwrite If it should overwrite the file everytime
     */
    fun plugin(url: String, overwrite: Boolean = false) { downloads.add(Pair(url, overwrite)) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy
     */
    fun filePlugins(vararg files: File) { filePlugins.addAll(files.map { Pair(it, false) }) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy
     * @param overwrite If it should overwrite the files
     */
    fun filePlugins(files: List<File>, overwrite: Boolean) { filePlugins.addAll(files.map { Pair(it, overwrite) }) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param files A list of plugins to copy and if it will overwrite the plugin file
     */
    fun filePlugins(files: List<Pair<File, Boolean>>) { filePlugins.addAll(files) }

    /**
     * This is an option to copy a plugin from your disk
     *
     * @param file The plugin to copy
     * @param overwrite If it should overwrite the plugin file
     */
    fun filePlugin(file: File, overwrite: Boolean = false) { filePlugins.add(Pair(file, overwrite)) }

    /**
     * This will accept the mojang eula for you when start
     */
    fun acceptMojangEula() {acceptEula = true}

    /**
     * This option will allow you to run a custom jar
     *
     * @param path This is the path to your custom jar
     * @param alwaysReplace If it should always replace you custom jar on start up
     */
    fun customJar(path: String, alwaysReplace: Boolean = false) {
        serverType = ServerType.CUSTOM
        customJarPath = path
        replaceCustomJar = alwaysReplace
        dependOnTasks()
    }

    /**
     * This will run when the task is called
     */
    override fun exec() {
        if (minecraftVersion == null) {
            val echoVersion = getEchoMinecraftVersion()
            if (echoVersion == null) {
                logger.error("No minecraft version selected!")
                throw IllegalArgumentException("No minecraft version selected")
            } else {
                minecraftVersion = echoVersion
            }
        }

        checkServerVersion()
        setup()
        createFolders()
        setUpCustomJar()
        loadPlugin()

        var download: DownloadResult? = null

        if (serverType != ServerType.CUSTOM) {
            logger.info("Downloading latest jar of type ${serverType.name.lowercase()} version $minecraftVersion...")
            download = downloadServerJar()
        }

        if (download == null || download.resultType == DownloadResultType.SUCCESS) {
            setClass(download?.jarFile ?: File(workingDir, customJarName!!))

            val slitVersion = minecraftVersion!!.split(".")
            val mainVersion = slitVersion[1].toInt()
            val subVersion = slitVersion[2].toInt()

            if (noGui) {
                if ((mainVersion == 15 && subVersion == 2) || mainVersion > 15) {
                    args("--nogui")
                }
            }

            val jvmFlags = mutableListOf("-Xmx$allowedRam")
            if (serverType == ServerType.SPIGOT) jvmFlags.add("-DIReallyKnowWhatIAmDoingISwear")
            if (acceptEula) jvmFlags.add("-Dcom.mojang.eula.agree=true")
            setJvmArgs(jvmFlags)

            super.exec()
        } else {
            logger.error("Download failed. Error [${download.errorMessage}]")
        }
    }

    /**
     * This is checking the server version and if it exists.
     */
    private fun checkServerVersion() {
        if (serverType == ServerType.CUSTOM) return
        serverType.versions().let {
            if (minecraftVersion !in it) throw VersionNotFoundException(minecraftVersion!!, it)
        }
    }

    /**
     * This is downloading the server jar
     *
     * @return The download result if it was success or not
     */
    private fun downloadServerJar(): DownloadResult? = serverType.downloadJar(minecraftVersion!!, workingDir)

    /**
     * This will be setting up the properties if a custom jar is selected
     */
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

    /**
     * This method will load your plugin and download the rest from the websites or copy them
     */
    private fun loadPlugin() {
        logger.info("Creating plugin...")
        val pluginFile = TaskLib.findPluginJar(project, inputTask, this)
        val inServerFile = File(pluginDir!!, pluginFile.name)
        logger.info("Coping plugins...")
        pluginFile.copyTo(inServerFile, true)
        logger.info("Plugin creation finished")

        downloads.forEach {
            val pluginName = PluginLib.fileName(it.first, serverType);

            if (!pluginDir!!.listFiles()!!.map { file -> file.name }.contains(pluginName) || it.second) {
                logger.info("Downloading $pluginName")
                PluginLib.download(pluginDir!!, it.first, serverType)
            }
        }

        copyFilePlugins()
    }

    /**
     * This will copy the local plugins into the plugin folder
     */
    private fun copyFilePlugins() {
        filePlugins.forEach {
            val pluginFile = File(pluginDir!!, it.first.name)
            it.first.copyTo(pluginFile, it.second)
        }
    }

    /**
     * This will be creating all the folder for the server
     */
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

    private fun getEchoMinecraftVersion(): String? {
        try {
            val clazz = Class.forName("com.undefinedcreations.echo.EchoPlugin")
            val compationInp = clazz.getDeclaredField("Companion").run {
                this.isAccessible = true
                this.get(null)
            }
            val companionClazz = Class.forName("com.undefinedcreations.echo.EchoPlugin\$Companion")
            return companionClazz.getDeclaredMethod("getMinecraftVersion").run {
                this.isAccessible = true
                this.invoke(compationInp)
            } as String
        } catch (e: Exception) {
            if (debug) throw e
            return null
        }
    }

}