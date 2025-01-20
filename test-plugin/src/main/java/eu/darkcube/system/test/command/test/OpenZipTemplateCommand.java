/*
 * Copyright (c) 2025. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.test.command.test;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.system.test.command.BaseCommand;

public class OpenZipTemplateCommand extends BaseCommand {
    public OpenZipTemplateCommand() {
        super("openZipIn", b -> b.executes(ctx -> {
            var storage = InjectionLayer.boot().instance(TemplateStorageProvider.class).templateStorage("woolbattle");
            System.out.println("Starting");
            storage.openZipInputStreamAsync(ServiceTemplate.builder().storage("woolbattle").prefix("test").name("default").build()).whenComplete((zipInputStream, throwable) -> {
                System.out.println("Done");
                System.out.println(zipInputStream);
                System.out.println(throwable);
            });
            return 0;
        }));
    }
}
