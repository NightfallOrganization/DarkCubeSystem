/*
 * Copyright (c) 2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer.velocity;

import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.Label;
import java.lang.classfile.Opcode;
import java.lang.classfile.TypeKind;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.FieldInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.LabelTarget;
import java.lang.classfile.instruction.LoadInstruction;
import java.lang.classfile.instruction.LocalVariable;
import java.lang.classfile.instruction.StoreInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class VelocityInitialLoginSessionHandlerTransformer implements ClassTransformer {
    private static final String CNI_InitialLoginSessionHandler = "com/velocitypowered/proxy/connection/client/InitialLoginSessionHandler";
    private static final String CNI_VelocityServer = "com/velocitypowered/proxy/VelocityServer";
    private static final String CNI_VelocityConfiguration = "com/velocitypowered/proxy/config/VelocityConfiguration";
    private static final String CNI_EncryptionRequestPacket = "com/velocitypowered/proxy/protocol/packet/EncryptionRequestPacket";
    private static final String CNI_EncryptionResponsePacket = "com/velocitypowered/proxy/protocol/packet/EncryptionResponsePacket";
    private static final String CNI_GameProfile = "com/velocitypowered/api/util/GameProfile";
    private static final String CNI_ServerLoginPacket = "com/velocitypowered/proxy/protocol/packet/ServerLoginPacket";
    private static final ClassDesc CD_CompletableFuture = CompletableFuture.class.describeConstable().orElseThrow();
    private static final ClassDesc CD_HttpClient = HttpClient.class.describeConstable().orElseThrow();
    private static final ClassDesc CD_HttpResponse = HttpResponse.class.describeConstable().orElseThrow();
    private static final ClassDesc CD_InitialLoginSessionHandler = ClassDesc.ofInternalName(CNI_InitialLoginSessionHandler);
    private static final ClassDesc CD_VelocityServer = ClassDesc.ofInternalName(CNI_VelocityServer);
    private static final ClassDesc CD_VelocityConfiguration = ClassDesc.ofInternalName(CNI_VelocityConfiguration);
    private static final ClassDesc CD_EncryptionRequestPacket = ClassDesc.ofInternalName(CNI_EncryptionRequestPacket);
    private static final ClassDesc CD_EncryptionResponsePacket = ClassDesc.ofInternalName(CNI_EncryptionResponsePacket);
    private static final ClassDesc CD_GameProfile = ClassDesc.ofInternalName(CNI_GameProfile);
    private static final ClassDesc CD_ServerLoginPacket = ClassDesc.ofInternalName(CNI_ServerLoginPacket);
    private static final MethodTypeDesc MTD_isOnlineMode = MethodTypeDesc.of(CD_boolean);
    private static final MethodTypeDesc MTD_getConfiguration = MethodTypeDesc.of(CD_VelocityConfiguration);
    private static final MethodTypeDesc MTD_handle = MethodTypeDesc.of(CD_boolean, CD_EncryptionResponsePacket);
    private static final MethodTypeDesc MTD_CompletableFuture_completedFuture = MethodTypeDesc.of(CD_CompletableFuture, CD_Object);
    private static final MethodTypeDesc MTD_forOfflinePlayer = MethodTypeDesc.of(CD_GameProfile, CD_String);
    private static final MethodTypeDesc MTD_getUsername = MethodTypeDesc.of(CD_String);

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return ClassTransform.ACCEPT_ALL.andThen(ClassTransform.transformingMethodBodies(model -> model.methodName().equalsString("lambda$handle$0"), CodeTransform.ofStateful(() -> new CodeTransform() {
            private int state = 0;

            @Override
            public void accept(CodeBuilder builder, CodeElement element) {
                if (state == 0) {
                    if (element instanceof LoadInstruction) {
                        state = 1;
                        return;
                    }
                    builder.with(element);
                } else if (state == 1) {
                    if (element instanceof InvokeInstruction invoke && invoke.opcode() == Opcode.INVOKEVIRTUAL && invoke.name().equalsString("isOnlineModeAllowed")) {
                        state = 2;
                    }
                    if (element instanceof LabelTarget) {
                        builder.with(element);
                    }
                } else if (state == 2) {
                    state = 3;
                } else if (state == 3) {
                    if (element instanceof InvokeInstruction i && i.name().equalsString("generateEncryptionRequest")) {
                        var local = builder.allocateLocal(TypeKind.BooleanType);
                        builder.with(element);
                        builder.dup();
                        isOnline(builder);
                        builder.dup().istore(local);

                        var after = builder.newLabel();
                        var end = builder.newLabel();
                        builder.ifne(after);

                        builder.invokevirtual(CD_EncryptionRequestPacket, "setNoAuthentication", MTD_void);

                        builder.goto_(end);
                        builder.labelBinding(after);
                        builder.pop(); // pop the original dup
                        builder.labelBinding(end);
                        state = 4;
                    } else {
                        builder.with(element);
                    }
                } else {
                    builder.with(element);
                }
            }
        }))).andThen(ClassTransform.transformingMethodBodies(model -> model.methodName().equalsString("handle") && model.methodTypeSymbol().equals(MTD_handle), CodeTransform.ofStateful(() -> new CodeTransform() {
            private int state = 0;
            private int slot_playerIp = -1;
            private int slot_httpClient = -1;
            private int slot_future = -1;
            private Label afterHttpClientBeginOffline;
            private Label completeFuture;

            @Override
            public void atStart(CodeBuilder builder) {
                afterHttpClientBeginOffline = builder.newLabel();
                completeFuture = builder.newLabel();
                slot_future = builder.allocateLocal(TypeKind.ReferenceType);
                builder.localVariable(slot_future, "completableFuture", CD_CompletableFuture, builder.startLabel(), builder.endLabel());
            }

            @Override
            public void accept(CodeBuilder builder, CodeElement element) {
                if (element instanceof LocalVariable l) {
                    if (slot_playerIp == -1) {
                        if (l.name().equalsString("playerIp")) {
                            slot_playerIp = l.slot();
                            builder.localVariable(l.slot(), l.name().stringValue(), l.typeSymbol(), builder.startLabel(), l.endScope());
                            return;
                        }
                    }
                    if (slot_httpClient == -1) {
                        if (l.typeSymbol().equals(CD_HttpClient)) {
                            slot_httpClient = l.slot();
                            builder.localVariable(l.slot(), l.name().stringValue(), l.typeSymbol(), builder.startLabel(), l.endScope());
                            return;
                        }
                    }
                }
                builder.with(element);
                if (state == 0) {
                    if (element instanceof StoreInstruction store && store.slot() == slot_playerIp) {
                        isOnline(builder);
                        builder.ifeq(afterHttpClientBeginOffline);
                        state = 1;
                    }
                } else if (state == 1) {
                    if (element instanceof InvokeInstruction i && i.opcode() == Opcode.INVOKEVIRTUAL && i.name().equalsString("sendAsync")) {
                        builder.astore(slot_future);
                        builder.goto_(completeFuture);
                        builder.labelBinding(afterHttpClientBeginOffline);

                        builder.aconst_null();
                        builder.invokestatic(CD_CompletableFuture, "completedFuture", MTD_CompletableFuture_completedFuture);
                        builder.astore(slot_future);

                        builder.aconst_null();
                        builder.astore(slot_httpClient);

                        builder.labelBinding(completeFuture);
                        builder.aload(slot_future);
                        builder.checkcast(CD_CompletableFuture);
                        state = 2;
                    }
                }
            }

            @Override
            public void atEnd(CodeBuilder builder) {
                if (slot_playerIp == -1) {
                    throw new IllegalStateException("Incompatible change in velocity InitialLoginSessionHandler - missing playerIp");
                }
                if (slot_httpClient == -1) {
                    throw new IllegalStateException("Incompatible change in velocity InitialLoginSessionHandler - missing httpClient");
                }
            }
        }))).andThen(ClassTransform.transformingMethodBodies(model -> model.methodName().equalsString("lambda$handle$4"), CodeTransform.ofStateful(() -> new CodeTransform() {
            private int slot_response = -1;
            private int slot_login = -1;
            private int slot_profile = -1;
            private Label label1;
            private Label label2;
            private int state = 0;

            @Override
            public void atStart(CodeBuilder builder) {
                label1 = builder.newLabel();
                label2 = builder.newLabel();
            }

            @Override
            public void accept(CodeBuilder builder, CodeElement element) {
                if (element instanceof LocalVariable l) {
                    if (slot_response == -1) {
                        if (l.typeSymbol().equals(CD_HttpResponse)) {
                            slot_response = l.slot();
                        }
                    }
                    if (slot_login == -1) {
                        if (l.typeSymbol().equals(CD_ServerLoginPacket)) {
                            slot_login = l.slot();
                        }
                    }
                    if (slot_profile == -1) {
                        if (l.typeSymbol().equals(CD_GameProfile)) {
                            slot_profile = l.slot();
                        }
                    }
                }
                if (state == 0) {
                    if (element instanceof LoadInstruction l && l.slot() == slot_response) {
                        builder.aload(slot_response);
                        builder.if_null(label1);
                        state = 1;
                    }
                    builder.with(element);
                } else if (state == 1) {
                    builder.with(element);
                    if (element instanceof BranchInstruction b && b.opcode() == Opcode.IF_ICMPNE) {
                        builder.labelBinding(label1);
                        state = 2;
                    }
                } else if (state == 2) {
                    if (element instanceof FieldInstruction f && f.opcode() == Opcode.GETSTATIC) {
                        var end = builder.newLabel();
                        builder.aload(slot_response);
                        builder.if_nonnull(end);

                        builder.aload(slot_login);
                        builder.invokevirtual(CD_ServerLoginPacket, "getUsername", MTD_getUsername);
                        builder.invokestatic(CD_GameProfile, "forOfflinePlayer", MTD_forOfflinePlayer);

                        builder.goto_(label2);
                        builder.labelBinding(end);
                        state = 3;
                    }
                    builder.with(element);
                } else if (state == 3) {
                    if (element instanceof StoreInstruction s && s.opcode() == Opcode.ASTORE && s.slot() == slot_profile) {
                        builder.labelBinding(label2);
                        state = 4;
                    }
                    builder.with(element);
                } else {
                    builder.with(element);
                }
            }

            @Override
            public void atEnd(CodeBuilder builder) {
                if (slot_profile == -1 || slot_login == -1 || slot_response == -1) throw new IllegalStateException();
            }
        })));
    }

    private static void isOnline(CodeBuilder builder) {
        builder.aload(0);
        builder.getfield(CD_InitialLoginSessionHandler, "server", CD_VelocityServer);
        builder.invokevirtual(CD_VelocityServer, "getConfiguration", MTD_getConfiguration);
        builder.invokevirtual(CD_VelocityConfiguration, "isOnlineMode", MTD_isOnlineMode);
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        if (CNI_InitialLoginSessionHandler.equals(internalClassName)) return TransformWillingness.ACCEPT_ONCE;
        return TransformWillingness.REJECT;
    }
}
