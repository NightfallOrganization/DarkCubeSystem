/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.server.inventory.listener;

import eu.darkcube.system.server.inventory.listener.ClickData;

public record ClickDataImpl(boolean isRight, boolean isLeft, boolean isShift) implements ClickData {
}
