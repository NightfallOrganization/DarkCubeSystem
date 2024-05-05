/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.GameState;

public final class DarkCubeServer {

    private static final AtomicInteger playingPlayers = new AtomicInteger();
    private static final AtomicInteger spectatingPlayers = new AtomicInteger();
    private static final AtomicInteger maxPlayingPlayers = new AtomicInteger(-1);
    private static final Document.Mutable extra = Document.newJsonDocument();
    private static volatile GameState gameState = GameState.UNKNOWN;
    private static volatile String displayName = InjectionLayer.boot().instance(ComponentInfo.class).componentName();
    private static volatile boolean autoConfigure = true;

    public static @NotNull GameState gameState() {
        return gameState;
    }

    public static void autoConfigure(boolean autoConfigure) {
        DarkCubeServer.autoConfigure = autoConfigure;
    }

    public static boolean autoConfigure() {
        return autoConfigure;
    }

    public static void gameState(@NotNull GameState gameState) {
        DarkCubeServer.gameState = gameState;
    }

    public static @NotNull AtomicInteger maxPlayingPlayers() {
        return maxPlayingPlayers;
    }

    public static void displayName(@NotNull String displayName) {
        DarkCubeServer.displayName = displayName;
    }

    public static @NotNull String displayName() {
        return displayName;
    }

    public static @NotNull AtomicInteger playingPlayers() {
        return playingPlayers;
    }

    public static @NotNull AtomicInteger spectatingPlayers() {
        return spectatingPlayers;
    }

    public static void extra(Consumer<Document.Mutable> modifier) {
        synchronized (extra) {
            modifier.accept(extra);
        }
    }

    public static Document extra() {
        synchronized (extra) {
            return extra.immutableCopy();
        }
    }
}
