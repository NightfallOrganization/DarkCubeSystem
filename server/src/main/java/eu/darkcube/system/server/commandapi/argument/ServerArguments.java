/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.commandapi.argument;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.server.item.enchant.Enchantment;

public interface ServerArguments {
    @Api
    @NotNull
    static ArgumentType<@NotNull Enchantment> enchantment() {
        return ServerArgumentsHolder.instance.enchantment_();
    }

    @NotNull
    ArgumentType<@NotNull Enchantment> enchantment_();
}

class ServerArgumentsHolder {
    static final ServerArguments instance = InternalProvider.instance().instance(ServerArguments.class);
}
