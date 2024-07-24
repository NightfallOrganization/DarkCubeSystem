/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.kyori.wrapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
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
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class KyoriWrappedAudience implements Audience {
    private static final ConcurrentMap<BossBar, net.kyori.adventure.bossbar.BossBar> bossBars = new ConcurrentHashMap<>();
    private final net.kyori.adventure.audience.Audience handle;
    private final KyoriAdventureSupport support;

    public KyoriWrappedAudience(net.kyori.adventure.audience.Audience handle, KyoriAdventureSupport support) {
        this.handle = handle;
        this.support = support;
    }

    @Override
    public @NotNull Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return Audience.super.filterAudience(filter); // TODO
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        handle.sendMessage(support.convert(message));
    }

    @Override
    public void sendMessage(@NotNull Component message, ChatType.Bound boundChatType) {
        handle.sendMessage(support.convert(message), support.convert(boundChatType));
    }

    @Override
    public void sendMessage(@NotNull SignedMessage signedMessage, ChatType.Bound boundChatType) {
        handle.sendMessage(support.convert(signedMessage), support.convert(boundChatType));
    }

    @SuppressWarnings({"UnstableApiUsage", "removal", "deprecation"})
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        handle.sendMessage(support.convert(source), support.convert(message), support.convert(type));
    }

    @Override
    public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        Audience.super.forEachAudience(action); // TODO
    }

    @Override
    public void deleteMessage(SignedMessage.Signature signature) {
        handle.deleteMessage(support.convert(signature));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        handle.sendActionBar(support.convert(message));
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        handle.sendPlayerListHeaderAndFooter(support.convert(header), support.convert(footer));
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        if (part == TitlePart.TITLE) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.TITLE, support.convert((Component) value));
        } else if (part == TitlePart.SUBTITLE) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.SUBTITLE, support.convert((Component) value));
        } else if (part == TitlePart.TIMES) {
            handle.sendTitlePart(net.kyori.adventure.title.TitlePart.TIMES, support.convert((Title.Times) value));
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
        var bossBar = support.convert(bar);
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
        handle.playSound(support.convert(sound));
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        handle.playSound(support.convert(sound), x, y, z);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.Emitter emitter) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        handle.stopSound(support.convert(stop));
    }

    @Override
    public void openBook(@NotNull Book book) {
        handle.openBook(support.convert(book));
    }
}
