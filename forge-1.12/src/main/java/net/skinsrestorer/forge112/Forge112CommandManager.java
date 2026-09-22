/*
 * SkinsRestorer
 * Copyright (C) 2026  SkinsRestorer Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.skinsrestorer.forge112;

import net.skinsrestorer.forge112.command.Forge112CommandRegistrationHandler;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.subjects.SRCommandSender;
import net.skinsrestorer.shared.subjects.permissions.Permission;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

public final class Forge112CommandManager extends CommandManager<SRCommandSender>
        implements Forge112CommandRegistrationHandler.CommandManagerHolder {
    private final WrapperForge wrapper;
    private final Forge112CommandRegistrationHandler registrationHandler;

    public Forge112CommandManager(
            ExecutionCoordinator<SRCommandSender> coordinator,
            WrapperForge wrapper,
            Forge112CommandRegistrationHandler registrationHandler
    ) {
        super(coordinator, registrationHandler);
        this.wrapper = wrapper;
        this.registrationHandler = registrationHandler;
        registrationHandler.initialize(this);
    }

    @Override
    public org.incendo.cloud.CommandManager<SRCommandSender> commandManager() {
        return this;
    }

    @Override
    public WrapperForge wrapper() {
        return wrapper;
    }

    public Forge112CommandRegistrationHandler registrationHandler() {
        return registrationHandler;
    }

    @Override
    public boolean hasPermission(SRCommandSender sender, String permission) {
        return sender.hasPermission(Permission.of(permission));
    }
}
