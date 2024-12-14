/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.wrapper.transformer.minestom;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.constant.ConstantDescs.*;

import java.lang.classfile.ClassTransform;
import java.lang.classfile.TypeKind;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

import eu.cloudnetservice.wrapper.transform.ClassTransformer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ObjectSetImplTagTransformer implements ClassTransformer {
    private static final String CNI_Tag = "net/minestom/server/registry/ObjectSetImpl$Tag";
    private static final ClassDesc CD_Tag = ClassDesc.ofInternalName(CNI_Tag);
    private static final ClassDesc CD_Objects = ClassDesc.ofInternalName("java/util/Objects");
    private static final ClassDesc CD_BasicType = ClassDesc.ofInternalName("net/minestom/server/gamedata/tags/Tag$BasicType");
    private static final MethodTypeDesc MTD_Objects_equals = MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object);
    private static final MethodTypeDesc MTD_equals = MethodTypeDesc.of(CD_boolean, CD_Object);
    private static final MethodTypeDesc MTD_hashCode = MethodTypeDesc.of(CD_int);
    private static final MethodTypeDesc MTD_hash = MethodTypeDesc.of(CD_int, CD_Object.arrayType());

    @Override
    public @NotNull ClassTransform provideClassTransform() {
        return ClassTransform.endHandler(classBuilder -> {
            classBuilder.withMethod("equals", MTD_equals, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
                //  if (!(o instanceof Tag<?> tag)) return false;
                //  return tagType == tag.tagType && Objects.equals(name, tag.name);

                // if(!(o instanceof Tag<?> tag)) return false
                var tag = builder.allocateLocal(TypeKind.ReferenceType);
                var next = builder.newLabel();
                var end = builder.endLabel();
                var fail = builder.newLabel();
                builder.localVariable(tag, "tag", CD_Tag, next, end);
                builder.aload(1).instanceOf(CD_Tag).ifne(next);
                builder.labelBinding(fail);
                builder.iconst_0().ireturn();
                builder.labelBinding(next);
                builder.aload(1).checkcast(CD_Tag).astore(tag);

                // tagType == tag.tagType && ...
                builder.aload(0).getfield(CD_Tag, "tagType", CD_BasicType);
                builder.aload(tag).getfield(CD_Tag, "tagType", CD_BasicType);
                builder.if_acmpne(fail);
                // Objects.equals(name, tag.name);
                builder.aload(0).getfield(CD_Tag, "name", CD_String);
                builder.aload(tag).getfield(CD_Tag, "name", CD_String);
                builder.invokestatic(CD_Objects, "equals", MTD_Objects_equals);
                // return ...
                builder.ireturn();
            }));
            classBuilder.withMethod("hashCode", MTD_hashCode, ACC_PUBLIC, methodBuilder -> methodBuilder.withCode(builder -> {
                // var a = new Object[2];
                builder.iconst_2().anewarray(CD_Object);
                // a[0] = this.tagType;
                builder.dup().iconst_0().aload(0).getfield(CD_Tag, "tagType", CD_BasicType).aastore();
                // a[1] = this.name;
                builder.dup().iconst_1().aload(0).getfield(CD_Tag, "name", CD_String).aastore();
                // return Objects.hash(a);
                builder.invokestatic(CD_Objects, "hash", MTD_hash).ireturn();
            }));
        });
    }

    @Override
    public @NotNull TransformWillingness classTransformWillingness(@NotNull String internalClassName) {
        return CNI_Tag.equals(internalClassName) ? TransformWillingness.ACCEPT_ONCE : TransformWillingness.REJECT;
    }
}
