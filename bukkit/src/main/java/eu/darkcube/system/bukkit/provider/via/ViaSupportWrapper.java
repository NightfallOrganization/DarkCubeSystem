/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.provider.via;

import java.util.List;
import java.util.UUID;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.entity.Player;

class ViaSupportWrapper implements ViaSupport {

    private final ViaSupport via;

    public ViaSupportWrapper(ViaSupport via) {
        this.via = via;
    }

    @Override
    public boolean supported() {
        return via != null && via.supported();
    }

    @Override
    public int version(UUID uuid) {
        return via.version(uuid);
    }

    @Override
    public int serverVersion() {
        return via.serverVersion();
    }

    @Override
    public int[] supportedVersions() {
        return via.supportedVersions();
    }

    @Override
    public List<String> tabComplete(int playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, Suggestions suggestions) {
        return via.tabComplete(playerVersion, player, commandLine, parse, suggestions);
    }
}
