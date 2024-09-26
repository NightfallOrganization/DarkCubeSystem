/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.wrapper.transform.ClassTransformerRegistry;
import eu.darkcube.system.cloudnet.packetapi.PacketAPI;
import eu.darkcube.system.impl.cloudnet.ModuleImplementation;
import eu.darkcube.system.impl.cloudnet.wrapper.transformer.PaperMainClassLoadingTransformer;
import eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaAbstractProtocolDetectorServiceTransformer;
import eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaInventoryFixTransformer;
import eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaProtocolDetectorServiceTransformer;
import eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaVelocityVersionProviderTransformer;
import eu.darkcube.system.impl.cloudnet.wrapper.userapi.WrapperUserAPI;
import eu.darkcube.system.impl.cloudnet.wrapper.util.data.WrapperCustomPersistentDataProvider;
import eu.darkcube.system.provider.InternalProvider;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;

@Singleton
public class DarkCubeSystemWrapper implements ModuleImplementation {
    private final WrapperUserAPI userAPI;

    @Inject
    public DarkCubeSystemWrapper(ClassTransformerRegistry transformerRegistry, WrapperUserAPI userAPI) {
        this.userAPI = userAPI;
        PaperMainClassLoadingTransformer.register(transformerRegistry);
        transformerRegistry.registerTransformer(new ViaProtocolDetectorServiceTransformer());
        transformerRegistry.registerTransformer(new ViaAbstractProtocolDetectorServiceTransformer());
        transformerRegistry.registerTransformer(new ViaVelocityVersionProviderTransformer());
        transformerRegistry.registerTransformer(new ViaInventoryFixTransformer());
    }

    @Override
    public void start() {
        InternalProvider.instance().register(UserAPI.class, this.userAPI);
        InternalProvider.instance().register(CustomPersistentDataProvider.class, new WrapperCustomPersistentDataProvider());
        PacketAPI.init();
    }

    @Override
    public void stop() {
        this.userAPI.close();
    }
}
