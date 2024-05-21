/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

public class CommandNotFoundException extends Exception {

    private static final long serialVersionUID = -2980710899154740069L;

    private String message;

    public CommandNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return message + "\nValue: " + super.getMessage();
    }

}
