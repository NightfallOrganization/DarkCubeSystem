/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    `maven-publish`
    id("eu.darkcube.darkcube")
}

val embed = configurations.create("embed")

dependencies {
    embed(libs.bundles.adventure)
    embed(libs.brigadier)
    embed(libs.gson)
    embed(libs.annotations)
    embed(libs.caffeine)
}

val configuration = sourceRemapper.remap(embed, "eu.darkcube.system.libs", configurations.named("api"))

publishing {
    publications {
        register<MavenPublication>("libs") {
            from(configuration.component.configureJava(sourceSets.main, tasks.jar).component)
        }
    }
}
