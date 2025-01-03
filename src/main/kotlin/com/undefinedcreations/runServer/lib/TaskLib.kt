package com.undefinedcreations.runServer.lib

import org.gradle.api.Project
import org.gradle.api.Task

/**
 * This object is used to get the task.
 *
 * @since 1.0.
 */
object TaskLib {

    /**
     * Gets the task to build and run on the server
     *
     * @param project The gradle project
     * @param inputTask This will be null if there is no input task selected
     * @return The task to run.
     */
    fun getPluginTask(project: Project, inputTask: Task?): Task =
        project.tasks.named(inputTask?.name ?: "jar").get()
}