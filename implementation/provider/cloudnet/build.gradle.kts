/*
 * Copyright (c) 2024-2025. [DarkCube]
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
    api(projects.darkcubesystemImplementationProvider)
    api(libs.cloudnet.driver.api)
    api(libs.cloudnet.driver.impl)
}