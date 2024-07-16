package eu.darkcube.system.impl.cloudnet.wrapper.transformer.via;

import static eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaProtocolDetectorServiceTransformer.CD_ProtocolDetectorService;
import static eu.darkcube.system.impl.cloudnet.wrapper.transformer.via.ViaProtocolDetectorServiceTransformer.MTD_serverProtocolVersion;

import java.lang.classfile.ClassTransform;
import java.lang.classfile.instruction.InvokeInstruction;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ViaVelocityVersionProviderTransformer implements ClassTransformer {
    private static final String CNI_VelocityVersionProvider = "com/viaversion/viaversion/velocity/providers/VelocityVersionProvider";

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return ClassTransform.transformingMethodBodies(mm -> mm.methodName().equalsString("getBackProtocol"), (builder, element) -> {
            if (element instanceof InvokeInstruction i && i.name().equalsString("serverProtocolVersion")) {
                var user = builder.parameterSlot(0);
                builder.aload(user);
                builder.invokeinterface(CD_ProtocolDetectorService, "serverProtocolVersion", MTD_serverProtocolVersion);
            } else {
                builder.with(element);
            }
        });
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        var isVersionProvider = internalClassName.equals(CNI_VelocityVersionProvider);
        return isVersionProvider ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
