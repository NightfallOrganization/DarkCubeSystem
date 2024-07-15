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

val common = configurations.register("common") { isTransitive = false }
val commonTransitive = configurations.register("commonTransitive")
val node = sourceSets.register("node")
val wrapper = sourceSets.register("wrapper")
val agent = sourceSets.register("agent")
val bootstrap = sourceSets.register("bootstrap")
val main = sourceSets.main

val nodeJar = tasks.register<Jar>("nodeJar") {
    archiveClassifier = "node"
    dependsOn(common)
    dependsOn(commonTransitive)
    from(bootstrap.map { it.output })
    from(main.map { it.output })
    from(node.map { it.output })
    common.get().files.forEach { from(zipTree(it)) }
    commonTransitive.get().files.forEach { from(zipTree(it)) }
    dependsOn(tasks.named(main.get().compileJavaTaskName))
    dependsOn(tasks.named(node.get().compileJavaTaskName))
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
val libJar = tasks.register<Jar>("libJar") {
    archiveClassifier = "lib"
    from(main.map { it.output })
    from(wrapper.map { it.output })
    dependsOn(common)
    dependsOn(commonTransitive)
    common.get().files.forEach { from(zipTree(it)) }
    commonTransitive.get().files.forEach { from(zipTree(it)) }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
val wrapperJar = tasks.register<Jar>("wrapperJar") {
    archiveClassifier = "wrapper"
    dependsOn(libJar)
    from(bootstrap.map { it.output })
    from(libJar) { rename { "lib.jar" } }
    dependsOn(tasks.named(main.get().compileJavaTaskName))
}
val agentJar = tasks.register<Jar>("agentJar") {
    from(agent.map { it.output })
    archiveClassifier = "agent"
    manifest.attributes["Premain-Class"] = "eu.darkcube.system.impl.agent.Agent"
}

tasks {
    jar { enabled = false }
    assemble.configure {
        dependsOn(nodeJar)
        dependsOn(wrapperJar)
    }
    processResources.configure {
        expand(mapOf("version" to version))
        inputs.property("version", version)
    }
}

configurations {
    named("nodeCompileOnly").configure { extendsFrom(compileClasspath.get()) }
    named("wrapperCompileOnly").configure { extendsFrom(compileClasspath.get()) }
    @Suppress("UnstableApiUsage") consumable("node") {
        extendsFrom(getByName("nodeRuntimeClasspath"))
        outgoing.artifact(nodeJar)
    }
    @Suppress("UnstableApiUsage") consumable("wrapper") {
        extendsFrom(getByName("wrapperRuntimeClasspath"))
        outgoing.artifact(wrapperJar)
    }
    @Suppress("UnstableApiUsage") consumable("agent") {
        extendsFrom(getByName("agentRuntimeClasspath"))
        outgoing.artifact(agentJar)
    }
}

dependencies {
    api(projects.darkcubesystemApiCloudnet)
    api(projects.darkcubesystemImplementationCommon)
    implementation(projects.darkcubesystemImplementationProviderCloudnet)

    common(projects.darkcubesystemApi)
    common(projects.darkcubesystemApiCloudnet)
    common(projects.darkcubesystemImplementationCommon)
    common(projects.darkcubesystemImplementationProvider)
    common(projects.darkcubesystemImplementationProviderCloudnet)
    commonTransitive(projects.darkcubesystemLibs)

    "nodeCompileOnly"(libs.cloudnet.node)
    "nodeCompileOnly"(main.map { it.output })

    "wrapperCompileOnly"(libs.cloudnet.wrapper)
    "wrapperCompileOnly"(main.map { it.output })
    "wrapperCompileOnly"(agent.map { it.output })

    "bootstrapCompileOnly"(main.map { it.output })
    "bootstrapCompileOnly"(agent.map { it.output })
    "bootstrapCompileOnly"(projects.darkcubesystemApiCloudnet)
    "bootstrapCompileOnly"(projects.darkcubesystemImplementationCommon)
}