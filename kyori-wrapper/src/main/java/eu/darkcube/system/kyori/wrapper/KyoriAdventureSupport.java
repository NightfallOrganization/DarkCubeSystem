/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.kyori.wrapper;

import java.util.List;
import java.util.Set;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.audience.MessageType;
import eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar;
import eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType;
import eu.darkcube.system.libs.net.kyori.adventure.chat.SignedMessage;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.inventory.Book;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import eu.darkcube.system.libs.net.kyori.adventure.sound.SoundStop;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.title.Title;
import eu.darkcube.system.libs.net.kyori.adventure.util.RGBLike;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;

public interface KyoriAdventureSupport extends AdventureSupport {

    @Api
    @NotNull
    static KyoriAdventureSupport adventureSupport() {
        return (KyoriAdventureSupport) AdventureSupport.adventureSupport();
    }

    @NotNull
    Component convert(@NotNull net.kyori.adventure.text.Component component);

    @NotNull
    net.kyori.adventure.text.Component convert(@NotNull Component component);

    @NotNull
    Title.Times convert(@NotNull net.kyori.adventure.title.Title.Times times);

    @NotNull
    net.kyori.adventure.title.Title.Times convert(@NotNull Title.Times times);

    @NotNull
    BossBar convert(@NotNull net.kyori.adventure.bossbar.BossBar bossbar);

    @NotNull
    net.kyori.adventure.bossbar.BossBar convert(@NotNull BossBar bossbar);

    @NotNull
    BossBar.Color convert(@NotNull net.kyori.adventure.bossbar.BossBar.Color color);

    @NotNull
    net.kyori.adventure.bossbar.BossBar.Color convert(@NotNull BossBar.Color color);

    @NotNull
    BossBar.Overlay convert(@NotNull net.kyori.adventure.bossbar.BossBar.Overlay overlay);

    @NotNull
    net.kyori.adventure.bossbar.BossBar.Overlay convert(@NotNull BossBar.Overlay overlay);

    @NotNull
    BossBar.Flag convert(@NotNull net.kyori.adventure.bossbar.BossBar.Flag flag);

    @NotNull
    net.kyori.adventure.bossbar.BossBar.Flag convert(@NotNull BossBar.Flag flag);

    @NotNull
    TextColor convert(@NotNull net.kyori.adventure.text.format.TextColor color);

    @NotNull
    net.kyori.adventure.text.format.TextColor convert(@NotNull TextColor color);

    @NotNull
    ChatType convert(@NotNull net.kyori.adventure.chat.ChatType chatType);

    @NotNull
    net.kyori.adventure.chat.ChatType convert(@NotNull ChatType chatType);

    @NotNull
    ChatType.Bound convert(@NotNull net.kyori.adventure.chat.ChatType.Bound bound);

    @NotNull
    net.kyori.adventure.chat.ChatType.Bound convert(@NotNull ChatType.Bound bound);

    @NotNull
    Identity convert(@NotNull net.kyori.adventure.identity.Identity identity);

    @NotNull
    net.kyori.adventure.identity.Identity convert(@NotNull Identity identity);

    @NotNull
    SignedMessage convert(@NotNull net.kyori.adventure.chat.SignedMessage signedMessage);

    @NotNull
    net.kyori.adventure.chat.SignedMessage convert(@NotNull SignedMessage signedMessage);

    @SuppressWarnings("UnstableApiUsage")
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    @NotNull
    MessageType convert(@NotNull net.kyori.adventure.audience.MessageType messageType);

    @SuppressWarnings("UnstableApiUsage")
    @Deprecated(forRemoval = true)
    @ApiStatus.ScheduledForRemoval
    @NotNull
    net.kyori.adventure.audience.MessageType convert(@NotNull MessageType messageType);

    @NotNull
    Sound.Source convert(@NotNull net.kyori.adventure.sound.Sound.Source source);

    @NotNull
    net.kyori.adventure.sound.Sound.Source convert(@NotNull Sound.Source source);

    @NotNull
    SoundStop convert(@NotNull net.kyori.adventure.sound.SoundStop soundStop);

    @NotNull
    net.kyori.adventure.sound.SoundStop convert(@NotNull SoundStop soundStop);

    @NotNull
    Sound convert(@NotNull net.kyori.adventure.sound.Sound sound);

    @NotNull
    net.kyori.adventure.sound.Sound convert(@NotNull Sound sound);

    @NotNull
    Key convert(@NotNull net.kyori.adventure.key.Key key);

    @NotNull
    net.kyori.adventure.key.Key convert(@NotNull Key key);

    @NotNull
    Book convert(@NotNull net.kyori.adventure.inventory.Book book);

    @NotNull
    net.kyori.adventure.inventory.Book convert(@NotNull Book book);

    @NotNull
    RGBLike convert(@NotNull net.kyori.adventure.util.RGBLike rgbLike);

    @NotNull
    net.kyori.adventure.util.RGBLike convert(@NotNull RGBLike rgbLike);

    @NotNull
    CompoundBinaryTag convert(@NotNull net.kyori.adventure.nbt.CompoundBinaryTag tag);

    @NotNull
    net.kyori.adventure.nbt.CompoundBinaryTag convert(@NotNull CompoundBinaryTag tag);

    @NotNull
    SignedMessage.Signature convert(@NotNull net.kyori.adventure.chat.SignedMessage.Signature signature);

    @NotNull
    net.kyori.adventure.chat.SignedMessage.Signature convert(@NotNull SignedMessage.Signature signature);

    @NotNull
    List<Component> convertComponentsP2C(@NotNull List<net.kyori.adventure.text.Component> components);

    @NotNull
    List<net.kyori.adventure.text.Component> convertComponentsC2P(@NotNull List<Component> components);

    @NotNull
    Set<BossBar.Flag> convertBossBarFlagsP2C(@NotNull Set<net.kyori.adventure.bossbar.BossBar.Flag> flags);

    @NotNull
    Set<net.kyori.adventure.bossbar.BossBar.Flag> convertBossBarFlagsC2P(@NotNull Set<BossBar.Flag> flags);

    @NotNull
    List<RGBLike> convertRGBLikeP2C(@NotNull List<net.kyori.adventure.util.RGBLike> rgbLikes);

    @NotNull
    List<net.kyori.adventure.util.RGBLike> convertRGBLikeC2P(@NotNull List<RGBLike> rgbLikes);
}
