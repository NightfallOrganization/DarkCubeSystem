/*
 * Copyright (c) 2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer.velocity;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.CD_boolean;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.MethodModel;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class VelocityEncryptionRequestPacketTransformer implements ClassTransformer {
    private static final String CNI_EncryptionRequestPacket = "com/velocitypowered/proxy/protocol/packet/EncryptionRequestPacket";
    private static final ClassDesc CD_EncryptionRequestPacket = ClassDesc.ofInternalName(CNI_EncryptionRequestPacket);
    private static final MethodTypeDesc MTD_setNoAuthentication = ConstantDescs.MTD_void;

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return (builder, element) -> {
            if (element instanceof MethodModel method) {
                if (method.methodName().equalsString("getPublicKey")) {
                    add_setNoAuthentication(builder);
                    return;
                }
            }
            builder.with(element);
        };
    }

    private void add_setNoAuthentication(ClassBuilder classBuilder) {
        classBuilder.withMethod("setNoAuthentication", MTD_setNoAuthentication, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
            builder.aload(0);
            builder.iconst_0();
            builder.putfield(CD_EncryptionRequestPacket, "shouldAuthenticate", CD_boolean);
            builder.return_();
        }));
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        if (CNI_EncryptionRequestPacket.equals(internalClassName)) return TransformWillingness.ACCEPT_ONCE;
        return TransformWillingness.REJECT;
    }
}
