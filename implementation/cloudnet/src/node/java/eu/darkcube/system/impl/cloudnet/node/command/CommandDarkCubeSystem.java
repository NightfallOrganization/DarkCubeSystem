/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.cloudnet.node.command;

import java.util.Set;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.node.command.annotation.CommandAlias;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;

@Singleton
@CommandAlias("darkcubesystem")
@CommandPermission("darkcube.command.darkcubesystem")
public class CommandDarkCubeSystem {

    @Inject
    private DatabaseProvider databaseProvider;

    @CommandMethod("darkcubesystem migrateAll")
    public void migrateAll() {
        migrateUserApi();
        migrateLobbySystem();
    }

    @CommandMethod("darkcubesystem migrateWoolBattle")
    public void migrateWoolBattle() {
        var database = databaseProvider.database("persistent_data");
        var documentImmutable = database.get("woolbattle:woolbattle");
        if (documentImmutable == null) return;

        var document = documentImmutable.mutableCopy();
        migrateLong(document, "WoolBattle:lobbydeathline", "woolbattle:lobby_death_line");
        migrateLong(document, "WoolBattle:min_player_count", "woolbattle:min_player_count");
        migrateDocument(document, "WoolBattle:spawn", "woolbattle:spawn");

        database.insert("woolbattle:woolbattle", document);
    }

    @CommandMethod("darkcubesystem migrateLobbySystem")
    public void migrateLobbySystem() {
        var database = databaseProvider.database("persistent_data");
        var documentImmutable = database.get("LobbySystem:LobbySystem");
        if (documentImmutable == null) return;

        var document = documentImmutable.mutableCopy();
        migrateDocument(document, "LobbySystem:npcs", "lobbysystem:npcs");

        if (databaseProvider.containsDatabase("lobbysystem_data")) {
            var lobbysystemData = databaseProvider.database("lobbysystem_data");

            var borderImmutable = lobbysystemData.get("border");
            if (borderImmutable != null) {
                document.append("lobbysystem:border", borderImmutable);
            }

            var dailyRewardNpcLocationImmutable = lobbysystemData.get("dailyrewardNPCLocation");
            if (dailyRewardNpcLocationImmutable != null) {
                document.append("lobbysystem:daily_reward_npc_location", dailyRewardNpcLocationImmutable);
            }

            var fisherNpcLocationImmutable = lobbysystemData.get("fisherNPCLocation");
            if (fisherNpcLocationImmutable != null) {
                document.append("lobbysystem:fisher_npc_location", fisherNpcLocationImmutable);
            }

            var fisherSpawnImmutable = lobbysystemData.get("fisherSpawn");
            if (fisherSpawnImmutable != null) {
                document.append("lobbysystem:fisher_spawn", fisherSpawnImmutable);
            }

            var jumpAndRunEnabledImmutable = lobbysystemData.get("jumpAndRunEnabled");
            if (jumpAndRunEnabledImmutable != null) {
                document.append("lobbysystem:jump_and_run_enabled", jumpAndRunEnabledImmutable.getBoolean("value"));
            }

            var jumpAndRunPlateImmutable = lobbysystemData.get("jumpAndRunPlate");
            if (jumpAndRunPlateImmutable != null) {
                document.append("lobbysystem:jump_and_run_plate", jumpAndRunPlateImmutable);
            }

            var jumpAndRunSpawnImmutable = lobbysystemData.get("jumpAndRunSpawn");
            if (jumpAndRunSpawnImmutable != null) {
                document.append("lobbysystem:jump_and_run_spawn", jumpAndRunSpawnImmutable);
            }

            var spawnImmutable = lobbysystemData.get("spawn");
            if (spawnImmutable != null) {
                document.append("lobbysystem:spawn", spawnImmutable);
            }

            var sumoNpcLocationImmutable = lobbysystemData.get("sumoNPCLocation");
            if (sumoNpcLocationImmutable != null) {
                document.append("lobbysystem:sumo_npc_location", sumoNpcLocationImmutable);
            }

            var sumoSpawnImmutable = lobbysystemData.get("sumoSpawn");
            if (sumoSpawnImmutable != null) {
                document.append("lobbysystem:sumo_spawn", sumoSpawnImmutable);
            }

            var sumoTasksImmutable = lobbysystemData.get("sumoTasks");
            if (sumoTasksImmutable != null) {
                var tasks = (Set<String>) sumoTasksImmutable.readObject("tasks", TypeToken.getParameterized(Set.class, String.class).getType());
                document.append("lobbysystem:sumo_tasks", tasks);
            }

            var winterImmutable = lobbysystemData.get("winter");
            if (winterImmutable != null) {
                document.append("lobbysystem:winter", winterImmutable.getBoolean("value"));
            }

            var woolbattleNpcLocationImmutable = lobbysystemData.get("woolbattleNPCLocation");
            if (woolbattleNpcLocationImmutable != null) {
                document.append("lobbysystem:woolbattle_npc_location", woolbattleNpcLocationImmutable);
            }

            var woolbattleSpawnImmutable = lobbysystemData.get("woolbattleSpawn");
            if (woolbattleSpawnImmutable != null) {
                document.append("lobbysystem:woolbattle_spawn", woolbattleSpawnImmutable);
            }

            var woolbattleTasksImmutable = lobbysystemData.get("woolbattleTasks");
            if (woolbattleTasksImmutable != null) {
                var tasks = (Set<String>) woolbattleTasksImmutable.readObject("tasks", TypeToken.getParameterized(Set.class, String.class).getType());
                document.append("lobbysystem:woolbattle_tasks", tasks);
            }

            databaseProvider.deleteDatabase("lobbysystem_data");
        }

        database.delete("LobbySystem:LobbySystem");
        database.insert("lobbysystem:lobbysystem", document);
    }

    @CommandMethod("darkcubesystem migrateUserApi")
    public void migrateUserApi() {
        var database = databaseProvider.database("userapi_users");
        for (var entry : database.entries().entrySet()) {
            var document = entry.getValue().mutableCopy();
            var persistentData = document.readDocument("persistentData").mutableCopy();
            migrateBoolean(persistentData, "LobbySystem:animations", "lobbysystem:animations");
            migrateBoolean(persistentData, "LobbySystem:sounds", "lobbysystem:sounds");
            migrateLong(persistentData, "LobbySystem:lastDailyReward", "lobbysystem:last_daily_reward");
            migrateDocument(persistentData, "LobbySystem:rewardSlotsUsed", "lobbysystem:reward_slots_used");
            migrateString(persistentData, "LobbySystem:gadget", "lobbysystem:gadget");
            migrateLong(persistentData, "LobbySystem:selectedSlot", "lobbysystem:selected_slot");
            migrateDocument(persistentData, "LobbySystem:position", "lobbysystem:position");
            migrateLong(persistentData, "LobbySystem:spawn_round_count", "lobbysystem:spawn_round_count");
            migrateLong(persistentData, "LobbySystem:lobby_round_count", "lobbysystem:lobby_round_count");

            migrateLong(persistentData, "UserAPI:cubes", "userapi:cubes");
            migrateString(persistentData, "UserAPI:language", "userapi:language");

            migrateDocument(persistentData, "WoolBattle:heightDisplay", "woolbattle:height_display");
            migrateBoolean(persistentData, "WoolBattle:particles", "woolbattle:particles");
            migrateDocument(persistentData, "WoolBattle:perks", "woolbattle:perks");
            migrateString(persistentData, "WoolBattle:woolSubtractDirection", "woolbattle:wool_subtract_direction");
            migrateLong(persistentData, "WoolBattle:data_version", "woolbattle:data_version");

            document.append("persistentData", persistentData);
            database.insert(entry.getKey(), document);
        }
    }

    private void migrateDocument(Document.Mutable document, String key, String newKey) {
        if (document.contains(key)) {
            var data = document.readDocument(key);
            document.remove(key);
            document.append(newKey, data);
        }
    }

    private void migrateBoolean(Document.Mutable document, String key, String newKey) {
        if (document.contains(key)) {
            var data = document.getBoolean(key);
            document.remove(key);
            document.append(newKey, data);
        }
    }

    private void migrateString(Document.Mutable document, String key, String newKey) {
        if (document.contains(key)) {
            var data = document.getString(key);
            document.remove(key);
            document.append(newKey, data);
        }
    }

    private void migrateLong(Document.Mutable document, String key, String newKey) {
        if (document.contains(key)) {
            var data = document.getLong(key);
            document.remove(key);
            document.append(newKey, data);
        }
    }
}
