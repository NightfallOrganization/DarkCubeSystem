/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.adventure;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;

public class AdventureUtils {
    private static final BossBar.Color[] A_COLOR = BossBar.Color.values();
    private static final BossBar.Overlay[] A_OVERLAY = BossBar.Overlay.values();
    private static final BossBar.Flag[] A_FLAG = BossBar.Flag.values();
    private static final Sound.Source[] A_SOURCE = Sound.Source.values();
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private static final MessageType[] A_MESSAGE_TYPE = MessageType.values();

    public static Component convert(eu.darkcube.system.libs.net.kyori.adventure.text.Component component) {
        return GsonComponentSerializer.gson().deserialize(eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component));
    }

    public static eu.darkcube.system.libs.net.kyori.adventure.text.Component convert(Component component) {
        return eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(GsonComponentSerializer.gson().serialize(component));
    }

    public static Title.Times convert(eu.darkcube.system.libs.net.kyori.adventure.title.Title.Times times) {
        return Title.Times.times(times.fadeIn(), times.stay(), times.fadeOut());
    }

    public static BossBar convert(eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar bossBar) {
        return BossBar.bossBar(convert(bossBar.name()), bossBar.progress(), convert(bossBar.color()), convert(bossBar.overlay()), convertBossBarFlags(bossBar.flags()));
    }

    public static BossBar.Color convert(eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color color) {
        return A_COLOR[color.ordinal()];
    }

    public static BossBar.Overlay convert(eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay overlay) {
        return A_OVERLAY[overlay.ordinal()];
    }

    public static ChatType.Bound convert(eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.Bound bound) {
        return convert(bound.type()).bind(convert(bound.name()), convert(bound.target()));
    }

    public static Identity convert(eu.darkcube.system.libs.net.kyori.adventure.identity.Identity identity) {
        return Identity.identity(identity.uuid());
    }

    public static SignedMessage convert(eu.darkcube.system.libs.net.kyori.adventure.chat.SignedMessage signedMessage) {
        return SignedMessage.system(signedMessage.message(), convert(signedMessage.unsignedContent()));
    }

    public static ChatType convert(eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType chatType) {
        if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.CHAT) {
            return ChatType.CHAT;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.EMOTE_COMMAND) {
            return ChatType.EMOTE_COMMAND;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.MSG_COMMAND_INCOMING) {
            return ChatType.MSG_COMMAND_INCOMING;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.MSG_COMMAND_OUTGOING) {
            return ChatType.MSG_COMMAND_OUTGOING;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_INCOMING) {
            return ChatType.TEAM_MSG_COMMAND_INCOMING;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.TEAM_MSG_COMMAND_OUTGOING) {
            return ChatType.TEAM_MSG_COMMAND_OUTGOING;
        } else if (chatType == eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType.SAY_COMMAND) {
            return ChatType.SAY_COMMAND;
        }
        return ChatType.chatType(convert(chatType.key()));
    }

    public static BossBar.@Nullable Flag convert(eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag flag) {
        if (flag == null) return null;
        return A_FLAG[flag.ordinal()];
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static MessageType convert(eu.darkcube.system.libs.net.kyori.adventure.audience.MessageType messageType) {
        return A_MESSAGE_TYPE[messageType.ordinal()];
    }

    public static Book convert(eu.darkcube.system.libs.net.kyori.adventure.inventory.Book book) {
        return Book.book(convert(book.title()), convert(book.author()), convertComponents(book.pages()));
    }

    @Contract("!null -> !null, null -> null")
    public static @Nullable Sound.Source convert(eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source source) {
        if (source == null) return null;
        return A_SOURCE[source.ordinal()];
    }

    public static SoundStop convert(eu.darkcube.system.libs.net.kyori.adventure.sound.SoundStop stop) {
        var sound = convert(stop.sound());
        var source = convert(stop.source());
        if (sound != null && source != null) return SoundStop.namedOnSource(sound, source);
        if (sound != null) return SoundStop.named(sound);
        if (source != null) return SoundStop.source(source);
        return SoundStop.all();
    }

    @SuppressWarnings("PatternValidation")
    public static @Nullable Key convert(eu.darkcube.system.libs.net.kyori.adventure.key.Key key) {
        if (key == null) return null;
        return Key.key(key.namespace(), key.value());
    }

    public static @Nullable Sound convert(eu.darkcube.system.libs.net.kyori.adventure.sound.Sound sound) {
        if (sound == null) return null;
        return Sound.sound(convert(sound.name()), Objects.requireNonNull(convert(sound.source())), sound.volume(), sound.pitch());
    }

    public static List<Component> convertComponents(List<eu.darkcube.system.libs.net.kyori.adventure.text.Component> components) {
        return components.stream().map(AdventureUtils::convert).toList();
    }

    public static List<eu.darkcube.system.libs.net.kyori.adventure.text.Component> convertComponentsBack(List<Component> components) {
        return components.stream().map(AdventureUtils::convert).toList();
    }

    public static Set<BossBar.Flag> convertBossBarFlags(Set<eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag> flags) {
        return flags.stream().map(AdventureUtils::convert).collect(Collectors.toSet());
    }

}
