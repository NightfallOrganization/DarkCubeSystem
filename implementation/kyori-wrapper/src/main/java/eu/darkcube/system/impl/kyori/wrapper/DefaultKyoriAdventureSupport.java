/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.kyori.wrapper;

import static eu.darkcube.system.impl.kyori.wrapper.Fields.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.audience.MessageType;
import eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar;
import eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType;
import eu.darkcube.system.libs.net.kyori.adventure.chat.SignedMessage;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.inventory.Book;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import eu.darkcube.system.libs.net.kyori.adventure.sound.SoundStop;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.title.Title;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface DefaultKyoriAdventureSupport extends KyoriAdventureSupport {
    @NotNull
    @Override
    default net.kyori.adventure.text.format.TextColor convert(@NotNull TextColor color) {
        return net.kyori.adventure.text.format.TextColor.color(color.red(), color.green(), color.blue());
    }

    @Override
    default @NotNull TextColor convert(@NotNull net.kyori.adventure.text.format.TextColor color) {
        return TextColor.color(color.red(), color.green(), color.blue());
    }

    @Override
    @NotNull
    default Set<net.kyori.adventure.bossbar.BossBar.Flag> convertBossBarFlagsC2P(@NotNull Set<BossBar.Flag> flags) {
        return flags.stream().map(this::convert).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @NotNull
    default Set<BossBar.Flag> convertBossBarFlagsP2C(@NotNull Set<net.kyori.adventure.bossbar.BossBar.Flag> flags) {
        return flags.stream().map(this::convert).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @NotNull
    default List<net.kyori.adventure.text.Component> convertComponentsC2P(@NotNull List<Component> components) {
        return components.stream().map(this::convert).toList();
    }

    @Override
    @NotNull
    default List<Component> convertComponentsP2C(@NotNull List<net.kyori.adventure.text.Component> components) {
        return components.stream().map(this::convert).toList();
    }

    @NotNull
    @Override
    default net.kyori.adventure.chat.SignedMessage.Signature convert(SignedMessage.@NotNull Signature signature) {
        return net.kyori.adventure.chat.SignedMessage.signature(signature.bytes());
    }

    @Override
    @NotNull
    default SignedMessage.Signature convert(net.kyori.adventure.chat.SignedMessage.@NotNull Signature signature) {
        return SignedMessage.signature(signature.bytes());
    }

    @NotNull
    @Override
    default net.kyori.adventure.inventory.Book convert(@NotNull Book book) {
        return net.kyori.adventure.inventory.Book.book(convert(book.title()), convert(book.author()), convertComponentsC2P(book.pages()));
    }

    @Override
    @NotNull
    default Book convert(net.kyori.adventure.inventory.@NotNull Book book) {
        return Book.book(convert(book.title()), convert(book.author()), convertComponentsP2C(book.pages()));
    }

    @NotNull
    @Override
    default net.kyori.adventure.key.Key convert(@NotNull Key key) {
        return net.kyori.adventure.key.Key.key(key.namespace(), key.value());
    }

    @Override
    @NotNull
    default Key convert(net.kyori.adventure.key.@NotNull Key key) {
        return Key.key(key.namespace(), key.value());
    }

    @NotNull
    @Override
    default net.kyori.adventure.sound.Sound convert(@NotNull Sound sound) {
        return net.kyori.adventure.sound.Sound.sound(convert(sound.name()), convert(sound.source()), sound.volume(), sound.pitch());
    }

    @Override
    @NotNull
    default Sound convert(net.kyori.adventure.sound.@NotNull Sound sound) {
        return Sound.sound(convert(sound.name()), convert(sound.source()), sound.volume(), sound.pitch());
    }

    @NotNull
    @Override
    default net.kyori.adventure.sound.SoundStop convert(@NotNull SoundStop soundStop) {
        @Nullable var oldSound = soundStop.sound();
        @Nullable var oldSource = soundStop.source();
        if (oldSound != null && oldSource != null) {
            return net.kyori.adventure.sound.SoundStop.namedOnSource(convert(oldSound), convert(oldSource));
        }
        if (oldSound != null) {
            return net.kyori.adventure.sound.SoundStop.named(convert(oldSound));
        }
        if (oldSource != null) {
            return net.kyori.adventure.sound.SoundStop.source(convert(oldSource));
        }
        return net.kyori.adventure.sound.SoundStop.all();
    }

    @Override
    @NotNull
    default SoundStop convert(net.kyori.adventure.sound.@NotNull SoundStop soundStop) {
        @Nullable var oldSound = soundStop.sound();
        @Nullable var oldSource = soundStop.source();
        if (oldSound != null && oldSource != null) {
            return SoundStop.namedOnSource(convert(oldSound), convert(oldSource));
        }
        if (oldSound != null) {
            return SoundStop.named(convert(oldSound));
        }
        if (oldSource != null) {
            return SoundStop.source(convert(oldSource));
        }
        return SoundStop.all();
    }

    @NotNull
    @Override
    default net.kyori.adventure.sound.Sound.Source convert(Sound.@NotNull Source source) {
        return A_SOURCE[source.ordinal()];
    }

    @Override
    @NotNull
    default Sound.Source convert(@NotNull net.kyori.adventure.sound.Sound.Source source) {
        return C_SOURCE[source.ordinal()];
    }

    @SuppressWarnings({"UnstableApiUsage", "removal"})
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    @NotNull
    @Override
    default net.kyori.adventure.audience.MessageType convert(@NotNull MessageType messageType) {
        return A_MESSAGE_TYPE[messageType.ordinal()];
    }

    @SuppressWarnings({"UnstableApiUsage", "removal"})
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    @Override
    @NotNull
    default MessageType convert(net.kyori.adventure.audience.@NotNull MessageType messageType) {
        return C_MESSAGE_TYPE[messageType.ordinal()];
    }

    @NotNull
    @Override
    default net.kyori.adventure.chat.SignedMessage convert(@NotNull SignedMessage signedMessage) {
        return new WrappedSignedMessageC2P(signedMessage, this);
    }

    @Override
    @NotNull
    default SignedMessage convert(net.kyori.adventure.chat.@NotNull SignedMessage signedMessage) {
        return new WrappedSignedMessageP2C(signedMessage, this);
    }

    @NotNull
    @Override
    default net.kyori.adventure.identity.Identity convert(@NotNull Identity identity) {
        return net.kyori.adventure.identity.Identity.identity(identity.uuid());
    }

    @Override
    @NotNull
    default Identity convert(net.kyori.adventure.identity.@NotNull Identity identity) {
        return Identity.identity(identity.uuid());
    }

    @NotNull
    @Override
    default net.kyori.adventure.chat.ChatType.Bound convert(ChatType.@NotNull Bound bound) {
        var target = bound.target();
        if (target != null) {
            return convert(bound.type()).bind(convert(bound.name()), convert(target));
        } else {
            return convert(bound.type()).bind(convert(bound.name()));
        }
    }

    @Override
    @NotNull
    default ChatType.Bound convert(net.kyori.adventure.chat.ChatType.@NotNull Bound bound) {
        var target = bound.target();
        if (target != null) {
            return convert(bound.type()).bind(convert(bound.name()), convert(target));
        } else {
            return convert(bound.type()).bind(convert(bound.name()));
        }
    }

    @NotNull
    @Override
    default net.kyori.adventure.chat.ChatType convert(@NotNull ChatType chatType) {
        return net.kyori.adventure.chat.ChatType.chatType(convert(chatType.key()));
    }

    @Override
    @NotNull
    default ChatType convert(net.kyori.adventure.chat.@NotNull ChatType chatType) {
        return ChatType.chatType(convert(chatType.key()));
    }

    @NotNull
    @Override
    default net.kyori.adventure.bossbar.BossBar.Flag convert(BossBar.@NotNull Flag flag) {
        return A_FLAG[flag.ordinal()];
    }

    @Override
    @NotNull
    default BossBar.Flag convert(net.kyori.adventure.bossbar.BossBar.@NotNull Flag flag) {
        return C_FLAG[flag.ordinal()];
    }

    @NotNull
    @Override
    default net.kyori.adventure.bossbar.BossBar.Overlay convert(BossBar.@NotNull Overlay overlay) {
        return A_OVERLAY[overlay.ordinal()];
    }

    @Override
    @NotNull
    default BossBar.Overlay convert(net.kyori.adventure.bossbar.BossBar.@NotNull Overlay overlay) {
        return C_OVERLAY[overlay.ordinal()];
    }

    @NotNull
    @Override
    default net.kyori.adventure.bossbar.BossBar.Color convert(BossBar.@NotNull Color color) {
        return A_COLOR[color.ordinal()];
    }

    @Override
    @NotNull
    default BossBar.Color convert(net.kyori.adventure.bossbar.BossBar.@NotNull Color color) {
        return C_COLOR[color.ordinal()];
    }

    @NotNull
    @Override
    default net.kyori.adventure.bossbar.BossBar convert(@NotNull BossBar bossbar) {
        return net.kyori.adventure.bossbar.BossBar.bossBar(convert(bossbar.name()), bossbar.progress(), convert(bossbar.color()), convert(bossbar.overlay()), convertBossBarFlagsC2P(bossbar.flags()));
    }

    @Override
    @NotNull
    default BossBar convert(net.kyori.adventure.bossbar.@NotNull BossBar bossbar) {
        return BossBar.bossBar(convert(bossbar.name()), bossbar.progress(), convert(bossbar.color()), convert(bossbar.overlay()), convertBossBarFlagsP2C(bossbar.flags()));
    }

    @NotNull
    @Override
    default net.kyori.adventure.title.Title.Times convert(Title.@NotNull Times times) {
        return net.kyori.adventure.title.Title.Times.times(times.fadeIn(), times.stay(), times.fadeOut());
    }

    @Override
    @NotNull
    default Title.Times convert(net.kyori.adventure.title.Title.@NotNull Times times) {
        return Title.Times.times(times.fadeIn(), times.stay(), times.fadeOut());
    }

    @NotNull
    @Override
    default net.kyori.adventure.text.Component convert(@NotNull Component component) {
        return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(GsonComponentSerializer.gson().serialize(component));
    }

    @Override
    @NotNull
    default Component convert(net.kyori.adventure.text.@NotNull Component component) {
        return GsonComponentSerializer.gson().deserialize(net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component));
    }

    @Override
    @NotNull
    default List<net.kyori.adventure.util.RGBLike> convertRGBLikeC2P(@NotNull List<RGBLike> rgbLikes) {
        return rgbLikes.stream().map(this::convert).toList();
    }

    @Override
    @NotNull
    default List<RGBLike> convertRGBLikeP2C(@NotNull List<net.kyori.adventure.util.RGBLike> rgbLikes) {
        return rgbLikes.stream().map(this::convert).toList();
    }

    @Override
    @NotNull
    default net.kyori.adventure.util.RGBLike convert(@NotNull RGBLike rgbLike) {
        return net.kyori.adventure.text.format.TextColor.color(rgbLike.red(), rgbLike.green(), rgbLike.blue());
    }

    @Override
    @NotNull
    default RGBLike convert(net.kyori.adventure.util.@NotNull RGBLike rgbLike) {
        return TextColor.color(rgbLike.red(), rgbLike.green(), rgbLike.blue());
    }
}

@SuppressWarnings("NonExtendableApiUsage")
class WrappedSignedMessageP2C implements SignedMessage {
    private final net.kyori.adventure.chat.SignedMessage handle;
    private final KyoriAdventureSupport support;

    public WrappedSignedMessageP2C(net.kyori.adventure.chat.SignedMessage handle, KyoriAdventureSupport support) {
        this.handle = handle;
        this.support = support;
    }

    @Override
    public @NotNull Instant timestamp() {
        return handle.timestamp();
    }

    @Override
    public long salt() {
        return handle.salt();
    }

    @Nullable
    @Override
    public Signature signature() {
        var signature = handle.signature();
        if (signature == null) return null;
        return support.convert(signature);
    }

    @Override
    public @Nullable Component unsignedContent() {
        var content = handle.unsignedContent();
        if (content == null) return null;
        return support.convert(content);
    }

    @Override
    public @NotNull String message() {
        return handle.message();
    }

    @Override
    public @NotNull Identity identity() {
        return support.convert(handle.identity());
    }
}

@SuppressWarnings("NonExtendableApiUsage")
class WrappedSignedMessageC2P implements net.kyori.adventure.chat.SignedMessage {
    private final SignedMessage handle;
    private final KyoriAdventureSupport support;

    public WrappedSignedMessageC2P(SignedMessage handle, KyoriAdventureSupport support) {
        this.handle = handle;
        this.support = support;
    }

    @Override
    public @NotNull Instant timestamp() {
        return handle.timestamp();
    }

    @Override
    public long salt() {
        return handle.salt();
    }

    @Nullable
    @Override
    public Signature signature() {
        var signature = handle.signature();
        if (signature == null) return null;
        return support.convert(signature);
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component unsignedContent() {
        var content = handle.unsignedContent();
        if (content == null) return null;
        return support.convert(content);
    }

    @Override
    public @NotNull String message() {
        return handle.message();
    }

    @Override
    public @NotNull net.kyori.adventure.identity.Identity identity() {
        return support.convert(handle.identity());
    }
}

class Fields {
    static final BossBar.Color[] C_COLOR = BossBar.Color.values();
    static final BossBar.Overlay[] C_OVERLAY = BossBar.Overlay.values();
    static final BossBar.Flag[] C_FLAG = BossBar.Flag.values();
    static final Sound.Source[] C_SOURCE = Sound.Source.values();
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    static final MessageType[] C_MESSAGE_TYPE = MessageType.values();

    static final net.kyori.adventure.bossbar.BossBar.Color[] A_COLOR = net.kyori.adventure.bossbar.BossBar.Color.values();
    static final net.kyori.adventure.bossbar.BossBar.Overlay[] A_OVERLAY = net.kyori.adventure.bossbar.BossBar.Overlay.values();
    static final net.kyori.adventure.bossbar.BossBar.Flag[] A_FLAG = net.kyori.adventure.bossbar.BossBar.Flag.values();
    static final net.kyori.adventure.sound.Sound.Source[] A_SOURCE = net.kyori.adventure.sound.Sound.Source.values();
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    static final net.kyori.adventure.audience.MessageType[] A_MESSAGE_TYPE = net.kyori.adventure.audience.MessageType.values();
}