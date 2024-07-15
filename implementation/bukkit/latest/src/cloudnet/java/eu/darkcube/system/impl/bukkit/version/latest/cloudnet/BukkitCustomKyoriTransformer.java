/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.latest.cloudnet;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassReader;
import java.lang.classfile.ClassTransform;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.cloudnetservice.wrapper.transform.ClassTransformerRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import io.papermc.paper.plugin.entrypoint.classloader.ClassloaderBytecodeModifier;

public class BukkitCustomKyoriTransformer implements ClassTransformer {
    private static final String CNI_MINECRAFT_REFLECTION = "eu/darkcube/system/libs/net/kyori/adventure/platform/bukkit/MinecraftReflection";

    // @Override
    // public void transform(@NotNull String className, @NotNull ClassNode classNode) {
    // }
    //
    // @Override
    // public byte[] toByteArray(ClassNode classNode) {
    //     var bytes = Transformer.super.toByteArray(classNode);
    //     return ClassloaderBytecodeModifier.bytecodeModifier().modify(null, bytes);
    // }
    //
    public static void register(ClassTransformerRegistry transformerRegistry) {
        transformerRegistry.registerTransformer(new BukkitCustomKyoriTransformer());
    }

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return new ClassTransform() {
            @Override
            public void atStart(ClassBuilder builder) {
                var classModel = builder.original().orElseThrow();
                var classReader = (ClassReader) classModel.constantPool();
                var originalClassBytes = classReader.readBytes(0, classReader.classfileLength());
                var bytes = ClassloaderBytecodeModifier.bytecodeModifier().modify(null, originalClassBytes);
                var newModel = ClassFile.of().parse(bytes);
                newModel.forEach(builder::with);
            }

            @Override
            public void accept(ClassBuilder builder, ClassElement element) {
            }
        };
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalName) {
        var isMinecraftReflection = internalName.equals(CNI_MINECRAFT_REFLECTION);
        return isMinecraftReflection ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
