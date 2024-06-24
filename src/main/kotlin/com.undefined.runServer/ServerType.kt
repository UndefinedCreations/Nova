package com.undefined.runServer

import java.io.File
import java.util.concurrent.CompletableFuture


enum class ServerType(val URL_BUILDVERSION: String, val URL_DOWNLOAD: String) {
    SPIGOT("", ""),
    CRAFTBUKKIT("", ""),
    PAPERMC("https://api.papermc.io/v2/projects/paper/versions/", "https://api.papermc.io/v2/projects/paper/versions/mcVersion/builds/newestBuild/downloads/paper-mcVersion-newestBuild.jar"),
    PUFFERFISH("as", ""),
    PURPUR("", ""),
    BUNGEECORD("", ""),
    WATERFALL("", ""),
    VELOCITY("", ""),
    FOLIA("", "")
}

fun ServerType.downloadJar(mcVersion: String, folder: File): CompletableFuture<DownloadResult> =
    when(this) {
        ServerType.SPIGOT -> DownloadLib.downloadSpigot(folder, mcVersion)
        ServerType.CRAFTBUKKIT -> DownloadLib.downloadBukkit(folder, mcVersion)
        ServerType.PAPERMC -> DownloadLib.downloadPaper(folder, mcVersion)
        ServerType.PUFFERFISH -> DownloadLib.downloadPufferFish(folder, mcVersion)
        ServerType.PURPUR -> DownloadLib.downloadPurper(folder, mcVersion)
        ServerType.BUNGEECORD -> DownloadLib.downloadBungeecord(folder, mcVersion)
        ServerType.WATERFALL -> DownloadLib.downloadWaterfall(folder, mcVersion)
        ServerType.VELOCITY -> DownloadLib.downloadVelocity(folder, mcVersion)
        ServerType.FOLIA -> DownloadLib.downloadFolia(folder, mcVersion)
    }
