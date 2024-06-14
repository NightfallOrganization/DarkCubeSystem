/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://nexus.darkcube.eu/repository/darkcube-group/") {
            name = "DarkCube"
            credentials(PasswordCredentials::class)
        }
    }
}

plugins {
    id("eu.darkcube.darkcube.settings") version "1.1.5"
}

rootProject.name = "darkcubesystem"

includeSubProjects("", "bukkit", "proxy", "minestom", "libs")
includeSubProjects("api", "cloudnet")
includeSubProjects("server", "cloudnet")
includeSubProjects("implementation", "common", "server", "velocity", "minestom", "cloudnet", "standalone", "kyori-wrapper")
includeSubProjects("implementation:provider", "cloudnet", "standalone")
includeSubProjects("implementation:bukkit", "1.8.8", "latest")

fun includeSubProjects(root: String, vararg subProjects: String) {
    if (root.isNotBlank()) {
        includeProject(":$root")
    }
    for (subProject in subProjects) {
        val path = if (root.isBlank()) ":$subProject" else ":$root:$subProject"
        includeProject(path)
    }
}

fun includeProject(path: String) {
    val name = path.replaceFirst(":", "${rootProject.name}-").replace(':', '-').replace('.', '_')
    include(":$name")
    project(":$name").projectDir = rootProject.projectDir.resolve(path.replaceFirst(":", "").replace(':', '/'))
}
