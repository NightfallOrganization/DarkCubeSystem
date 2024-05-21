/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class SpacedCommand extends Command implements ISpaced {

    public SpacedCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung, Argument... arguments) {
        super(plugin, name, childs, beschreibung, arguments);
    }

    public SpacedCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung) {
        super(plugin, name, childs, beschreibung);
    }

    private String spaced = "";

    @Override
    public void setSpaced(String spaced) {
        this.spaced = spaced;
        for (var child : getChilds()) {
            ((SubCommand) child).setSpaced(spaced);
        }
    }

    @Override
    public String getSpaced() {
        return spaced;
    }

    public abstract static class SubCommand extends Command {

        public SubCommand(JavaPlugin plugin, String name, Command[] childs, String beschreibung, Argument... arguments) {
            super(plugin, name, childs, beschreibung, arguments);
        }

        public SubCommand(JavaPlugin plugin, String name, Command[] childs, String beschreibung) {
            super(plugin, name, childs, beschreibung);
        }

        private String spaced = "";

        public String getSpaced() {
            return spaced;
        }

        public void setSpaced(String spaced) {
            this.spaced = spaced;
        }

    }

    public abstract static class SpacedSubCommand extends SubCommand implements ISpaced {

        public SpacedSubCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung, Argument... arguments) {
            super(plugin, name, childs, beschreibung, arguments);
        }

        public SpacedSubCommand(JavaPlugin plugin, String name, SubCommand[] childs, String beschreibung) {
            super(plugin, name, childs, beschreibung);
        }

        private String spaced = "";

        @Override
        public void setSpaced(String spaced) {
            this.spaced = spaced;
            for (var child : getChilds()) {
                ((SubCommand) child).setSpaced(spaced);
            }
        }

        @Override
        public String getSpaced() {
            return spaced;
        }
    }
}
