/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Formattable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentLike;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public enum Language {

    GERMAN(Locale.GERMAN),
    ENGLISH(Locale.ENGLISH);

    public static final Language DEFAULT = Language.GERMAN;
    private final Locale locale;
    private final Bundle bundle;

    Language(Locale locale) {
        this.locale = locale;
        this.bundle = new Bundle();
    }

    @Api
    public static void validateEntries(String[] entrySet, Function<String, String> keyModifier) {
        for (var language : Language.values()) {
            language.validate(entrySet, keyModifier);
        }
    }

    @Api
    public static InputStream getResource(ClassLoader loader, String path) {
        return loader.getResourceAsStream(path);
    }

    @Api
    public static Reader getReader(InputStream stream) {
        return new InputStreamReader(stream, Language.getCharset());
    }

    @Api
    public static Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Api
    public static Language fromString(String language) {
        for (var l : Language.values()) {
            if (l.name().equalsIgnoreCase(language)) {
                return l;
            }
        }
        return GERMAN;
    }

    @Api
    public static Style lastStyle(Component c) {
        return c.children().isEmpty() ? c.style() : c.children().getLast().style();
    }

    @Api
    public boolean containsMessage(String key) {
        return bundle.containsKey(key);
    }

    @Api
    public Component getMessage(String key, Object... replacements) {
        replacements = Arrays.copyOf(replacements, replacements.length);
        List<Component> components = new ArrayList<>();
        for (var i = 0; i < replacements.length; i++) {
            if (replacements[i] instanceof BaseMessage) {
                replacements[i] = ((BaseMessage) replacements[i]).getMessage(this);
            }
            if (replacements[i] instanceof ComponentLike componentLike) {
                replacements[i] = (Formattable) (formatter, flags, width, precision) -> {
                    var index = components.size();
                    components.add(componentLike.asComponent());
                    formatter.format("&#!$%s%s;", (char) 1054, index);
                };
            }
        }
        if (this.bundle.containsKey(key)) {
            var formatted = String.format(this.locale, this.bundle.getObject(key).toString(), replacements);
            formatted = ChatColorUtil.translateAlternateColorCodes('&', formatted);
            Component c = Component.empty();
            for (var i = 0; i < components.size(); i++) {
                var s = formatted.split(String.format("&#!\\$%s%s;", (char) 1054, i), 2);
                c = c.append(LegacyComponentSerializer.legacySection().deserialize(s[0]));
                var o = c;
                if (s.length == 2) {
                    formatted = s[1];
                    c = c.append(components.get(i));
                    var str = LegacyComponentSerializer.legacySection().serialize(Component.text(" ").style(lastStyle(o)));
                    str = str.replace(" ", "");

                    formatted = str + formatted;
                    // c = c.append(LegacyComponentSerializer.legacySection()
                    //.deserialize(formatted));
                } else {
                    break;
                }
            }
            if (components.isEmpty()) {
                c = LegacyComponentSerializer.legacySection().deserialize(formatted);
            } else {
                var str = LegacyComponentSerializer.legacySection().serialize(Component.text(" ").style(lastStyle(c)));
                str = str.replace(" ", "");

                formatted = str + formatted;
                c = c.append(LegacyComponentSerializer.legacySection().deserialize(formatted));
            }
            return c;
        } else {
            return LegacyComponentSerializer.legacySection().deserialize(key + '[' + String.join(", ", Arrays.stream(replacements).map(String::valueOf).toArray(String[]::new)) + ']');
        }
    }

    @Api
    public void validate(String[] entrySet, Function<String, String> keyModifier) {
        for (var key : entrySet) {
            var mapped = keyModifier.apply(key);
            if (!this.bundle.containsKey(mapped)) {
                System.out.println("Missing translation for language " + this + ": " + mapped);
            }
        }
    }

    @Api
    public void registerLookup(Map<String, Object> lookup, Function<String, String> keyModifier) {
        this.bundle.append(lookup, keyModifier);
    }

    @Api
    public void registerLookup(Properties properties, Function<String, String> keyModifier) {
        this.bundle.append(properties, keyModifier);
    }

    @Api
    public void registerLookup(Reader reader, Function<String, String> keyModifier) throws IOException {
        var properties = new Properties();
        properties.load(reader);
        this.registerLookup(properties, keyModifier);
    }

    @Api
    public void registerLookup(ClassLoader loader, String path, Function<String, String> keyModifier) throws IOException {
        this.registerLookup(Language.getReader(Language.getResource(loader, path)), keyModifier);
    }

    @Api
    public void registerLookup(ClassLoader loader, String path) throws IOException {
        this.registerLookup(loader, path, s -> s);
    }

    @ApiStatus.Experimental
    public Locale getLocale() {
        return this.locale;
    }

    private static class Bundle extends ResourceBundle {

        private Map<String, Object> lookup = new HashMap<>();

        public void append(Map<String, Object> lookup, Function<String, String> keyModifier) {
            for (var key : lookup.keySet()) {
                if (this.lookup.containsKey(keyModifier.apply(key))) {
                    System.out.println("[LanguageAPI] Overriding translation: " + keyModifier.apply(key));
                }
                this.lookup.put(keyModifier.apply(key), lookup.get(key));
            }
        }

        public void append(Properties properties, Function<String, String> keyModifier) {
            Map<String, Object> m = new HashMap<>();
            for (var s : properties.entrySet()) {
                m.put(s.getKey().toString(), s.getValue());
            }
            this.append(m, keyModifier);
        }

        @Override
        protected Object handleGetObject(@NotNull String key) {
            return this.lookup.get(key);
        }

        @Override
        public @NotNull Enumeration<String> getKeys() {
            var parent = this.parent;
            return new ResourceBundleEnumeration(this.lookup.keySet(), (parent != null) ? parent.getKeys() : null);
        }

        @Override
        public @NotNull Set<String> handleKeySet() {
            return this.lookup.keySet();
        }

        public static class ResourceBundleEnumeration implements Enumeration<String> {

            Set<String> set;
            Iterator<String> iterator;
            Enumeration<String> enumeration; // may remain null
            String next = null;

            /**
             * Constructs a resource bundle enumeration.
             *
             * @param set         a set providing some elements of the enumeration
             * @param enumeration an enumeration providing more elements of the enumeration.
             *                    enumeration may be null.
             */
            public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
                this.set = set;
                this.iterator = set.iterator();
                this.enumeration = enumeration;
            }

            @Override
            public boolean hasMoreElements() {
                if (this.next == null) {
                    if (this.iterator.hasNext()) {
                        this.next = this.iterator.next();
                    } else if (this.enumeration != null) {
                        while (this.next == null && this.enumeration.hasMoreElements()) {
                            this.next = this.enumeration.nextElement();
                            if (this.set.contains(this.next)) {
                                this.next = null;
                            }
                        }
                    }
                }
                return this.next != null;
            }

            @Override
            public String nextElement() {
                if (this.hasMoreElements()) {
                    var result = this.next;
                    this.next = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
        }
    }
}
