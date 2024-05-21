/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.deprecated;

public class InvalidCommandException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidCommandException() {
    }

    public InvalidCommandException(Throwable throwable) {
        super(throwable);
    }

    public InvalidCommandException(String msg) {
        super(msg);
    }
}
