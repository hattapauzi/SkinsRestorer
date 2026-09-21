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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.skinsrestorer.shared.listeners.LoginProfileListenerAdapter;

import javax.inject.Inject;

public class Forge112LoginListener {
    private final LoginProfileListenerAdapter<Void> adapter;
    private final SkinApplierForge112 applier;
    private final SRForge112Adapter platform;

    @Inject
    public Forge112LoginListener(
            LoginProfileListenerAdapter<Void> adapter,
            SkinApplierForge112 applier,
            SRForge112Adapter platform
    ) {
        this.adapter = adapter;
        this.applier = applier;
        this.platform = platform;
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }
        adapter.handleLogin(new Forge112LoginEvent(
                player.getGameProfile(),
                platform::runAsync,
                property -> applier.applySkin(player, property)
        ));
    }
}
