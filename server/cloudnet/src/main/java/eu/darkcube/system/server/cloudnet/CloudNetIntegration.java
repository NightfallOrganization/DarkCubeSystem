/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.cloudnet;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassTransform;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.cloudnetservice.wrapper.transform.ClassTransformerRegistry;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public class CloudNetIntegration {
    @Api
    public static void init() {
        var transformerRegistry = InjectionLayer.boot().instance(ClassTransformerRegistry.class);
        transformerRegistry.registerTransformer(new CloudNetTransformer());
    }

    private record CloudNetTransformer() implements ClassTransformer {
        private static final String CNI_MINECRAFT_SERVER = "net/minestom/server/MinecraftServer";
        private static final String MN_GET_EXTENSION_MANAGER = "getExtensionManager";
        private static final ClassDesc CD_EXTENSION_MANAGER = ClassDesc.of("net.minestom.server.extensions.ExtensionManager");
        private static final ClassDesc CD_EXTENSION_BOOTSTRAP = ClassDesc.of("net.hollowcube.minestom.extensions.ExtensionBootstrap");
        private static final MethodTypeDesc MTD_GET_EXTENSION_MANAGER = MethodTypeDesc.of(CD_EXTENSION_MANAGER);

        @Override
        public @NotNull ClassTransform provideClassTransform() {
            return new ClassTransform() {
                @Override
                public void accept(ClassBuilder builder, ClassElement element) {
                    builder.with(element);
                }

                @Override
                public void atEnd(ClassBuilder builder) {
                    builder.withMethodBody(MN_GET_EXTENSION_MANAGER, MTD_GET_EXTENSION_MANAGER, ACC_PUBLIC | ACC_STATIC, b -> {
                        b.invokestatic(CD_EXTENSION_BOOTSTRAP, MN_GET_EXTENSION_MANAGER, MTD_GET_EXTENSION_MANAGER);
                        b.areturn();
                    });
                }
            };
        }

        @Override
        public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
            var isMinecraftServer = internalClassName.equals(CNI_MINECRAFT_SERVER);
            return isMinecraftServer ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
        }
    }
}
