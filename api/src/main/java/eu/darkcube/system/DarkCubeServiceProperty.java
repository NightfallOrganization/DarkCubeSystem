/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.property.DocProperty;
import eu.darkcube.system.util.GameState;

public class DarkCubeServiceProperty {
    public static final DocProperty<Boolean> AUTOCONFIGURED = DocProperty.property("autoconfigured", Boolean.class).withDefault(false);
    public static final DocProperty<GameState> GAME_STATE = DocProperty.property("gameState", GameState.class).withDefault(null);
    public static final DocProperty<Integer> PLAYING_PLAYERS = DocProperty.property("playingPlayers", Integer.class).withDefault(-1);
    public static final DocProperty<Integer> SPECTATING_PLAYERS = DocProperty.property("spectatingPlayers", Integer.class).withDefault(-1);
    public static final DocProperty<Integer> MAX_PLAYING_PLAYERS = DocProperty.property("maxPlayingPlayers", Integer.class).withDefault(-1);
    public static final DocProperty<String> DISPLAY_NAME = DocProperty.property("displayName", String.class).withDefault(null);
    public static final DocProperty<Document> EXTRA = DocProperty.property("darkcubeExtra", Document.class).withDefault(null);
    // TODO Implement ProtocolVersion over these properties instead of Packets
}
