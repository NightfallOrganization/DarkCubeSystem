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

val cloudnetSource by sourceSets.register("cloudnet")

configurations.named(cloudnetSource.compileClasspathConfigurationName).configure {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.6-R0.1-20240610.063449-113")
    implementation(projects.darkcubesystemImplementationBukkit)
    compileOnly(libs.cloudnet.driver)
    api(libs.viaversion.common)

    cloudnetSource.implementationConfigurationName(projects.darkcubesystemApiCloudnet)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationProvider)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetPluginRaw" }
    cloudnetSource.implementationConfigurationName(sourceSets.main.map { it.output })
    cloudnetSource.implementationConfigurationName(libs.cloudnet.wrapper)
    cloudnetSource.implementationConfigurationName(libs.cloudnet.asm)
    cloudnetSource.implementationConfigurationName(libs.cloudnet.asm.tree)
}

tasks {
    jar {
        from(cloudnetSource.output)
    }
}

configurations.consumable("version") {
    outgoing.artifact(tasks.jar) {
        name = "v1_20_6"
    }
}
