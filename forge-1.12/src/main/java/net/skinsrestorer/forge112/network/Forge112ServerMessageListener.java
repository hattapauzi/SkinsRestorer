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
package net.skinsrestorer.forge112.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.listeners.SRServerMessageAdapter;
import net.skinsrestorer.shared.listeners.event.SRServerMessageEvent;
import net.skinsrestorer.shared.log.SRLogger;
import net.skinsrestorer.shared.subjects.SRServerPlayer;
import net.skinsrestorer.shared.utils.SRHelpers;

import javax.inject.Inject;

public class Forge112ServerMessageListener {
    private final SRServerMessageAdapter adapter;
    private final WrapperForge wrapper;
    private final SRLogger logger;
    private FMLEventChannel channel;

    @Inject
    public Forge112ServerMessageListener(
            SRServerMessageAdapter adapter,
            WrapperForge wrapper,
            SRLogger logger
    ) {
        this.adapter = adapter;
        this.wrapper = wrapper;
        this.logger = logger;
    }

    public void register() {
        if (channel != null) {
            return;
        }
        channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SRHelpers.MESSAGE_CHANNEL);
        channel.register(this);
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        String channelName = event.getPacket().channel();
        if (!SRHelpers.MESSAGE_CHANNEL.equals(channelName)) {
            return;
        }
        EntityPlayerMP player = ((NetHandlerPlayServer) event.getHandler()).player;
        byte[] data = Forge112PluginMessages.copyPayload(event.getPacket().payload());
        try {
            adapter.handlePluginMessage(wrap(channelName, player, data));
        } catch (RuntimeException e) {
            logger.warning("Dropped malformed sr:messagechannel payload from " + player.getName(), e);
        }
    }

    public static void send(EntityPlayerMP player, byte[] data) {
        player.connection.sendPacket(new SPacketCustomPayload(
                SRHelpers.MESSAGE_CHANNEL,
                new PacketBuffer(Unpooled.wrappedBuffer(data))
        ));
    }

    private SRServerMessageEvent wrap(String channelName, EntityPlayerMP player, byte[] message) {
        return new SRServerMessageEvent() {
            @Override
            public SRServerPlayer getPlayer() {
                return wrapper.player(player);
            }

            @Override
            public byte[] getData() {
                return message;
            }

            @Override
            public String getChannel() {
                return channelName;
            }
        };
    }
}
