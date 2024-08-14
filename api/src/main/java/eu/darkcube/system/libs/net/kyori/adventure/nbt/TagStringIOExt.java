/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.IOException;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * From minestom
 */
public class TagStringIOExt {
    public static @NotNull String writeTag(@NotNull BinaryTag tag) {
        return writeTag(tag, "");
    }

    public static @NotNull String writeTag(@NotNull BinaryTag input, @NotNull String indent) {
        final var sb = new StringBuilder();
        try (final var emit = new TagStringWriter(sb, indent)) {
            emit.writeTag(input);
        } catch (IOException e) {
            // The IOException comes from Writer#close(), but we are passing a StringBuilder which
            // is not a writer and does not need to be closed so will not throw.
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static @NotNull BinaryTag readTag(@NotNull String input) throws IOException {
        try {
            final var buffer = new CharBuffer(input);
            final var parser = new TagStringReader(buffer);
            final var tag = parser.tag();
            if (buffer.skipWhitespace().hasMore()) {
                throw new IOException("Document had trailing content after first tag");
            }
            return tag;
        } catch (final StringTagParseException ex) {
            throw new IOException(ex);
        }
    }
}
