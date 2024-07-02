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

val step2 = sourceRemapper.remap(embedStep2, "eu.darkcube.system.libs", configurations.named("api"))
val step1 = sourceRemapper.remap(embedStep1, "eu.darkcube.system.libs", configurations.named("api"))

val component = sourceRemapper.createComponent(configurations.api, step1, step2)
configurations["remapApiElements"].attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, java.toolchain.languageVersion.get().asInt())
configurations["remapRuntimeElements"].attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, java.toolchain.languageVersion.get().asInt())
artifacts.add("remapApiElements", tasks.jar)
artifacts.add("remapRuntimeElements", tasks.jar)

component.addVariantsFromConfiguration(configurations.sourcesElements.get()) {
    this.mapToMavenScope("runtime")
    this.mapToOptional()
}
component.addVariantsFromConfiguration(configurations.javadocElements.get()) {
    this.mapToMavenScope("runtime")
    this.mapToOptional()
}

publishing {
    publications {
        register<MavenPublication>("libs") {
            from(component)
        }
    }
}
