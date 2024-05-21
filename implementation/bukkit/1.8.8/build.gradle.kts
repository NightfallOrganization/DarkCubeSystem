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

configurations.consumable("version") {
    outgoing.artifact(tasks.jar) {
        name = "v1_8_R3"
    }
}

dependencies {
    api("io.papermc.paper:paper:1.8.8-R0.1-SNAPSHOT")
    api(projects.darkcubesystemImplementationBukkit)
    api(libs.viaversion.common)
}