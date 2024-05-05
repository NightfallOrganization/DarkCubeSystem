/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.annotations.Api;

@Api
public enum GameState {
    @Api LOBBY,
    @Api INGAME,
    @Api STOPPING,
    @Api UNKNOWN,

    ;

    @Api
    public static GameState fromString(String gameState) {
        for (var state : values()) {
            if (state.toString().equals(gameState)) {
                return state;
            }
        }
        return GameState.UNKNOWN;
    }

    @Override
    public String toString() {
        return super.name();
    }
}
