package com.undefined.runServer.lib

import com.google.gson.JsonParser
import java.io.File
import java.io.FileOutputStream
import java.net.URI

object DownloadLib {

    private const val PAPERMC_REPO = "https://api.papermc.io/v2/projects"
    private const val GETBUKKIT_REPO = "https://download.getbukkit.org/"
    private const val BUNGEECORD_REPO = "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar"
    private const val PURPER_REPO = "https://api.purpurmc.org/v2/purpur"
    private const val PUFFERFISH_REPO = "https://ci.pufferfish.host/job"

    private fun downloadFromPaperMC(folder: File, mcVersion: String, stringProjectName: String): DownloadResult {
        val url = URI("$PAPERMC_REPO/$stringProjectName/versions/$mcVersion")
        val arrayBuildJson = JsonParser.parseString(url.toURL().readText()).asJsonObject.getAsJsonArray("builds")
        val latestBuild = arrayBuildJson.get(arrayBuildJson.size() - 1).asInt

        return downloadFile(folder, "$url/builds/$latestBuild/downloads/$stringProjectName-$mcVersion-$latestBuild.jar", "server.jar")
    }
    private fun downloadFromGetBukkit(folder: File, mcVersion: String, stringProjectName: String): DownloadResult = downloadFile(folder, "$GETBUKKIT_REPO/$stringProjectName/$stringProjectName-$mcVersion.jar", "server.jar")

    fun downloadPaper(folder: File, mcVersion: String) = downloadFromPaperMC(folder, mcVersion, "paper")
    fun downloadWaterfall(folder: File, mcVersion: String) = downloadFromPaperMC(folder, mcVersion, "waterfall")
    fun downloadVelocity(folder: File, mcVersion: String) = downloadFromPaperMC(folder, mcVersion, "velocity")
    fun downloadFolia(folder: File, mcVersion: String) = downloadFromPaperMC(folder, mcVersion, "folia")
    fun downloadSpigot(folder: File, mcVersion: String) = downloadFromGetBukkit(folder, mcVersion, "spigot")
    fun downloadBukkit(folder: File, mcVersion: String) = downloadFromGetBukkit(folder, mcVersion, "craftbukkit")
    fun downloadBungeecord(folder: File, mcVersion: String): DownloadResult = downloadFile(folder, BUNGEECORD_REPO, "server.jar")
    fun downloadPurper(folder: File, mcVersion: String): DownloadResult = downloadFile(folder, "$PURPER_REPO/$mcVersion/latest/download", "server.jar")
    fun downloadPufferFish(folder: File, mcVersion: String): DownloadResult {
        val mainURL = URI("$PUFFERFISH_REPO/Pufferfish-$mcVersion/lastSuccessfulBuild")

        val buildURL = URI("$mainURL/api/json")
        val path = JsonParser.parseString(buildURL.toURL().readText()).asJsonObject.getAsJsonArray("artifacts").get(0).asJsonObject.get("relativePath").asString

        return downloadFile(folder, "$mainURL/artifact/$path", "server.jar")
    }

    fun downloadFile(
        folder: File,
        downloadURL: String,
        name: String
    ): DownloadResult = downloadFile(folder, URI(downloadURL), name)

    fun downloadFile(
        folder: File,
        downloadURL: URI,
        name: String
    ): DownloadResult  {
        val file = File(folder, name)

        return if (!file.exists()) {
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
        } else {
            DownloadResult(DownloadResultType.SUCCESS, null, file)
        }
    }

}

data class DownloadResult(val downloadResultType: DownloadResultType, val e: String?, val jarFile: File?)

enum class DownloadResultType {
    SUCCESS,
    FAILED
}