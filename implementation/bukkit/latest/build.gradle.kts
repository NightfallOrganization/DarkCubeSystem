/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.paperweight.userdev)
    id("eu.darkcube.darkcube")
}

configurations.consumable("version") {
    outgoing.artifact(tasks.jar) {
        name = "v1_20_6"
    }
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.6-R0.1-SNAPSHOT")
    implementation(projects.darkcubesystemImplementationBukkit)
    compileOnly(libs.cloudnet.driver)
    api(libs.viaversion.common)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    jar {
        destinationDirectory = temporaryDir
    }
}