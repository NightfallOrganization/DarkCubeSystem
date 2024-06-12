/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.provider.via;

import java.util.List;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.impl.bukkit.provider.via.AbstractViaSupport;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import org.bukkit.entity.Player;

public class ViaSupportImpl extends AbstractViaSupport {
    @Override
    public void init() {
    }

    @Override
    public List<String> tabComplete(ProtocolVersion playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, Suggestions suggestions) {
        throw new UnsupportedOperationException();
    }
}
