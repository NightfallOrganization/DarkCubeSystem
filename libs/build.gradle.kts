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

val embedStep1 = configurations.create("embedStep1")
val embedStep2 = configurations.create("embedStep2")

dependencies {
    embedStep1(libs.brigadier)
    embedStep1(libs.gson)
    embedStep1(libs.annotations)
    embedStep1(libs.caffeine)

    embedStep2(libs.bundles.adventure) {
        exclude(group = "com.google.code.gson")
    }
}

sourceRemapper.remap(embedStep2, "eu.darkcube.system.libs", configurations.named("api"))
sourceRemapper.remap(embedStep1, "eu.darkcube.system.libs", configurations.named("api"))

publishing {
    publications {
        register<MavenPublication>("libs") {
            from(components["java"])
        }
    }
}
