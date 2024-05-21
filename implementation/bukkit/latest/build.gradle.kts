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
        name = "v1_20_R2"
    }
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.2-R0.1-20231113.183409-94")
    implementation(projects.darkcubesystemImplementationBukkit)
    compileOnly(libs.cloudnet.driver)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    jar {
        destinationDirectory = temporaryDir
    }
}