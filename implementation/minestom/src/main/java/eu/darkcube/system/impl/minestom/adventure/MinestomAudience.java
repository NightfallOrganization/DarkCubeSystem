/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.minestom.adventure;

import static eu.darkcube.system.impl.minestom.adventure.AdventureUtils.convert;
import static net.kyori.adventure.chat.SignedMessage.signature;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.MessageType;
import eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar;
import eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType;
import eu.darkcube.system.libs.net.kyori.adventure.chat.SignedMessage;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.inventory.Book;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import eu.darkcube.system.libs.net.kyori.adventure.sound.SoundStop;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.title.Title;
import eu.darkcube.system.libs.net.kyori.adventure.title.TitlePart;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.adventure.audience.Audiences;

public class MinestomAudience implements Audience {
    private static final ConcurrentMap<BossBar, net.kyori.adventure.bossbar.BossBar> bossBars = new ConcurrentHashMap<>();
    public static MinestomAudience ALL = new MinestomAudience(Audiences.all());
    public static MinestomAudience CONSOLE = new MinestomAudience(Audiences.console());
    public static MinestomAudience PLAYERS = new MinestomAudience(Audiences.players());

    private final net.kyori.adventure.audience.Audience handle;

    MinestomAudience(net.kyori.adventure.audience.Audience handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return Audience.super.filterAudience(filter); // TODO
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        handle.sendMessage(convert(message));
    }

    @Override
    public void sendMessage(@NotNull Component message, ChatType.@NotNull Bound boundChatType) {
        handle.sendMessage(convert(message), convert(boundChatType));
    }

    @Override
    public void sendMessage(@NotNull SignedMessage signedMessage, ChatType.@NotNull Bound boundChatType) {
        handle.sendMessage(convert(signedMessage), convert(boundChatType));
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        handle.sendMessage(convert(source), convert(message), convert(type));
    }

    @Override
    public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        Audience.super.forEachAudience(action); // TODO
    }

    @Override
    public void deleteMessage(SignedMessage.@NotNull Signature signature) {
        handle.deleteMessage(signature(signature.bytes()));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        handle.sendActionBar(convert(message));
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        handle.sendPlayerListHeaderAndFooter(convert(header), convert(footer));
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        if (part == TitlePart.TITLE) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.TITLE, convert((Component) value));
        } else if (part == TitlePart.SUBTITLE) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.SUBTITLE, convert((Component) value));
        } else if (part == TitlePart.TIMES) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.TIMES, convert((Title.Times) value));
        } else throw new IllegalArgumentException("Invalid TitlePart: " + part);
    }

    @Override
    public void clearTitle() {
        handle.clearTitle();
    }

    @Override
    public void resetTitle() {
        handle.resetTitle();
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        var bossBar = convert(bar);
        bossBars.put(bar, bossBar);
        handle.showBossBar(bossBar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        var bossBar = bossBars.remove(bar);
        if (bossBar != null) {
            handle.hideBossBar(bossBar);
        }
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        handle.playSound(convert(sound));
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        handle.playSound(convert(sound), x, y, z);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        handle.stopSound(convert(stop));
    }

    @Override
    public void openBook(@NotNull Book book) {
        handle.openBook(convert(book));
    }
}
