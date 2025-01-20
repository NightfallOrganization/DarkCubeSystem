/*
 * Copyright (c) 2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.test.command.BaseCommand;

public class NewOutCommand extends BaseCommand {
    public NewOutCommand() {
        super("newOutputStream", b -> b.then(Commands.argument("text", StringArgumentType.greedyString()).executes(ctx -> {
            var text = StringArgumentType.getString(ctx, "text");
            var storage = InjectionLayer.boot().instance(TemplateStorageProvider.class).templateStorage("woolbattle");
            System.out.println("Starting");
            storage.newOutputStreamAsync(ServiceTemplate.builder().storage("woolbattle").prefix("test").name("default").build(), "test.txt").whenComplete((out, throwable) -> {
                System.out.println("Done");
                if (out != null) {
                    try {
                        out.write(text.getBytes(StandardCharsets.UTF_8));
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(out);
                System.out.println(throwable);
            });
            return 0;
        })));
    }
}
