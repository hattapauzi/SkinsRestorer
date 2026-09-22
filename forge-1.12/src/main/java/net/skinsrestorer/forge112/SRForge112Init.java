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
import net.minecraftforge.common.MinecraftForge;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.log.SRLogger;
import net.skinsrestorer.shared.plugin.SRPlugin;
import net.skinsrestorer.shared.plugin.SRServerPlatformInit;

import javax.inject.Inject;

public class SRForge112Init implements SRServerPlatformInit {
    private final SRPlugin plugin;
    private final Injector injector;
    private final WrapperForge wrapper;

    @Inject
    public SRForge112Init(SRPlugin plugin, Injector injector, WrapperForge wrapper) {
        this.plugin = plugin;
        this.injector = injector;
        this.wrapper = wrapper;
    }

    @Override
    public void initSkinApplier() {
        plugin.registerSkinApplier(injector.getSingleton(SkinApplierForge112.class), EntityPlayerMP.class, wrapper);
        injector.getSingleton(SRLogger.class).warning(
                "Forge 1.12.2 companion: GUI and plugin messages are not registered yet.");
    }

    @Override
    public void initLoginProfileListener() {
        MinecraftForge.EVENT_BUS.register(injector.getSingleton(Forge112LoginListener.class));
    }

    @Override
    public void initAdminInfoListener() {
    }

    @Override
    public void initPermissions() {
    }

    @Override
    public void initGUIListener() {
    }

    @Override
    public void initMessageChannel() {
    }
}
