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
package net.skinsrestorer.forge112.command;

import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.subjects.SRCommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.internal.CommandRegistrationHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Forge112CommandRegistrationHandler implements CommandRegistrationHandler<SRCommandSender> {
    private final Map<String, ICommand> pending = new LinkedHashMap<>();
    private CommandManagerHolder holder;

    public interface CommandManagerHolder {
        org.incendo.cloud.CommandManager<SRCommandSender> commandManager();

        WrapperForge wrapper();
    }

    public void initialize(CommandManagerHolder holder) {
        this.holder = holder;
    }

    @Override
    public boolean registerCommand(Command<SRCommandSender> command) {
        CommandComponent<SRCommandSender> root = command.rootComponent();
        pending.putIfAbsent(root.name(), new CloudForgeCommand(root.name(), holder.commandManager(), holder.wrapper()));
        for (String alias : root.alternativeAliases()) {
            pending.putIfAbsent(alias, new CloudForgeCommand(alias, holder.commandManager(), holder.wrapper()));
        }
        return true;
    }

    public void flush(FMLServerStartingEvent event) {
        for (ICommand command : pending.values()) {
            event.registerServerCommand(command);
        }
    }
}
