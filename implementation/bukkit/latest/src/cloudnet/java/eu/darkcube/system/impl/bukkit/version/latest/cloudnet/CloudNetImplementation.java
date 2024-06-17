/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.cloudnet;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.transform.TransformerRegistry;
import eu.darkcube.system.impl.bukkit.version.latest.LatestCloudNetImplementation;

public class CloudNetImplementation implements LatestCloudNetImplementation {
    @Override
    public void init() {
        var transformerRegistry = InjectionLayer.boot().instance(TransformerRegistry.class);
        BukkitCustomKyoriTransformer.register(transformerRegistry);
    }
}
