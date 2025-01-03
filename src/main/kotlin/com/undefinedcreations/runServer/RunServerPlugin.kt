package com.undefinedcreations.runServer

import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * This class is registering the task with gradle
 *
 * @since 1.0.0
 */
class RunServerPlugin: Plugin<Project> {

    /**
     * This will be run when the plugin is loading.
     *
     * @param target The gradle project
     */
    override fun apply(target: Project) {
        target.tasks.register("runServer", RunServerTask::class.java) {
            it.group = "undefined"
            it.description = "This task will run an minecraft server inside your Intellij"
        }.get().dependsOn(target.tasks.named("jar"))
    }
}