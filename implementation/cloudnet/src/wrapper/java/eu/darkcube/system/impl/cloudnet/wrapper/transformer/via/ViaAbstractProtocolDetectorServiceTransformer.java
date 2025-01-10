/*
 * Copyright (c) 2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer.via;

import static eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaProtocolDetectorServiceTransformer.*;
import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.*;
import static java.lang.reflect.AccessFlag.FINAL;
import static java.lang.reflect.AccessFlag.PROTECTED;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeModel;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.FieldModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodTransform;
import java.lang.classfile.Opcode;
import java.lang.classfile.Signature;
import java.lang.classfile.TypeKind;
import java.lang.classfile.attribute.SignatureAttribute;
import java.lang.classfile.instruction.FieldInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.NewObjectInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.IntStream;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ViaAbstractProtocolDetectorServiceTransformer implements ClassTransformer {
    private static final String CNI_AbstractProtocolDetectorService = "com/viaversion/viaversion/platform/AbstractProtocolDetectorService";
    private static final String CNI_Object2IntOpenHashMap = "com/viaversion/viaversion/libs/fastutil/objects/Object2IntOpenHashMap";
    private static final String CNI_ProtocolInfo = "com/viaversion/viaversion/api/connection/ProtocolInfo";
    private static final ClassDesc CD_ConcurrentHashMap = ClassDesc.of(ConcurrentHashMap.class.getName());
    private static final ClassDesc CD_AbstractProtocolDetectorService = ClassDesc.ofInternalName(CNI_AbstractProtocolDetectorService);
    private static final ClassDesc CD_ReadWriteLock = ClassDesc.of(ReadWriteLock.class.getName());
    private static final ClassDesc CD_Lock = ClassDesc.of(Lock.class.getName());
    private static final ClassDesc CD_ProtocolInfo = ClassDesc.ofInternalName(CNI_ProtocolInfo);
    private static final ClassDesc CD_Arrays = ClassDesc.of(Arrays.class.getName());
    private static final ClassDesc CD_IntStream = ClassDesc.of(IntStream.class.getName());
    private static final ClassDesc CD_HashMap = ClassDesc.of(HashMap.class.getName());

    private static final MethodTypeDesc MTD_lowestSupportedProtocolVersion = MethodTypeDesc.of(CD_ProtocolVersion);
    private static final MethodTypeDesc MTD_serverProtocolVersion_String = MethodTypeDesc.of(CD_ProtocolVersion, CD_String);
    private static final MethodTypeDesc MTD_setProtocolVersion = MethodTypeDesc.of(CD_void, CD_String, CD_int);
    private static final MethodTypeDesc MTD_setProtocolVersions = MethodTypeDesc.of(CD_void, CD_String, CD_int.arrayType());

    private static final MethodTypeDesc MTD_Map_get = MethodTypeDesc.of(CD_Object, CD_Object);
    private static final MethodTypeDesc MTD_Map_remove = MethodTypeDesc.of(CD_Object, CD_Object);
    private static final MethodTypeDesc MTD_Map_put = MethodTypeDesc.of(CD_Object, CD_Object, CD_Object);
    private static final MethodTypeDesc MTD_ReadWriteLock_readLock = MethodTypeDesc.of(CD_Lock);
    private static final MethodTypeDesc MTD_ReadWriteLock_writeLock = MethodTypeDesc.of(CD_Lock);
    private static final MethodTypeDesc MTD_Array_clone = MethodTypeDesc.of(CD_Object);
    private static final MethodTypeDesc MTD_configuredServers = MethodTypeDesc.of(CD_Map);
    private static final MethodTypeDesc MTD_Integer_intValue = MethodTypeDesc.of(CD_int);
    private static final MethodTypeDesc MTD_ProtocolVersion_getProtocol = MethodTypeDesc.of(CD_ProtocolVersion, CD_int);
    private static final MethodTypeDesc MTD_UserConnection_getProtocolInfo = MethodTypeDesc.of(CD_ProtocolInfo);
    private static final MethodTypeDesc MTD_ProtocolInfo_protocolVersion = MethodTypeDesc.of(CD_ProtocolVersion);
    private static final MethodTypeDesc MTD_Arrays_binarySearch = MethodTypeDesc.of(CD_int, CD_Object.arrayType(), CD_Object);
    private static final MethodTypeDesc MTD_Arrays_stream = MethodTypeDesc.of(CD_IntStream, CD_int.arrayType());
    private static final MethodTypeDesc MTD_IntStream_distinct = MethodTypeDesc.of(CD_IntStream);
    private static final MethodTypeDesc MTD_IntStream_sorted = MethodTypeDesc.of(CD_IntStream);
    private static final MethodTypeDesc MTD_IntStream_toArray = MethodTypeDesc.of(CD_int.arrayType());
    private static final MethodTypeDesc MTD_ProtocolVersion_getVersion = MethodTypeDesc.of(CD_int);
    private static final MethodTypeDesc MTD_HashMap_init = MethodTypeDesc.of(CD_void, CD_Map);

    private static final Signature S_detectedProtocolIds = Signature.parseFrom("Ljava/util/Map<Ljava/lang/String;[Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;>;");

    private static final CodeTransform CT_init = (builder, element) -> {
        switch (element) {
            case NewObjectInstruction i when i.className().asInternalName().equals(CNI_Object2IntOpenHashMap) -> builder.new_(CD_ConcurrentHashMap);
            case InvokeInstruction i when i.owner().asInternalName().equals(CNI_Object2IntOpenHashMap) -> builder.invokespecial(CD_ConcurrentHashMap, "<init>", MTD_void);
            case FieldInstruction i when i.name().equalsString("detectedProtocolIds") -> builder.putfield(i.owner().asSymbol(), i.name().stringValue(), CD_Map);
            case FieldInstruction i when i.name().equalsString("lock") -> {
                builder.with(element);
                builder.return_();
            }
            case null, default -> builder.with(element);
        }
    };
    private static final MethodTransform MT_init = MethodTransform.transformingCode(CT_init);

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return (builder, element) -> {
            if (element instanceof MethodModel method) {
                if (method.methodName().equalsString("<init>")) {
                    builder.transformMethod(method, MT_init);
                    add_serverProtocolVersions(builder);
                    return;
                } else if (method.methodName().equalsString("serverProtocolVersion") && method.methodTypeSymbol().equals(MTD_serverProtocolVersion_String)) {
                    add_serverProtocolVersion_String(builder);
                    add_serverProtocolVersion_String_UserConnection(builder);
                    return;
                } else if (method.methodName().equalsString("setProtocolVersion")) {
                    add_setProtocolVersion(builder);
                    add_setProtocolVersions(builder);
                    return;
                } else if (method.methodName().equalsString("uncacheProtocolVersion")) {
                    transform_uncacheProtocolVersion(builder, method);
                    return;
                } else if (method.methodName().equalsString("detectedProtocolVersions")) {
                    builder.withMethod("detectedProtocolVersions", MTD_detectedProtocolVersions, ACC_PUBLIC, methodBuilder -> {
                        for (var methodElement : method) {
                            if (methodElement instanceof CodeModel code) {
                                methodBuilder.transformCode(code, (b, e) -> {
                                    switch (e) {
                                        case NewObjectInstruction _ -> b.new_(CD_HashMap);
                                        case FieldInstruction i when i.name().equalsString("detectedProtocolIds") -> b.getfield(CD_AbstractProtocolDetectorService, "detectedProtocolIds", CD_Map);
                                        case InvokeInstruction i when i.name().equalsString("<init>") -> b.invokespecial(CD_HashMap, "<init>", MTD_HashMap_init);
                                        case null, default -> b.with(e);
                                    }
                                });
                            } else {
                                methodBuilder.with(methodElement);
                            }
                        }
                    });
                    return;
                }
            } else if (element instanceof FieldModel field) {
                if (field.fieldName().equalsString("detectedProtocolIds")) {
                    builder.withField("detectedProtocolIds", CD_Map, b -> {
                        b.withFlags(PROTECTED, FINAL);
                        b.with(SignatureAttribute.of(S_detectedProtocolIds));
                    });
                    return;
                }
            }
            builder.with(element);
        };
    }

    private void transform_uncacheProtocolVersion(ClassBuilder classBuilder, MethodModel method) {
        classBuilder.transformMethod(method, MethodTransform.transformingCode(CodeTransform.ofStateful(TransformUncacheProtocolVersion::new)));
    }

    private static final class TransformUncacheProtocolVersion implements CodeTransform {
        private static final int WAIT_1 = 0;
        private static final int WAIT_2 = WAIT_1 + 1;
        private static final int DONE = WAIT_2 + 1;
        private int versions;
        private int state = WAIT_1;

        @Override
        public void atStart(CodeBuilder builder) {
            versions = builder.allocateLocal(TypeKind.ReferenceType);
        }

        @Override
        public void accept(CodeBuilder builder, CodeElement element) {
            if (state == WAIT_1) {
                if (element instanceof FieldInstruction i && i.opcode() == Opcode.GETFIELD && i.name().equalsString("detectedProtocolIds")) {
                    builder.getfield(CD_AbstractProtocolDetectorService, "detectedProtocolIds", CD_Map);
                    state = WAIT_2;
                    return;
                }
            } else if (state == WAIT_2) {
                if (element instanceof InvokeInstruction i && i.name().equalsString("removeInt")) {
                    builder.invokeinterface(CD_Map, "remove", MTD_Map_remove);
                    builder.checkcast(CD_ProtocolVersion.arrayType());
                    builder.astore(versions);

                    builder.aload(versions);
                    builder.ifThenElse(Opcode.IFNONNULL, b -> {
                        b.aload(versions);
                        b.iconst_0();
                        b.aaload();
                        b.invokevirtual(CD_ProtocolVersion, "getVersion", MTD_ProtocolVersion_getVersion);
                    }, CodeBuilder::iconst_m1);

                    state = DONE;
                    return;
                }
            } else if (state != DONE) {
                throw new IllegalStateException("State: " + state);
            }
            builder.with(element);
        }
    }

    private void add_serverProtocolVersion_String(ClassBuilder classBuilder) {
        classBuilder.withMethod("serverProtocolVersion", MTD_serverProtocolVersion_String, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
            var serverName = builder.parameterSlot(0);
            var versions = builder.allocateLocal(TypeKind.ReferenceType);

            // ProtocolVersion[] versions = this.serverProtocolVersions(serverName);
            builder.aload(0);
            builder.aload(serverName);
            builder.invokevirtual(CD_AbstractProtocolDetectorService, "serverProtocolVersions", MTD_serverProtocolVersions);
            builder.astore(versions);

            // return versions == null ? this.lowestSupportedProtocolVersion() : versions[0];
            builder.aload(versions);
            builder.ifThenElse(Opcode.IFNONNULL, b -> {
                b.aload(versions);
                b.iconst_0();
                b.aaload();
            }, b -> {
                b.aload(0);
                b.invokevirtual(CD_AbstractProtocolDetectorService, "lowestSupportedProtocolVersion", MTD_lowestSupportedProtocolVersion);
            });
            builder.areturn();
        }));
    }

    private void add_serverProtocolVersion_String_UserConnection(ClassBuilder classBuilder) {
        classBuilder.withMethod("serverProtocolVersion", MTD_serverProtocolVersion, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
            var serverName = builder.parameterSlot(0);
            var user = builder.parameterSlot(1);
            var versions = builder.allocateLocal(TypeKind.ReferenceType);
            var userVersion = builder.allocateLocal(TypeKind.ReferenceType);
            var idx = builder.allocateLocal(TypeKind.IntType);

            // ProtocolVersion[] versions = this.serverProtocolVersions(serverName);
            builder.aload(0);
            builder.aload(serverName);
            builder.invokevirtual(CD_AbstractProtocolDetectorService, "serverProtocolVersions", MTD_serverProtocolVersions);
            builder.astore(versions);

            // ProtocolVersion userVersion = user.getProtocolInfo().protocolVersion();
            builder.aload(user);
            builder.invokeinterface(CD_UserConnection, "getProtocolInfo", MTD_UserConnection_getProtocolInfo);
            builder.invokeinterface(CD_ProtocolInfo, "protocolVersion", MTD_ProtocolInfo_protocolVersion);
            builder.astore(userVersion);

            // int idx = Arrays.binarySearch(versions, userVersion);
            builder.aload(versions);
            builder.aload(userVersion);
            builder.invokestatic(CD_Arrays, "binarySearch", MTD_Arrays_binarySearch);
            builder.istore(idx);

            // if (idx >= 0) {
            //     return versions[idx];
            // }
            builder.iload(idx);
            builder.ifThen(Opcode.IFGE, b -> {
                b.aload(versions);
                b.iload(idx);
                b.aaload();
                b.areturn();
            });

            // idx = -idx - 1;
            builder.iload(idx);
            builder.ineg();
            builder.iconst_1();
            builder.isub();
            builder.istore(idx);

            // if (idx == 0) {
            //     return versions[0];
            // }
            builder.iload(idx);
            builder.ifThen(Opcode.IFEQ, b -> {
                b.aload(versions);
                b.iconst_0();
                b.aaload();
                b.areturn();
            });

            // return versions[idx - 1];
            builder.aload(versions);
            builder.iload(idx);
            builder.iconst_1();
            builder.isub();
            builder.aaload();
            builder.areturn();
        }));
    }

    private void add_serverProtocolVersions(ClassBuilder classBuilder) {
        classBuilder.withMethod("serverProtocolVersions", MTD_serverProtocolVersions, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
            var serverName = builder.parameterSlot(0);

            var detectedProtocols = builder.allocateLocal(TypeKind.ReferenceType);
            var servers = builder.allocateLocal(TypeKind.ReferenceType);
            var protocol = builder.allocateLocal(TypeKind.ReferenceType);
            var defaultProtocol = builder.allocateLocal(TypeKind.ReferenceType);

            // this.lock.readLock().lock();
            builder.aload(0);
            builder.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
            builder.invokeinterface(CD_ReadWriteLock, "readLock", MTD_ReadWriteLock_readLock);
            builder.invokeinterface(CD_Lock, "lock", MTD_void);

            // try {
            //     detectedProtocols = this.detectedProtocolIds.get(serverName);
            // finally {
            //     this.lock.readLock().unlock();
            // }
            var label = builder.newLabel();
            builder.trying(tryBuilder -> {
                tryBuilder.aload(0);
                tryBuilder.getfield(CD_AbstractProtocolDetectorService, "detectedProtocolIds", CD_Map);
                tryBuilder.aload(serverName);
                tryBuilder.invokeinterface(CD_Map, "get", MTD_Map_get);
                tryBuilder.checkcast(CD_ProtocolVersion.arrayType());
                tryBuilder.astore(detectedProtocols);
            }, catchBuilder -> catchBuilder.catchingAll(cb -> {
                cb.aload(0);
                cb.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
                cb.invokeinterface(CD_ReadWriteLock, "readLock", MTD_ReadWriteLock_readLock);
                cb.invokeinterface(CD_Lock, "unlock", MTD_void);
                cb.athrow();
            }));
            builder.aload(0);
            builder.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
            builder.invokeinterface(CD_ReadWriteLock, "readLock", MTD_ReadWriteLock_readLock);
            builder.invokeinterface(CD_Lock, "unlock", MTD_void);
            builder.labelBinding(label);

            // if (detectedProtocols != null) {
            //     return (ProtocolVersion[]) detectedProtocols.clone();
            // }
            builder.aload(detectedProtocols);
            builder.ifThen(Opcode.IFNONNULL, b -> {
                b.aload(detectedProtocols);
                b.invokevirtual(CD_ProtocolVersion.arrayType(), "clone", MTD_Array_clone);
                b.checkcast(CD_ProtocolVersion.arrayType());
                b.areturn();
            });

            // Map<String, Integer> servers = this.configuredServers();
            builder.aload(0);
            builder.invokevirtual(CD_AbstractProtocolDetectorService, "configuredServers", MTD_configuredServers);
            builder.astore(servers);

            // Integer protocol = servers.get(serverName);
            builder.aload(servers);
            builder.aload(serverName);
            builder.invokeinterface(CD_Map, "get", MTD_Map_get);
            builder.checkcast(CD_Integer);
            builder.astore(protocol);

            // if (protocol != null) {
            //     return new ProtocolVersion[] { ProtocolVersion.getProtocol(protocol) };
            // }
            builder.aload(protocol);
            builder.ifThen(Opcode.IFNONNULL, b -> {
                b.iconst_1();
                b.anewarray(CD_ProtocolVersion);
                b.dup();
                b.iconst_0();
                b.aload(protocol);
                b.invokevirtual(CD_Integer, "intValue", MTD_Integer_intValue);
                b.invokestatic(CD_ProtocolVersion, "getProtocol", MTD_ProtocolVersion_getProtocol);
                b.aastore();
                b.areturn();
            });

            // Integer defaultProtocol = servers.get("default");
            builder.aload(servers);
            builder.ldc("default");
            builder.invokeinterface(CD_Map, "get", MTD_Map_get);
            builder.checkcast(CD_Integer);
            builder.astore(defaultProtocol);

            // if (defaultProtocol != null) {
            //     return new ProtocolVersion[] { ProtocolVersion.getProtocol(defaultProtocol) };
            // }
            builder.aload(defaultProtocol);
            builder.ifThen(Opcode.IFNONNULL, b -> {
                b.iconst_1();
                b.anewarray(CD_ProtocolVersion);
                b.dup();
                b.iconst_0();
                b.aload(defaultProtocol);
                b.invokevirtual(CD_Integer, "intValue", MTD_Integer_intValue);
                b.invokestatic(CD_ProtocolVersion, "getProtocol", MTD_ProtocolVersion_getProtocol);
                b.aastore();
                b.areturn();
            });

            // return new ProtocolVersion[] { this.lowestSupportedProtocolVersion() };
            builder.iconst_1();
            builder.anewarray(CD_ProtocolVersion);
            builder.dup();
            builder.iconst_0();
            builder.aload(0);
            builder.invokevirtual(CD_AbstractProtocolDetectorService, "lowestSupportedProtocolVersion", MTD_lowestSupportedProtocolVersion);
            builder.aastore();
            builder.areturn();
        }));
    }

    private void add_setProtocolVersion(ClassBuilder classBuilder) {
        classBuilder.withMethod("setProtocolVersion", MTD_setProtocolVersion, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
            var serverName = builder.parameterSlot(0);
            var protocolVersion = builder.parameterSlot(1);

            builder.aload(0);
            builder.aload(serverName);
            builder.iconst_1();
            builder.newarray(TypeKind.IntType);
            builder.dup();
            builder.iconst_0();
            builder.iload(protocolVersion);
            builder.iastore();
            builder.invokevirtual(CD_AbstractProtocolDetectorService, "setProtocolVersions", MTD_setProtocolVersions);
            builder.return_();
        }));
    }

    private void add_setProtocolVersions(ClassBuilder classBuilder) {
        classBuilder.withMethodBody("setProtocolVersions", MTD_setProtocolVersions, ACC_PUBLIC, builder -> {
            var serverName = builder.parameterSlot(0);
            var protocolVersions = builder.parameterSlot(1);
            var array = builder.allocateLocal(TypeKind.ReferenceType);
            var versions = builder.allocateLocal(TypeKind.ReferenceType);

            // lock.writeLock().lock();
            builder.aload(0);
            builder.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
            builder.invokeinterface(CD_ReadWriteLock, "writeLock", MTD_ReadWriteLock_writeLock);
            builder.invokeinterface(CD_Lock, "lock", MTD_void);

            // try {
            //
            //
            //     detectedProtocolIds.put(serverName, versions);
            // } finally {
            //     lock.writeLock().unlock();
            // }
            var ex = builder.allocateLocal(TypeKind.ReferenceType);
            builder.trying(b -> {

                // int[] array = Arrays.stream(protocolVersions).distinct().sorted().toArray();
                b.aload(protocolVersions);
                b.invokestatic(CD_Arrays, "stream", MTD_Arrays_stream);
                b.invokeinterface(CD_IntStream, "distinct", MTD_IntStream_distinct);
                b.invokeinterface(CD_IntStream, "sorted", MTD_IntStream_sorted);
                b.invokeinterface(CD_IntStream, "toArray", MTD_IntStream_toArray);
                b.astore(array);

                // ProtocolVersion[] versions = new ProtocolVersion[array.length];
                b.aload(array);
                b.arraylength();
                b.anewarray(CD_ProtocolVersion);
                b.astore(versions);

                var i = b.allocateLocal(TypeKind.IntType);
                var labelEnd = b.newLabel();
                var labelStart = b.newLabel();
                // for (int i = 0; i < array.length; ++i) {
                //     versions[i] = ProtocolVersion.getProtocol(array[i]);
                // }
                b.iconst_0();
                b.istore(i);

                b.labelBinding(labelStart);

                b.iload(i);
                b.aload(array);
                b.arraylength();
                b.if_icmpge(labelEnd);

                b.aload(versions);
                b.iload(i);
                b.aload(array);
                b.iload(i);
                b.iaload();
                b.invokestatic(CD_ProtocolVersion, "getProtocol", MTD_ProtocolVersion_getProtocol);
                b.aastore();

                b.iinc(i, 1);
                b.goto_(labelStart);
                b.labelBinding(labelEnd);

                b.aload(0);
                b.getfield(CD_AbstractProtocolDetectorService, "detectedProtocolIds", CD_Map);
                b.aload(serverName);
                b.aload(versions);
                b.invokeinterface(CD_Map, "put", MTD_Map_put);
                b.pop();
            }, catchBuilder -> catchBuilder.catchingAll(b -> {
                b.astore(ex);
                b.aload(0);
                b.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
                b.invokeinterface(CD_ReadWriteLock, "writeLock", MTD_ReadWriteLock_writeLock);
                b.invokeinterface(CD_Lock, "unlock", MTD_void);
                b.aload(ex);
                b.athrow();
            }));

            builder.aload(0);
            builder.getfield(CD_AbstractProtocolDetectorService, "lock", CD_ReadWriteLock);
            builder.invokeinterface(CD_ReadWriteLock, "writeLock", MTD_ReadWriteLock_writeLock);
            builder.invokeinterface(CD_Lock, "unlock", MTD_void);

            builder.return_();
        });
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        var isDetectorService = internalClassName.equals(CNI_AbstractProtocolDetectorService);
        return isDetectorService ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
