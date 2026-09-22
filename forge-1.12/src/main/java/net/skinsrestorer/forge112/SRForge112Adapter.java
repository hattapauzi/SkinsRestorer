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

import ch.jalu.injector.Injector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.skinsrestorer.api.property.SkinProperty;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.skinsrestorer.forge112.command.Forge112CommandRegistrationHandler;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.codec.SRServerPluginMessage;
import net.skinsrestorer.shared.commands.SoundProvider;
import net.skinsrestorer.shared.gui.SRInventory;
import net.skinsrestorer.shared.info.Platform;
import net.skinsrestorer.shared.info.PluginInfo;
import net.skinsrestorer.shared.plugin.SRPlatformAdapter;
import net.skinsrestorer.shared.plugin.SRServerAdapter;
import net.skinsrestorer.shared.subjects.SRCommandSender;
import net.skinsrestorer.shared.subjects.SRPlayer;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SRForge112Adapter implements SRServerAdapter {
    private static SRForge112Adapter instance;
    private static final List<Object> REFERENCES_TO_PREVENT_GC = new ArrayList<>();
    private final Injector injector;
    private Forge112CommandManager commandManager;
    private final ScheduledExecutorService asyncScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "SkinsRestorer-Async");
        thread.setDaemon(true);
        return thread;
    });
    private final MinecraftServer server;

    @Inject
    public SRForge112Adapter(Injector injector, MinecraftServer server) {
        this.injector = injector;
        this.server = server;
        instance = this;
    }

    public static SRForge112Adapter instance() {
        return instance;
    }

    public MinecraftServer server() {
        return server;
    }

    @Override
    public Object createMetricsInstance() {
        return null;
    }

    @Override
    public InputStream getResource(String resource) {
        return SRPlatformAdapter.class.getClassLoader().getResourceAsStream(resource);
    }

    @Override
    public CommandManager<SRCommandSender> createCommandManager() {
        WrapperForge wrapper = injector.getSingleton(WrapperForge.class);
        commandManager = new Forge112CommandManager(
                ExecutionCoordinator.asyncCoordinator(),
                wrapper,
                new Forge112CommandRegistrationHandler()
        );
        return commandManager;
    }

    public void registerForgeCommands(FMLServerStartingEvent event) {
        if (commandManager != null) {
            commandManager.registrationHandler().flush(event);
        }
    }

    @Override
    public void runAsync(Runnable runnable) {
        asyncScheduler.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAsyncDelayed(Runnable runnable, long delay, TimeUnit timeUnit) {
        asyncScheduler.schedule(runnable, delay, timeUnit);
    }

    @Override
    public void runRepeatAsync(Runnable runnable, long delay, long interval, TimeUnit timeUnit) {
        asyncScheduler.scheduleWithFixedDelay(runnable, delay, interval, timeUnit);
    }

    @Override
    public void runSync(SRCommandSender sender, Runnable runnable) {
        server.addScheduledTask(runnable);
    }

    @Override
    public void runSyncToPlayer(SRPlayer player, Runnable runnable) {
        server.addScheduledTask(runnable);
    }

    @Override
    public boolean determineProxy() {
        return false;
    }

    @Override
    public void openGUI(SRPlayer player, SRInventory srInventory) {
        // later plan
    }

    @Override
    public void giveSkullItem(SRPlayer player, SRServerPluginMessage.GiveSkullChannelPayload giveSkullPayload) {
        // later plan
    }

    @Override
    public Class<? extends SoundProvider> getSoundProviderClass() {
        return SoundProvider.NoopSoundProvider.class;
    }

    @Override
    public void extendLifeTime(Object plugin, Object object) {
        REFERENCES_TO_PREVENT_GC.add(object);
    }

    @Override
    public boolean supportsDefaultPermissions() {
        return true;
    }

    @Override
    public void shutdownCleanup() {
        asyncScheduler.shutdown();
        REFERENCES_TO_PREVENT_GC.clear();
    }

    @Override
    public String getPlatformVersion() {
        return ForgeVersion.mcVersion;
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public String getPlatformVendor() {
        return ForgeVersion.getVersion();
    }

    @Override
    public Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public List<PluginInfo> getPlugins() {
        List<PluginInfo> list = new ArrayList<>();
        for (ModContainer mod : Loader.instance().getModList()) {
            list.add(new PluginInfo(
                    true,
                    mod.getModId(),
                    mod.getName(),
                    String.valueOf(mod.getVersion()),
                    "N/A",
                    Map.of(),
                    mod.getMetadata() != null ? mod.getMetadata().authorList : List.of("N/A")
            ));
        }
        return list;
    }

    @Override
    public Optional<SkinProperty> getSkinProperty(SRPlayer player) {
        return ForgeGameProfiles.readTextures(player.getAs(EntityPlayerMP.class).getGameProfile());
    }

    @Override
    public Collection<SRPlayer> getOnlinePlayers(SRCommandSender sender) {
        WrapperForge wrapper = injector.getSingleton(WrapperForge.class);
        List<SRPlayer> players = new ArrayList<>();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            players.add(wrapper.player(player));
        }
        return players;
    }

    @Override
    public Optional<SRPlayer> getPlayer(SRCommandSender sender, UUID uniqueId) {
        EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(uniqueId);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(injector.getSingleton(WrapperForge.class).player(player));
    }
}
