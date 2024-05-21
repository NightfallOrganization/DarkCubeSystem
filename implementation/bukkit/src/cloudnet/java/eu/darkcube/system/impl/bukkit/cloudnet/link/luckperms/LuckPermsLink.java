/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.cloudnet.link.luckperms;

import eu.darkcube.system.bukkit.link.PluginLink;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.Plugin;

public class LuckPermsLink extends PluginLink {
    private LinkContextCalculator calculator;

    public LuckPermsLink(Plugin source) throws Throwable {
        super(source, "LuckPerms");
    }

    @Override protected void link() {
        calculator = new LinkContextCalculator();
    }

    @Override protected void onEnable() {
        LuckPermsProvider.get().getContextManager().registerCalculator(calculator);
    }

    @Override protected void onDisable() {
        LuckPermsProvider.get().getContextManager().unregisterCalculator(calculator);
    }

    @Override protected void unlink() {
        calculator = null;
    }
}
