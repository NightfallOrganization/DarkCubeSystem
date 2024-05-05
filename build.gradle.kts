/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    alias(libs.plugins.shadow)
    id("eu.darkcube.darkcube")
}

val plugins = configurations.register("plugins") { isTransitive = false }
val inject = configurations.register("inject") { isTransitive = false }

tasks {
    val finalJar = register<Jar>("finalJar") {
        dependsOn(shadowJar)
        dependsOn(plugins)
        dependsOn(inject)
        from(shadowJar.get().outputs.files.map { zipTree(it) })
        plugins.get().incoming.files.forEach {
            from(it) {
                into("plugins")
            }
        }
        inject.get().incoming.files.forEach {
            from(it) {
                into("inject")
            }
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = null
    }
    shadowJar.configure { destinationDirectory = temporaryDir }
    jar.configure { destinationDirectory = temporaryDir }
    assemble.configure { dependsOn(finalJar)}
}