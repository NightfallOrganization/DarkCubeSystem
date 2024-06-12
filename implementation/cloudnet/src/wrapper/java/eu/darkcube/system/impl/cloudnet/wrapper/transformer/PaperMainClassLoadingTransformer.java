/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Set;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleURLClassLoader;
import eu.cloudnetservice.wrapper.transform.Transformer;
import eu.cloudnetservice.wrapper.transform.TransformerRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PaperMainClassLoadingTransformer implements Transformer {
    @Override
    public void transform(@NotNull String classname, @NotNull ClassNode classNode) {
        for (var method : classNode.methods) {
            if (method.name.equals("main")) {
                for (var instruction : method.instructions) {
                    if (instruction instanceof TypeInsnNode typeNode) {
                        if (typeNode.getOpcode() != NEW) continue;
                        if (!typeNode.desc.equals(getInternalName(URLClassLoader.class))) continue;

                        var instructions = new InsnList();

                        instructions.add(new VarInsnNode(ALOAD, 2));
                        instructions.add(new InsnNode(POP));

                        instructions.add(new TypeInsnNode(NEW, getInternalName(ModuleURLClassLoader.class)));
                        instructions.add(new InsnNode(DUP));

                        instructions.add(new VarInsnNode(ALOAD, 1));
                        instructions.add(new InsnNode(ICONST_0));
                        instructions.add(new InsnNode(AALOAD));

                        instructions.add(new VarInsnNode(ALOAD, 1));
                        instructions.add(new InsnNode(ICONST_1));
                        instructions.add(new VarInsnNode(ALOAD, 1));
                        instructions.add(new InsnNode(ARRAYLENGTH));
                        instructions.add(new MethodInsnNode(INVOKESTATIC, getInternalName(Arrays.class), "copyOfRange", getMethodDescriptor(getType(Object[].class), getType(Object[].class), INT_TYPE, INT_TYPE)));
                        instructions.add(new MethodInsnNode(INVOKESTATIC, getInternalName(Set.class), "of", getMethodDescriptor(getType(Set.class), getType(Object[].class)), true));

                        instructions.add(new LdcInsnNode("paper"));
                        instructions.add(new MethodInsnNode(INVOKESTATIC, getInternalName(InjectionLayer.class), "fresh", getMethodDescriptor(getType(InjectionLayer.class), getType(String.class)), true));

                        instructions.add(new MethodInsnNode(INVOKESPECIAL, getInternalName(ModuleURLClassLoader.class), "<init>", getMethodDescriptor(VOID_TYPE, getType(URL.class), getType(Set.class), getType(InjectionLayer.class))));

                        instructions.add(new InsnNode(DUP));
                        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, getInternalName(ModuleURLClassLoader.class), "registerGlobally", getMethodDescriptor(VOID_TYPE)));

                        method.instructions.insertBefore(instruction, instructions);

                        method.instructions.remove(instruction.getNext());
                        method.instructions.remove(instruction.getNext());
                        method.instructions.remove(instruction.getNext());
                        method.instructions.remove(instruction.getNext());
                        method.instructions.remove(instruction);
                        break;
                    }
                }
            }
        }
    }

    public static void register(TransformerRegistry transformerRegistry) {
        transformerRegistry.registerTransformer("io/papermc/paperclip", "Paperclip", new PaperMainClassLoadingTransformer());
    }
}
