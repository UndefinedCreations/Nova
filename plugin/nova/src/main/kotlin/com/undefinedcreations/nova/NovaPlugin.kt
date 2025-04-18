package com.undefinedcreations.nova

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This class is registering the task with gradle
 *
 * @since 1.0.0
 */
class NovaPlugin : Plugin<Project> {

    /**
     * This will be run when the plugin is loading.
     *
     * @param target The gradle project
     */
    override fun apply(target: Project) {
        target.tasks.register("runServer", RunServerTask::class.java) {
            it.group = "nova"
            it.description = "This task will run an minecraft server inside your Intellij"
        }
    }
}