/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer;

import static java.lang.constant.ConstantDescs.*;
import static java.lang.constant.DirectMethodHandleDesc.Kind.STATIC;

import java.io.IOException;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeModel;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodTransform;
import java.lang.classfile.instruction.InvokeDynamicInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.LocalVariable;
import java.lang.classfile.instruction.NewObjectInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.LambdaMetafactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.jar.JarFile;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.cloudnetservice.wrapper.transform.ClassTransformerRegistry;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.impl.agent.AgentAccess;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class PaperMainClassLoadingTransformer implements ClassTransformer {

    private static final String CNI_PAPERCLIP = "io/papermc/paperclip/Paperclip";
    private static final String MN_MAIN = "main";
    private static final String MN_MAIN_0 = "lambda$main$0";
    private static final String MN_METAFACTORY = "metafactory";
    private static final String CNI_URLClassLoader = "java/net/URLClassLoader";

    private static final ClassDesc CD_URL = ClassDesc.of(URL.class.getName());
    private static final ClassDesc CD_ClassLoader = ClassDesc.of(ClassLoader.class.getName());
    private static final ClassDesc CD_Paperclip = ClassDesc.ofInternalName(CNI_PAPERCLIP);
    private static final ClassDesc CD_this = ClassDesc.of(PaperMainClassLoadingTransformer.class.getName());
    private static final ClassDesc CD_Runnable = ClassDesc.of(Runnable.class.getName());
    private static final ClassDesc CD_LambdaMetafactory = ClassDesc.of(LambdaMetafactory.class.getName());
    private static final MethodTypeDesc MTD_loadClasses = MethodTypeDesc.of(CD_ClassLoader, CD_URL.arrayType());
    private static final MethodTypeDesc MTD_main_0 = MethodTypeDesc.of(CD_void, CD_String, CD_ClassLoader, CD_String.arrayType());
    private static final MethodTypeDesc MTD_main_0_Runnable = MethodTypeDesc.of(CD_Runnable, CD_String, CD_ClassLoader, CD_String.arrayType());
    private static final DirectMethodHandleDesc MHD_main_0 = MethodHandleDesc.ofMethod(STATIC, CD_Paperclip, MN_MAIN_0, MTD_main_0);

    private static final MethodTypeDesc MTD_metafactory = MethodTypeDesc.of(CD_CallSite, CD_MethodHandles_Lookup, CD_String, CD_MethodType, CD_MethodType, CD_MethodHandle, CD_MethodType);
    private static final DirectMethodHandleDesc MHD_metafactory = MethodHandleDesc.ofMethod(STATIC, CD_LambdaMetafactory, MN_METAFACTORY, MTD_metafactory);

    public static void register(ClassTransformerRegistry transformerRegistry) {
        transformerRegistry.registerTransformer(new PaperMainClassLoadingTransformer());
    }

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        var codeTransformMain0 = new Main0CodeTransformer();
        MethodTransform transformMain0 = (builder, element) -> {
            if (element instanceof CodeModel codeModel) {
                builder.transformCode(codeModel, codeTransformMain0);
                return;
            }
            builder.with(element);
        };
        var transformMain = MethodTransform.transformingCode(CodeTransform.ofStateful(MainCodeTransformer::new));
        var ctMain = ClassTransform.transformingMethods(mm -> mm.methodName().equalsString(MN_MAIN), transformMain);
        ClassTransform ctMain0 = (builder, element) -> {
            if (element instanceof MethodModel method && method.methodName().equalsString(MN_MAIN_0)) {
                builder.withMethod(MN_MAIN_0, MTD_main_0, method.flags().flagsMask(), methodBuilder -> {
                    for (var methodElement : method) {
                        transformMain0.accept(methodBuilder, methodElement);
                    }
                });
                return;
            }
            builder.with(element);
        };
        return ctMain.andThen(ClassTransform.ACCEPT_ALL).andThen(ctMain0);
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        var isPaperclip = internalClassName.equals(CNI_PAPERCLIP);
        return isPaperclip ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }

    private static final class Main0CodeTransformer implements CodeTransform {
        @Override
        public void accept(CodeBuilder builder, CodeElement element) {
            if (element instanceof LocalVariable localVariable && localVariable.name().equalsString("classLoader") && localVariable.slot() == 1) {
                builder.localVariable(localVariable.slot(), localVariable.name().stringValue(), CD_ClassLoader, localVariable.startScope(), localVariable.endScope());
                return;
            }
            builder.with(element);
        }
    }

    private static final class MainCodeTransformer implements CodeTransform {
        private static final int STATE_IDLE_1 = 0;
        private static final int STATE_DROP_1 = 1;
        private static final int STATE_IDLE_2 = 2;
        private int state = STATE_IDLE_1;

        @Override
        public void accept(CodeBuilder builder, CodeElement element) {
            if (element instanceof LocalVariable localVariable && localVariable.slot() == 3 && localVariable.name().equalsString("classLoader")) {
                builder.localVariable(localVariable.slot(), localVariable.name().stringValue(), CD_ClassLoader, localVariable.startScope(), localVariable.endScope());
                return;
            }
            if (this.state == STATE_IDLE_1) {
                if (element instanceof NewObjectInstruction newObjectInst && newObjectInst.className().asInternalName().equals(CNI_URLClassLoader)) {
                    this.state = STATE_DROP_1;
                }
            }
            if (this.state == STATE_DROP_1) {
                if (element instanceof InvokeInstruction invokeInst && invokeInst.owner().asInternalName().equals(CNI_URLClassLoader)) {
                    this.state = STATE_IDLE_2;
                    addCustomCode1(builder);
                }
                return;
            }
            if (this.state == STATE_IDLE_2) {
                if (element instanceof InvokeDynamicInstruction invoke) {
                    var bootstrapMethod = invoke.bootstrapMethod();
                    var bootstrapArgs = invoke.bootstrapArgs();
                    if (bootstrapMethod.methodName().equals("metafactory")) {
                        var invocationName = invoke.name().stringValue();
                        var args = bootstrapArgs.toArray(ConstantDesc[]::new);
                        args[1] = MHD_main_0;

                        var csd = DynamicCallSiteDesc.of(MHD_metafactory, invocationName, MTD_main_0_Runnable, args);

                        builder.invokedynamic(csd);
                        return;
                    }
                }
            }
            builder.with(element);
        }

        private void addCustomCode1(CodeBuilder builder) {
            builder.aload(1);
            builder.invokestatic(CD_this, "loadClasses", MTD_loadClasses);
        }
    }

    @Api
    public static ClassLoader loadClasses(URL[] urls) {
        var instrumentation = AgentAccess.instrumentation();
        for (var url : urls) {
            try {
                var file = Path.of(url.toURI()).toFile();
                var jarFile = new JarFile(file);
                instrumentation.appendToSystemClassLoaderSearch(jarFile);
            } catch (URISyntaxException | IOException e) {
                throw new Error(e);
            }
        }
        return ClassLoader.getSystemClassLoader();
    }
}
