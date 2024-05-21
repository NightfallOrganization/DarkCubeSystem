/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

public class CommandPosition {

    private int level;

    protected CommandPosition(int level) {
        this.level = level;
    }

    protected int getPosition() {
        return level;
    }

    public final CommandPosition next() {
        return new CommandPosition(level + 1);
    }

    protected CommandPosition getNext() {
        return new CommandPosition(level + 1);
    }
}
