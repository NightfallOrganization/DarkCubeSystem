import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow) apply false
    id("eu.darkcube.darkcube")
}

val standalone = configurations.register("include") { isTransitive = false }
val standaloneTransitive = configurations.register("includeTransitive")

val standaloneJar = tasks.register<ShadowJar>("standaloneJar") {
    configurations.addAll(listOf(standalone.get(), standaloneTransitive.get()))
    dependsOn(tasks.named(sourceSets.main.get().compileJavaTaskName))
    from(sourceSets.main.map { it.output })
    relocate("org.h2", "eu.darkcube.system.impl.standalone.libs")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks {
    jar { enabled = false }
    assemble.configure {
        dependsOn(standaloneJar)
    }
    processResources.configure {
        expand(mapOf("version" to version))
        inputs.property("version", version)
    }
}

configurations {
    consumable("standalone") {
        extendsFrom(runtimeClasspath.get())
        outgoing.artifact(standaloneJar)
    }
}

dependencies {
    api(projects.darkcubesystemImplementationCommon)
    implementation(libs.h2)
    implementation(projects.darkcubesystemImplementationProviderStandalone)

    standalone(libs.h2)
    standalone(projects.darkcubesystemApi)
    standalone(projects.darkcubesystemImplementationCommon)
    standalone(projects.darkcubesystemImplementationProvider)
    standalone(projects.darkcubesystemImplementationProviderStandalone)
    standaloneTransitive(projects.darkcubesystemLibs)
}
