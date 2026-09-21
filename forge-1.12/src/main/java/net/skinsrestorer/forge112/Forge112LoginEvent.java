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

import com.mojang.authlib.GameProfile;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.shared.listeners.event.SRLoginProfileEvent;

import java.util.UUID;
import java.util.function.Consumer;

public final class Forge112LoginEvent implements SRLoginProfileEvent<Void> {
    private final GameProfile gameProfile;
    private final Consumer<Runnable> asyncRunner;
    private final Consumer<SkinProperty> propertySink;

    public Forge112LoginEvent(
            GameProfile gameProfile,
            Consumer<Runnable> asyncRunner,
            Consumer<SkinProperty> propertySink
    ) {
        this.gameProfile = gameProfile;
        this.asyncRunner = asyncRunner;
        this.propertySink = propertySink;
    }

    @Override
    public boolean hasOnlineProperties() {
        return ForgeGameProfiles.readTextures(gameProfile).isPresent();
    }

    @Override
    public UUID getPlayerUniqueId() {
        return gameProfile.getId();
    }

    @Override
    public String getPlayerName() {
        return gameProfile.getName();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setResultProperty(SkinProperty property) {
        propertySink.accept(property);
    }

    @Override
    public Void runAsync(Runnable runnable) {
        asyncRunner.accept(runnable);
        return null;
    }
}
