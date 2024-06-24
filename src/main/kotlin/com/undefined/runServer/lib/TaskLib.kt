package com.undefined.runServer.lib

import org.gradle.api.Project
import org.gradle.api.Task

object TaskLib {
    fun getPluginTask(project: Project): Task {
        if (project.tasks.names.contains("remap")) return project.tasks.named("remap").get()
        if (project.tasks.names.contains("shadowJar")) return project.tasks.named("shadowJar").get()
        return project.tasks.named("jar").get()
    }
}