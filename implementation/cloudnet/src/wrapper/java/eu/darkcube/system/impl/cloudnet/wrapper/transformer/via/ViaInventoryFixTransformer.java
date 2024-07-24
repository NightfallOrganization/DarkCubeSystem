package eu.darkcube.system.impl.cloudnet.wrapper.transformer.via;

import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.TypeKind;
import java.lang.classfile.instruction.LocalVariable;
import java.lang.classfile.instruction.ReturnInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ViaInventoryFixTransformer implements ClassTransformer {
    private static final String CNI_Protocol1_17_1To1_17 = "com/viaversion/viabackwards/protocol/v1_17_1to1_17/Protocol1_17_1To1_17";
    private static final ClassDesc CD_ClientboundPackets1_17 = ClassDesc.ofInternalName("com/viaversion/viaversion/protocols/v1_16_4to1_17/packet/ClientboundPackets1_17");
    private static final ClassDesc CD_PacketWrapper = ClassDesc.ofInternalName("com/viaversion/viaversion/api/protocol/packet/PacketWrapper");
    private static final ClassDesc CD_PacketType = ClassDesc.ofInternalName("com/viaversion/viaversion/api/protocol/packet/PacketType");
    private static final ClassDesc CD_Types = ClassDesc.ofInternalName("com/viaversion/viaversion/api/type/Types");
    private static final ClassDesc CD_Type = ClassDesc.ofInternalName("com/viaversion/viaversion/api/type/Type");
    private static final ClassDesc CD_UnsignedByteType = ClassDesc.ofInternalName("com/viaversion/viaversion/api/type/types/UnsignedByteType");
    private static final ClassDesc CD_ShortType = ClassDesc.ofInternalName("com/viaversion/viaversion/api/type/types/ShortType");
    private static final ClassDesc CD_Protocol1_17_1To1_17 = ClassDesc.ofInternalName(CNI_Protocol1_17_1To1_17);

    private static final MethodTypeDesc MTD_PacketWrapper_create = MethodTypeDesc.of(CD_PacketWrapper, CD_PacketType);
    private static final MethodTypeDesc MTD_Short_valueOf = MethodTypeDesc.of(CD_Short, CD_short);
    private static final MethodTypeDesc MTD_PacketWrapper_write = MethodTypeDesc.of(CD_void, CD_Type, CD_Object);
    private static final MethodTypeDesc MTD_PacketWrapper_send = MethodTypeDesc.of(CD_void, CD_Class);

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return ClassTransform.transformingMethodBodies(mm -> mm.methodName().equalsString("lambda$registerPackets$3"), CodeTransform.ofStateful(ViaInventoryFixTransformer::transform));
    }

    private static CodeTransform transform() {
        return new CodeTransform() {
            private int carried = -1;

            @Override
            public void accept(CodeBuilder builder, CodeElement element) {
                if (element instanceof LocalVariable localVariable) {
                    if (localVariable.name().equalsString("carried")) {
                        carried = localVariable.slot();
                    }
                }
                if (element instanceof ReturnInstruction) {
                    var setSlotPacket = builder.allocateLocal(TypeKind.ReferenceType);

                    builder.newBoundLabel();
                    builder.lineNumber(0);

                    // PacketWrapper setSlotPacket = wrapper.create(ClientboundPackets1_17.CONTAINER_SET_SLOT);
                    builder.aload(0);
                    builder.getstatic(CD_ClientboundPackets1_17, "CONTAINER_SET_SLOT", CD_ClientboundPackets1_17);
                    builder.invokeinterface(CD_PacketWrapper, "create", MTD_PacketWrapper_create);
                    builder.astore(setSlotPacket);

                    // setSlotPacket.write(Types.BYTE, (byte) -1);
                    builder.aload(setSlotPacket);
                    builder.getstatic(CD_Types, "UNSIGNED_BYTE", CD_UnsignedByteType);
                    builder.iconst_m1();
                    builder.invokestatic(CD_Short, "valueOf", MTD_Short_valueOf);
                    builder.invokeinterface(CD_PacketWrapper, "write", MTD_PacketWrapper_write);

                    // setSlotPacket.write(Types.SHORT, (short) -1);
                    builder.aload(setSlotPacket);
                    builder.getstatic(CD_Types, "SHORT", CD_ShortType);
                    builder.iconst_m1();
                    builder.invokestatic(CD_Short, "valueOf", MTD_Short_valueOf);
                    builder.invokeinterface(CD_PacketWrapper, "write", MTD_PacketWrapper_write);

                    // setSlotPacket.write(Types.ITEM1_13_2, carried);
                    builder.aload(setSlotPacket);
                    builder.getstatic(CD_Types, "ITEM1_13_2", CD_Type);
                    builder.aload(carried);
                    builder.invokeinterface(CD_PacketWrapper, "write", MTD_PacketWrapper_write);

                    // setSlotPacket.send(Protocol1_17_1To1_17.class);
                    builder.aload(setSlotPacket);
                    builder.ldc(CD_Protocol1_17_1To1_17);
                    builder.invokeinterface(CD_PacketWrapper, "send", MTD_PacketWrapper_send);
                }
                builder.with(element);
            }
        };
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        var isProtocol = internalClassName.equals(CNI_Protocol1_17_1To1_17);
        return isProtocol ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
