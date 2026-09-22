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

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.skinsrestorer.shared.plugin.SRBootstrapper;
import net.skinsrestorer.shared.plugin.SRServerPlugin;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Path;
import java.util.List;

@Mod(
        modid = SRForge112Mod.MOD_ID,
        name = "SkinsRestorer",
        version = Tags.VERSION,
        acceptableRemoteVersions = "*",
        serverSideOnly = true,
        acceptableSaveVersions = "*"
)
public class SRForge112Mod {
    public static final String MOD_ID = "skinsrestorer";
    private Path configDir;
    private Runnable shutdownHook = () -> {
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory().toPath().resolve(MOD_ID);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        if (HybridGuard.isBukkitPresent()) {
            LogManager.getLogger(MOD_ID).error(
                    "Bukkit detected. Use the official SkinsRestorer plugin on hybrid 1.12.2. Companion mod will not start.");
            return;
        }
        MinecraftServer server = event.getServer();
        SRBootstrapper.startPlugin(
                hook -> this.shutdownHook = hook,
                List.of(new SRBootstrapper.PlatformClass<>(MinecraftServer.class, server)),
                new Log4jLoggerImpl(LogManager.getLogger(MOD_ID)),
                true,
                SRForge112Adapter.class,
                SRServerPlugin.class,
                configDir,
                SRForge112Init.class
        );
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        SRForge112Adapter adapter = SRForge112Adapter.instance();
        if (adapter == null) {
            return;
        }
        adapter.registerForgeCommands(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        shutdownHook.run();
    }
}
