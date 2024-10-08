/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.util;

import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;
import net.minestom.server.world.biome.BiomeParticle;

public class StoneWorld {
    public static void init() {
        // var biome = Biome.builder().name(NamespaceID.from("darkcube", "stone_world")).effects(BiomeEffects.builder().biomeParticle(new BiomeParticle(1F, new BiomeParticle.DustOption(1, 0, 1, 1))).fogColor(new Color(0, 255, 0).asRGB()).build()).build();
        var biome = Biome.builder().effects(BiomeEffects.builder().biomeParticle(new BiomeParticle(1F, Particle.DUST)).fogColor(new Color(0, 255, 0).asRGB()).build()).build();

        var biomeKey = MinecraftServer.getBiomeRegistry().register(Key.key("darkcube", "stone_world").asString(), biome);

        var dimensionType = DimensionType.builder().ambientLight(2.0F).fixedTime(6000L).hasSkylight(false).hasCeiling(true).build();
        var dimensionKey = MinecraftServer.getDimensionTypeRegistry().register(NamespaceID.from("darkcube", "stone_world"), dimensionType);

        var instance = new InstanceContainer(UUID.randomUUID(), dimensionKey, NamespaceID.from("darkcube", "stone_world"));
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(unit.absoluteStart().blockY(), 60, Block.STONE);
            unit.modifier().fillHeight(60, 61, Block.GRASS_BLOCK);
            unit.modifier().fillBiome(biomeKey);
        });

        MinecraftServer.getInstanceManager().registerInstance(instance);

        var events = MinecraftServer.getGlobalEventHandler();
        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            var player = event.getPlayer();
            player.setRespawnPoint(new Pos(0.5, 62, 0.5));
            event.setSpawningInstance(instance);
        });
        events.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) return;
            var player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            player.setPermissionLevel(4);
        });
        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> {
            sender.sendMessage("Unknown Command: " + command);
        });

    }
}
