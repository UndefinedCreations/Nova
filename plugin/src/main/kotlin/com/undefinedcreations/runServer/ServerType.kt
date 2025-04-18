package com.undefinedcreations.runServer

import com.undefinedcreations.runServer.lib.links.DownloadLib
import com.undefinedcreations.runServer.lib.links.VersionLib
import java.io.File

/**
 * Represents different types of Minecraft server platforms.
 *
 * @param loaderName Loader's name used in URL's for plugin downloading purposes.
 * @param proxy Whether the server is a proxy.
 * @since 1.0
 */
enum class ServerType(val loaderName: String, val proxy: Boolean) {

    /**
     * Spigot server type.
     */
    SPIGOT("spigot", false),

    /**
     * PaperMC server type, Spigot fork until Minecraft version `1.21.4`.
     */
    PAPERMC("paper", false),

    /**
     * Pufferfish server type, a fork of PaperMC.
     */
    PUFFERFISH("paper", false),

    /**
     * Purpur server type, a fork of Pufferfish.
     */
    PURPUR("purpur", false),

    /**
     * BungeeCord proxy server type.
     */
    BUNGEECORD("bungeecord", true),

    /**
     * Waterfall proxy server type, a fork of BungeeCord.
     */
    WATERFALL("waterfall", true),

    /**
     * Velocity proxy server type, a standalone proxy developed by Paper.
     */
    VELOCITY("velocity", true),

    /**
     * Folia server type, an experimental fork of PaperMC designed to add more multithreading.
     */
    FOLIA("folia", false),

    /**
     * Custom server type.
     */
    CUSTOM("CUSTOM", false);

    /**
     * Used to download a Jar from a `ServerType`.
     *
     * @param mcVersion The Minecraft server version.
     * @param directory The directory in which it will be saved in.
     */
    fun downloadJar(mcVersion: String, directory: File) =
        when(this) {
            SPIGOT -> DownloadLib.spigot(directory, mcVersion)
            PAPERMC -> DownloadLib.paper(directory, mcVersion)
            PUFFERFISH -> DownloadLib.pufferfish(directory, mcVersion)
            PURPUR -> DownloadLib.purper(directory, mcVersion)
            BUNGEECORD -> DownloadLib.bungeecord(directory)
            WATERFALL -> DownloadLib.waterfall(directory, mcVersion)
            VELOCITY -> DownloadLib.velocity(directory, mcVersion)
            FOLIA -> DownloadLib.folia(directory, mcVersion)
            CUSTOM -> null
        }

    /**
     * Used to get a list of all supported Minecraft server versions for this `ServerType`.
     */
    fun versions(): List<String> =
        when(this) {
            SPIGOT -> VersionLib.spigot()
            PAPERMC -> VersionLib.paper()
            PUFFERFISH -> VersionLib.pufferfish()
            PURPUR -> VersionLib.purpur()
            BUNGEECORD -> VersionLib.bungeecord()
            WATERFALL -> VersionLib.waterfall()
            VELOCITY -> VersionLib.velocity()
            FOLIA -> VersionLib.folia()
            CUSTOM -> listOf()
        }

}