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
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForgeGameProfilesTest {
    @Test
    void applyTexturesReplacesTexturesAndKeepsCape() {
        GameProfile profile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Steve");
        profile.getProperties().put("textures", new Property("textures", "old", "old-sig"));
        profile.getProperties().put("cape", new Property("cape", "cape-val", "cape-sig"));

        SkinProperty next = SkinProperty.of("new-val", "new-sig");
        ForgeGameProfiles.applyTextures(profile, next);

        assertEquals(1, profile.getProperties().get("textures").size());
        Property textures = profile.getProperties().get("textures").iterator().next();
        assertEquals("new-val", textures.getValue());
        assertEquals("new-sig", textures.getSignature());
        assertEquals(1, profile.getProperties().get("cape").size());
        assertTrue(ForgeGameProfiles.readTextures(profile).isPresent());
        assertEquals("new-val", ForgeGameProfiles.readTextures(profile).get().getValue());
    }

    @Test
    void readTexturesEmptyWhenMissing() {
        GameProfile profile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Alex");
        assertTrue(ForgeGameProfiles.readTextures(profile).isEmpty());
    }
}
