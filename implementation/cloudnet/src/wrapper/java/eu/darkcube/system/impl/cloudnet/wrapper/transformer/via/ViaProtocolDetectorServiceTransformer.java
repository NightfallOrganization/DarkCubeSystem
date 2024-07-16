package eu.darkcube.system.impl.cloudnet.wrapper.transformer.via;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassTransform;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodSignature;
import java.lang.classfile.attribute.SignatureAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ViaProtocolDetectorServiceTransformer implements ClassTransformer {
    public static final String CNI_ProtocolDetectorService = "com/viaversion/viaversion/api/platform/ProtocolDetectorService";
    public static final ClassDesc CD_ProtocolDetectorService = ClassDesc.ofInternalName(CNI_ProtocolDetectorService);
    public static final ClassDesc CD_ProtocolVersion = ClassDesc.of("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
    public static final ClassDesc CD_UserConnection = ClassDesc.of("com.viaversion.viaversion.api.connection.UserConnection");
    public static final MethodTypeDesc MTD_serverProtocolVersions = MethodTypeDesc.of(CD_ProtocolVersion.arrayType(), CD_String);
    public static final MethodTypeDesc MTD_serverProtocolVersion = MethodTypeDesc.of(CD_ProtocolVersion, CD_String, CD_UserConnection);
    public static final MethodTypeDesc MTD_setProtocolVersions = MethodTypeDesc.of(CD_void, CD_String, CD_int.arrayType());
    public static final MethodTypeDesc MTD_detectedProtocolVersions = MethodTypeDesc.of(CD_Map);
    private static final MethodSignature MS_detectedProtocolVersions = MethodSignature.parseFrom("()Ljava/util/Map<Ljava/lang/String;[Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;>;");

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return (builder, element) -> {
            if (element instanceof MethodModel method && method.methodName().equalsString("detectedProtocolVersions") && !method.methodTypeSymbol().returnType().equals(CD_Map)) {
                builder.withMethod("serverProtocolVersions", MTD_serverProtocolVersions, ACC_PUBLIC | ACC_ABSTRACT, _ -> {
                });
                builder.withMethod("serverProtocolVersion", MTD_serverProtocolVersion, ACC_PUBLIC | ACC_ABSTRACT, _ -> {
                });
                builder.withMethod("setProtocolVersions", MTD_setProtocolVersions, ACC_PUBLIC | ACC_ABSTRACT | ACC_VARARGS, _ -> {
                });

                builder.withMethod("detectedProtocolVersions", MTD_detectedProtocolVersions, ACC_PUBLIC | ACC_ABSTRACT, b -> {
                    b.with(SignatureAttribute.of(MS_detectedProtocolVersions));
                });

                builder.with(element);
            } else {
                builder.with(element);
            }
        };
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        var isDetectorService = internalClassName.equals(CNI_ProtocolDetectorService);
        return isDetectorService ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
