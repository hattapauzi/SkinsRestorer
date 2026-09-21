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

import ch.jalu.configme.SettingsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.shared.api.SkinApplierAccess;
import net.skinsrestorer.shared.api.event.EventBusImpl;
import net.skinsrestorer.shared.api.event.SkinApplyEventImpl;
import net.skinsrestorer.shared.config.ServerConfig;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

public class SkinApplierForge112 implements SkinApplierAccess<EntityPlayerMP> {
    private final SRForge112Adapter adapter;
    private final EventBusImpl eventBus;
    private final SettingsManager settings;

    @Inject
    public SkinApplierForge112(SRForge112Adapter adapter, EventBusImpl eventBus, SettingsManager settings) {
        this.adapter = adapter;
        this.eventBus = eventBus;
        this.settings = settings;
    }

    @Override
    public void applySkin(EntityPlayerMP player, SkinProperty property) {
        if (player.hasDisconnected()) {
            return;
        }
        adapter.runAsync(() -> {
            SkinApplyEventImpl applyEvent = new SkinApplyEventImpl(player, property);
            eventBus.callEvent(applyEvent);
            if (applyEvent.isCancelled()) {
                return;
            }
            adapter.server().addScheduledTask(() -> applySkinSync(player, applyEvent.getProperty()));
        });
    }

    public void applySkinSync(EntityPlayerMP player, SkinProperty property) {
        if (player.hasDisconnected()) {
            return;
        }
        ejectPassengers(player);
        ForgeGameProfiles.applyTextures(player.getGameProfile(), property);
        refreshTabList(player);
        refreshTrackedEntity(player);
        refreshSelf(player);
    }

    private void ejectPassengers(EntityPlayerMP player) {
        Entity vehicle = player.getRidingEntity();
        if (settings.getProperty(ServerConfig.DISMOUNT_PLAYER_ON_UPDATE) && vehicle != null) {
            player.dismountRidingEntity();
            if (settings.getProperty(ServerConfig.REMOUNT_PLAYER_ON_UPDATE)) {
                adapter.server().addScheduledTask(() -> player.startRiding(vehicle, true));
            }
        }
        if (settings.getProperty(ServerConfig.DISMOUNT_PASSENGERS_ON_UPDATE) && !player.getPassengers().isEmpty()) {
            player.removePassengers();
        }
    }

    private void refreshTabList(EntityPlayerMP player) {
        SPacketPlayerListItem remove = new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, player);
        SPacketPlayerListItem add = new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, player);
        for (EntityPlayerMP other : player.server.getPlayerList().getPlayers()) {
            other.connection.sendPacket(remove);
            other.connection.sendPacket(add);
        }
    }

    private void refreshTrackedEntity(EntityPlayerMP player) {
        EntityTracker tracker = player.getServerWorld().getEntityTracker();
        tracker.sendToTracking(player, new SPacketDestroyEntities(player.getEntityId()));
        tracker.sendToTracking(player, new SPacketSpawnPlayer(player));
    }

    private void refreshSelf(EntityPlayerMP player) {
        WorldServer world = player.getServerWorld();
        player.connection.sendPacket(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, player));
        player.connection.sendPacket(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, player));
        player.connection.sendPacket(new SPacketRespawn(
                world.provider.getDimensionType().getId(),
                world.getDifficulty(),
                world.getWorldInfo().getTerrainType(),
                player.interactionManager.getGameType()
        ));
        player.connection.sendPacket(new SPacketPlayerPosLook(
                player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch,
                Collections.emptySet(), 0
        ));
        player.connection.sendPacket(new SPacketHeldItemChange(player.inventory.currentItem));
        player.server.getPlayerList().updatePermissionLevel(player);
        player.server.getPlayerList().updateTimeAndWeatherForPlayer(player, world);
        player.sendPlayerAbilities();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), effect));
        }
        player.inventoryContainer.detectAndSendChanges();
        player.connection.sendPacket(new SPacketUpdateHealth(
                player.getHealth(),
                player.getFoodStats().getFoodLevel(),
                player.getFoodStats().getSaturationLevel()));
        player.connection.sendPacket(new SPacketSetExperience(
                player.experience, player.experienceTotal, player.experienceLevel));
        Collection<IAttributeInstance> watched = ((AttributeMap) player.getAttributeMap()).getWatchedAttributes();
        if (!watched.isEmpty()) {
            player.connection.sendPacket(new SPacketEntityProperties(player.getEntityId(), watched));
        }
    }
}
