/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.cloudnet;

import eu.cloudnetservice.wrapper.transform.Transformer;
import eu.cloudnetservice.wrapper.transform.TransformerRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import io.papermc.paper.plugin.entrypoint.classloader.ClassloaderBytecodeModifier;
import org.objectweb.asm.tree.ClassNode;

public class BukkitCustomKyoriTransformer implements Transformer {

    @Override
    public void transform(@NotNull String className, @NotNull ClassNode classNode) {
    }

    @Override
    public byte[] toByteArray(ClassNode classNode) {
        var bytes = Transformer.super.toByteArray(classNode);
        return ClassloaderBytecodeModifier.bytecodeModifier().modify(null, bytes);
    }

    public static void register(TransformerRegistry transformerRegistry) {
        transformerRegistry.registerTransformer("eu/darkcube/system/libs/net/kyori/adventure/platform/bukkit", "MinecraftReflection", new BukkitCustomKyoriTransformer());
    }
}
