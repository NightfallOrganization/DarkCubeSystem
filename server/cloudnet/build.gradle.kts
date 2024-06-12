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

dependencies {
    compileOnly(libs.cloudnet.asm) // in cloudnet but not exposed
    compileOnly(libs.cloudnet.asm.tree) // in cloudnet but not exposed
    api(projects.darkcubesystemServer)
    api(libs.cloudnet.wrapper)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}