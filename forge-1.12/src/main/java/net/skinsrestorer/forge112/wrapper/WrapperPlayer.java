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
package net.skinsrestorer.forge112.wrapper;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.skinsrestorer.forge112.network.Forge112ServerMessageListener;
import net.skinsrestorer.shared.subjects.SRPlayer;
import net.skinsrestorer.shared.subjects.SRServerPlayer;

import java.util.UUID;

@SuperBuilder
public class WrapperPlayer extends WrapperCommandSender implements SRServerPlayer {
    private final @NonNull EntityPlayerMP player;

    @Override
    public <S> S getAs(Class<S> senderClass) {
        if (senderClass.isAssignableFrom(EntityPlayerMP.class)) {
            return senderClass.cast(player);
        }
        return super.getAs(senderClass);
    }

    @Override
    public UUID getUniqueId() {
        return player.getGameProfile().getId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean canSee(SRPlayer other) {
        return true;
    }

    @Override
    public void closeInventory() {
        player.closeScreen();
    }

    @Override
    public void sendToMessageChannel(byte[] data) {
        Forge112ServerMessageListener.send(player, data);
    }
}
