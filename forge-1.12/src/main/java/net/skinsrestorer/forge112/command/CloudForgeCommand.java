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

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.subjects.SRCommandSender;
import org.incendo.cloud.CommandManager;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class CloudForgeCommand extends CommandBase {
    private final String name;
    private final CommandManager<SRCommandSender> commandManager;
    private final WrapperForge wrapper;

    public CloudForgeCommand(String name, CommandManager<SRCommandSender> commandManager, WrapperForge wrapper) {
        this.name = name;
        this.commandManager = commandManager;
        this.wrapper = wrapper;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + name;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        SRCommandSender srSender = wrapper.commandSender(sender);
        commandManager.commandExecutor()
                .executeCommand(srSender, CloudCommandLine.line(name, args))
                .whenComplete((result, error) -> {
                    if (error == null) {
                        return;
                    }
                    Throwable cause = error instanceof CompletionException && error.getCause() != null
                            ? error.getCause() : error;
                    String message = cause.getMessage() == null ? "Command failed" : cause.getMessage();
                    server.addScheduledTask(() -> sender.sendMessage(new TextComponentString(message)));
                });
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        SRCommandSender srSender = wrapper.commandSender(sender);
        try {
            return commandManager.suggestionFactory()
                    .suggestImmediately(srSender, CloudCommandLine.line(name, args))
                    .list()
                    .stream()
                    .map(suggestion -> suggestion.suggestion())
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }
}
