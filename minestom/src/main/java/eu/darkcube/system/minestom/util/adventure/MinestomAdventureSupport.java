/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.util.adventure;

import eu.darkcube.system.util.AdventureSupport;

public interface MinestomAdventureSupport extends AdventureSupport {

    static MinestomAdventureSupport adventureSupport() {
        return (MinestomAdventureSupport) AdventureSupport.adventureSupport();
    }

    @Override MinestomAudienceProvider audienceProvider();

}
