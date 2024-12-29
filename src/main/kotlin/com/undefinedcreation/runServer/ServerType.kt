package com.undefinedcreation.runServer

import com.undefinedcreation.runServer.lib.links.DownloadLib
import com.undefinedcreation.runServer.lib.links.DownloadResult
import com.undefinedcreation.runServer.lib.links.VersionLib
import java.io.File

enum class ServerType(val loaderName: String, val proxy: Boolean) {
    SPIGOT("spigot", false),
    PAPERMC("paper", false),
    PUFFERFISH("paper", false),
    PURPUR("purpur", false),
    BUNGEECORD("bungeecord", true),
    WATERFALL("waterfall", true),
    VELOCITY("velocity", true),
    FOLIA("folia", false),
    CUSTOM("CUSTOM", false);

    fun downloadJar(mcVersion: String, folder: File) =
        when(this) {
            SPIGOT -> DownloadLib.spigot(folder, mcVersion)
            PAPERMC -> DownloadLib.paper(folder, mcVersion)
            PUFFERFISH -> DownloadLib.pufferfish(folder, mcVersion)
            PURPUR -> DownloadLib.purper(folder, mcVersion)
            BUNGEECORD -> DownloadLib.bungeecord(folder)
            WATERFALL -> DownloadLib.waterfall(folder, mcVersion)
            VELOCITY -> DownloadLib.velocity(folder, mcVersion)
            FOLIA -> DownloadLib.folia(folder, mcVersion)
            CUSTOM -> null
        }


    fun versions(): List<String> =
        when(this) {
            SPIGOT -> VersionLib.spigot()
            PAPERMC -> VersionLib.paper()
            PUFFERFISH -> VersionLib.pufferfish()
            PURPUR -> VersionLib.purper()
            BUNGEECORD -> VersionLib.bungeecord()
            WATERFALL -> VersionLib.waterfall()
            VELOCITY -> VersionLib.velocity()
            FOLIA -> VersionLib.folia()
            CUSTOM -> listOf()
        }

}