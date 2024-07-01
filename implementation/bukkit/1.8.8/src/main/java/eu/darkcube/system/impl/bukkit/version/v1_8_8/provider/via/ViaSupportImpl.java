/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.bukkit.version.v1_8_8.provider.via;

import java.util.Collections;
import java.util.List;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.mapping.PacketMappings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.packet.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.storage.TabCompleteTracker;
import com.viaversion.viaversion.protocols.v1_12to1_12_1.packet.ClientboundPackets1_12_1;
import com.viaversion.viaversion.util.GsonUtil;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.util.ReflectionUtils;
import eu.darkcube.system.impl.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.impl.bukkit.provider.via.AbstractViaSupport;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.entity.Player;

public class ViaSupportImpl extends AbstractViaSupport {

    public static final String TAB_COMPLETE_CANCEL = "DarkCube:ViaSupport:TabCompletionFor1_13+:Cancel";

    public void enable() {
        DarkCubeSystemBukkit.systemPlugin().sendConsole("[ViaSupport] Enabling ViaVersion support");
        var protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_12_2To1_13.class);
        if (protocol == null) return;
        var clientboundMappings = ReflectionUtils.getValue(protocol, AbstractProtocol.class, true, "clientboundMappings", PacketMappings.class);
        var unmapped = ClientboundPackets1_12_1.COMMAND_SUGGESTIONS;
        var mapped = ClientboundPackets1_13.COMMAND_SUGGESTIONS;
        var mapping = clientboundMappings.mappedPacket(unmapped.state(), unmapped.getId());
        if (mapping == null) return;
        var oldHandler = mapping.handler();
        protocol.registerClientbound(unmapped, mapped, wrapper -> {
            var tabCompleteTracker = wrapper.user().get(TabCompleteTracker.class);
            int count = wrapper.read(Types.VAR_INT);
            c:
            if (count == 1) {
                var completion = wrapper.read(Types.STRING);
                if (completion.startsWith(TAB_COMPLETE_CANCEL)) {
                    int id;
                    try {
                        id = Integer.parseInt(completion.substring(TAB_COMPLETE_CANCEL.length()));
                    } catch (Throwable t) {
                        id = -1;
                    }
                    if (id == -1 || id > 15) {
                        wrapper.write(Types.VAR_INT, 0);
                        break c;
                    }
                    var data = ViaTabExecutor.take(id);
                    if (data == null) {
                        wrapper.write(Types.VAR_INT, 0);
                        break c;
                    }
                    wrapper.write(Types.VAR_INT, tabCompleteTracker.getTransactionId());
                    wrapper.write(Types.VAR_INT, data.start());
                    wrapper.write(Types.VAR_INT, data.length());
                    wrapper.write(Types.VAR_INT, data.suggestions().length);
                    for (var i = 0; i < data.suggestions().length; i++) {
                        var suggestion = data.suggestions()[i];
                        var tooltip = data.tooltips()[i];
                        wrapper.write(Types.STRING, suggestion);

                        @Nullable var transformedTooltip = tooltip != Component.empty() ? GsonComponentSerializer.gson().serialize(tooltip) : null;
                        var json = transformedTooltip == null ? null : GsonUtil.getGson().fromJson(transformedTooltip, JsonElement.class);
                        wrapper.write(Types.OPTIONAL_COMPONENT, json);
                    }
                    return;
                } else {
                    wrapper.write(Types.VAR_INT, count);
                    wrapper.write(Types.STRING, completion);
                }
            } else {
                wrapper.write(Types.VAR_INT, count);
                for (var i = 0; i < count; i++) {
                    wrapper.passthrough(Types.STRING);
                }
            }

            wrapper.resetReader();
            if (oldHandler != null) oldHandler.handle(wrapper);
        }, true);
    }

    @Override
    public void init() {
    }

    @Override
    public List<String> tabComplete(ProtocolVersion playerVersion, Player player, String commandLine, ParseResults<CommandSource> parse, Suggestions suggestions) {
        var id = ViaTabExecutor.work(commandLine, suggestions);
        return Collections.singletonList(TAB_COMPLETE_CANCEL + id);
    }
}
