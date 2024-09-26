/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command.test;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.test.TestPlugin;
import eu.darkcube.system.test.command.BaseCommand;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.scheduler.BukkitRunnable;

public class DataStorage extends BaseCommand {
    private static final Key R = Key.key("testplugin", "datastorage_runnable");
    private static final DataKey<Integer> D_KEY = DataKey.of(Key.key("testplugin", "datastorage_counter"), PersistentDataTypes.INTEGER);

    public DataStorage(TestPlugin plugin) {
        super("dataStorage", b -> b.then(Commands.literal("start").executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            var user = UserAPI.instance().user(player.getUniqueId());
            user.metadata().set(R, new BukkitRunnable() {
                {
                    runTaskTimer(plugin, 1, 1);
                }

                @Override
                public void run() {
                    for (var i = 0; i < 10; i++) {
                        increment();
                    }
                    user.sendMessage(Component.text(get()));
                }

                private int get() {
                    return user.persistentData().get(D_KEY, () -> 0);
                }

                private void increment() {
                    var val = get();
                    user.persistentData().set(D_KEY, val + 1);
                }
            });
            return 0;
        })).then(Commands.literal("stop").executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            var user = UserAPI.instance().user(player.getUniqueId());
            user.metadata().<BukkitRunnable>remove(R).cancel();
            return 0;
        })));
    }
}
