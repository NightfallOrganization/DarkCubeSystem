/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.server.cloudnet;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getObjectType;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.transform.Transformer;
import eu.cloudnetservice.wrapper.transform.TransformerRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CloudNetIntegration {
    private static final String HOLLOWCUBE_PACKAGE = "net/hollowcube/minestom";
    private static final String HOLLOWCUBE_PACKAGE_EXTENSIONS = HOLLOWCUBE_PACKAGE + "/extensions";
    private static final String MINESTOM_EXTENSION_BOOTSTRAP = HOLLOWCUBE_PACKAGE_EXTENSIONS + "/ExtensionBootstrap";
    private static final String MINESTOM_PACKAGE = "net/minestom/server";
    private static final String MINESTOM_PACKAGE_EXTENSIONS = MINESTOM_PACKAGE + "/extensions";
    private static final String MINESTOM_EXTENSION_MANAGER = MINESTOM_PACKAGE_EXTENSIONS + "/ExtensionManager";
    private static final String CLASS_MINECRAFT_SERVER = "MinecraftServer";
    private static final String METHOD_GET_EXTENSION_MANAGER = "getExtensionManager";

    public static void init() {
        var transformerRegistry = InjectionLayer.boot().instance(TransformerRegistry.class);
        transformerRegistry.registerTransformer(MINESTOM_PACKAGE, CLASS_MINECRAFT_SERVER, new CloudNetTransformer());
    }

    private record CloudNetTransformer() implements Transformer {
        @Override
        public void transform(@NotNull String s, @NotNull ClassNode classNode) {
            addGetExtensionManager(classNode);
        }

        private void addGetExtensionManager(ClassNode node) {
            for (var method : node.methods)
                if (method.name.equals(METHOD_GET_EXTENSION_MANAGER)) return;
            var method = new MethodNode(ACC_PUBLIC | ACC_STATIC, METHOD_GET_EXTENSION_MANAGER, getMethodDescriptor(getObjectType(MINESTOM_EXTENSION_MANAGER)), null, null);
            method.instructions.add(new MethodInsnNode(INVOKESTATIC, MINESTOM_EXTENSION_BOOTSTRAP, METHOD_GET_EXTENSION_MANAGER, method.desc, false));
            method.instructions.add(new InsnNode(ARETURN));
            node.methods.add(method);
        }
    }
}
