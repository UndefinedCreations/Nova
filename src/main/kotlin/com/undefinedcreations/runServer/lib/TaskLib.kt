package com.undefinedcreations.runServer.lib

import com.undefinedcreations.runServer.RunServerTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import java.io.File

/**
 * This object is used to get the task.
 *
 * @since 1.0.
 */
object TaskLib {

    object TaskNames {
        const val JAR = "jar"
        const val SHADOW = "shadowJar"
        const val REMAP = "remap"
    }

    /**
     * Gets the task to build and run on the server
     *
     * @param project The gradle project
     * @param inputTask This will be null if there is no input task selected
     * @return The task to run.
     */
    @Deprecated("FUCKING SHIT")
    fun getPluginTask(project: Project, inputTask: TaskProvider<*>?): Task =
        project.tasks.named(inputTask?.name ?: "jar").get()


    /**
     * Gets the projects plugin file by check what task to use and the depending on them
     *
     * @param project The gradle project
     * @param inputTask The input task that can be specified by the user
     * @param runServerTask The run server task
     * @return This will return the projects plugin file
     */
    fun findPluginJar(project: Project, inputTask: TaskProvider<*>?, runServerTask: RunServerTask): File {
        if (inputTask?.isPresent == true) {
            return inputTask.get().outputs.files.singleFile
        }
        if (TaskNames.REMAP in project.tasks.names) {
            val task = project.tasks.named(TaskNames.REMAP)
            return task.get().outputs.files.singleFile
        }
        if (TaskNames.SHADOW in project.tasks.names) {
            val task = project.tasks.named(TaskNames.SHADOW) as AbstractArchiveTask
            return task.archiveFile.get().asFile
        }
        val jar = project.tasks.named(TaskNames.JAR) as AbstractArchiveTask
        return jar.archiveFile.get().asFile
    }
}