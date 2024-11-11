/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.commandapi.argument;

import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.commandapi.argument.ServerArguments;
import eu.darkcube.system.server.item.enchant.Enchantment;

public class ServerArgumentsImpl implements ServerArguments {
    @Override
    public @NotNull ArgumentType<@NotNull Enchantment> enchantment_() {
        return new EnchantmentArgument();
    }
}
