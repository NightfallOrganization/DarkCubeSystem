/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.command;

import java.util.List;
import java.util.Locale;

import eu.darkcube.system.BaseMessage;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GameModeCommand extends Command {
    public GameModeCommand() {
        super("gamemode", "gm");

        var gamemodeArgumentType = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var targetsArgumentType = ArgumentType.Entity("targets").onlyPlayers(true);

        setCondition((sender, _) -> PermissionProvider.provider().hasPermission(sender, "command.gamemode"));
        gamemodeArgumentType.setCallback((sender, exception) -> MinestomCommandExecutor.create(sender).sendMessage(Message.INVALID_GAMEMODE, exception.getInput()));

        addConditionalSyntax((sender, _) -> sender instanceof Player, (sender, context) -> gamemode(sender, context.get(gamemodeArgumentType), List.of((Player) sender)), gamemodeArgumentType);
        addSyntax((sender, context) -> gamemode(sender, context.get(gamemodeArgumentType), context.get(targetsArgumentType).find(sender)), gamemodeArgumentType, targetsArgumentType);
    }

    private void gamemode(CommandSender sender, GameMode gameMode, List<Entity> targets) {
        for (var target : targets) {
            if (target instanceof Player player) {
                player.setGameMode(gameMode);
                var gameModeComponent = Component.translatable("gameMode." + gameMode.name().toLowerCase(Locale.ROOT));
                if (target == sender) {
                    sender.sendMessage(Component.translatable("commands.gamemode.success.self", gameModeComponent));
                } else {
                    var playerName = player.getDisplayName() == null ? player.getName() : player.getDisplayName();
                    player.sendMessage(Component.translatable("gameMode.changed", gameModeComponent));
                    sender.sendMessage(Component.translatable("commands.gamemode.success.other", playerName, gameModeComponent));
                }
            }
        }
    }

    public enum Message implements BaseMessage {
        INVALID_GAMEMODE;

        @Override
        public String key() {
            return name();
        }
    }
}
