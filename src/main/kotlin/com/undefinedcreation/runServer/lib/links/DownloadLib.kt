package com.undefinedcreation.runServer.lib.links

import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream
import java.net.URI

object DownloadLib {

    fun paper(folder: File, mcVersion: String) =
        downloadFromPaperMC(folder, mcVersion, "paper")

    fun waterfall(folder: File, mcVersion: String) =
        downloadFromPaperMC(folder, mcVersion, "waterfall")

    fun velocity(folder: File, mcVersion: String) =
        downloadFromPaperMC(folder, mcVersion, "velocity")

    fun folia(folder: File, mcVersion: String) =
        downloadFromPaperMC(folder, mcVersion, "folia")

    fun spigot(folder: File, mcVersion: String) =
        downloadFromGetUndefinedCreation(folder, mcVersion)

    fun bungeecord(folder: File): DownloadResult =
        downloadFile(folder, Repositories.BUNGEECORD_REPO, "Bungeecord.jar")

    fun purper(folder: File, mcVersion: String): DownloadResult =
        downloadFile(folder, "${Repositories.PURPER_REPO}/$mcVersion/latest/download", "Purper.jar")

    fun pufferfish(folder: File, mcVersion: String): DownloadResult {
        val mainUrl = URI("${Repositories.PUFFERFISH_REPO}/Pufferfish-$mcVersion/lastSuccessfulBuild")
        val buildUrl = URI("$mainUrl/api/json")

        val path = JsonParser.parseString(buildUrl.toURL().readText())
            .asJsonObject.getAsJsonArray("artifacts")
            .get(0).asJsonObject.get("relativePath").asString

        return downloadFile(folder, "$mainUrl/artifact/$path", "PufferFish.jar")
    }


    private fun downloadFromPaperMC(folder: File, mcVersion: String, projectName: String): DownloadResult {
        val url = URI("${Repositories.PAPERMC_REPO}/$projectName/versions/$mcVersion")
        val builds = JsonParser.parseString(url.toURL().readText())
            .asJsonObject.getAsJsonArray("builds")
        val latestBuild = builds.last().asInt

        return downloadFile(folder, "$url/builds/$latestBuild/downloads/$projectName-$mcVersion-$latestBuild.jar", "$projectName.jar")
    }

    private fun downloadFromGetUndefinedCreation(folder: File, mcVersion: String): DownloadResult =
        downloadFile(folder, "${Repositories.UNDEFINEDCREATION_REPO}/spigot-$mcVersion.jar", "spigot.jar")

    fun downloadFile(folder: File, downloadURL: String, name: String): DownloadResult =
        downloadFile(folder, URI(downloadURL), name)

    private fun downloadFile(folder: File, downloadURL: URI, name: String): DownloadResult {
        val file = File(folder, name)

        return if (file.exists()) {
            DownloadResult(DownloadResultType.SUCCESS, null, file)
        } else {
            try {
                downloadURL.toURL().openStream().use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                DownloadResult(DownloadResultType.SUCCESS, null, file)
            } catch (exception: Exception) {
                DownloadResult(DownloadResultType.FAILED, exception.message, null)
            }
        }
    }
}

data class DownloadResult(val resultType: DownloadResultType, val errorMessage: String?, val jarFile: File?)

enum class DownloadResultType {
    SUCCESS,
    FAILED
}