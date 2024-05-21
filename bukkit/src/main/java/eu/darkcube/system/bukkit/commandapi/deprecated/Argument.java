/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

public class Argument {

    private boolean needed;
    private String name;
    private String beschreibung;

    public Argument(String name, String beschreibung) {
        this(name, beschreibung, true);
    }

    public Argument(String name, String beschreibung, boolean needed) {
        this.beschreibung = beschreibung;
        this.needed = needed;
        this.name = name;
    }

    public String getDescription() {
        return beschreibung;
    }

    public boolean isNeeded() {
        return needed;
    }

    public String getName() {
        return name;
    }
}
