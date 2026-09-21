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
import com.mojang.authlib.properties.Property;
import net.skinsrestorer.api.property.SkinProperty;

import java.util.Optional;

public final class ForgeGameProfiles {
    public static GameProfile applyTextures(GameProfile profile, SkinProperty property) {
        profile.getProperties().removeAll(SkinProperty.TEXTURES_NAME);
        profile.getProperties().put(
                SkinProperty.TEXTURES_NAME,
                new Property(SkinProperty.TEXTURES_NAME, property.getValue(), property.getSignature())
        );
        return profile;
    }

    public static Optional<SkinProperty> readTextures(GameProfile profile) {
        return profile.getProperties().get(SkinProperty.TEXTURES_NAME).stream()
                .map(p -> SkinProperty.tryParse(p.getName(), p.getValue(), p.getSignature()))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private ForgeGameProfiles() {
    }
}
