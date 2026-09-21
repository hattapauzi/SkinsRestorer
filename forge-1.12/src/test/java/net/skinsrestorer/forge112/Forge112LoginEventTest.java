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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Forge112LoginEventTest {
    @Test
    void setResultPropertyGoesToSinkNotProfile() {
        GameProfile profile = new GameProfile(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Alex");
        List<SkinProperty> applied = new ArrayList<>();
        Forge112LoginEvent event = new Forge112LoginEvent(profile, r -> r.run(), applied::add);
        assertFalse(event.hasOnlineProperties());
        assertEquals("Alex", event.getPlayerName());
        event.setResultProperty(SkinProperty.of("v", "s"));
        assertEquals(1, applied.size());
        assertEquals("v", applied.get(0).getValue());
        assertTrue(ForgeGameProfiles.readTextures(profile).isEmpty());
    }

    @Test
    void runAsyncUsesProvidedExecutor() {
        AtomicBoolean delegated = new AtomicBoolean();
        Forge112LoginEvent event = new Forge112LoginEvent(
                new GameProfile(UUID.randomUUID(), "a"),
                r -> delegated.set(true),
                p -> {
                }
        );
        event.runAsync(() -> {
        });
        assertTrue(delegated.get());
    }
}
