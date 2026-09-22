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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.shared.codec.SRInputReader;
import net.skinsrestorer.shared.codec.SRServerPluginMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Forge112PluginMessagesTest {
    @Test
    void copyPayloadDoesNotAliasBuffer() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(new byte[]{1, 2, 3});
        byte[] copy = Forge112PluginMessages.copyPayload(buf);
        assertArrayEquals(new byte[]{1, 2, 3}, copy);
        buf.setByte(0, 9);
        assertEquals(1, copy[0]);
        buf.release();
    }

    @Test
    void roundTripSkinUpdateV3() throws Exception {
        SkinProperty property = SkinProperty.of("v", "s");
        SRServerPluginMessage original = new SRServerPluginMessage(
                new SRServerPluginMessage.SkinUpdateV3ChannelPayload(property, java.util.Optional.empty())
        );
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        net.skinsrestorer.shared.codec.SROutputWriter writer =
                new net.skinsrestorer.shared.codec.SROutputWriter(new java.io.DataOutputStream(baos));
        SRServerPluginMessage.CODEC.write(writer, original);
        SRServerPluginMessage read = SRServerPluginMessage.CODEC.read(new SRInputReader(baos.toByteArray()));
        assertInstanceOf(SRServerPluginMessage.SkinUpdateV3ChannelPayload.class, read.channelPayload());
    }

    @Test
    void malformedBytesDoNotThrowFromCopy() {
        byte[] malformed = new byte[]{0};
        ByteBuf buf = Unpooled.wrappedBuffer(malformed);
        byte[] copy = Forge112PluginMessages.copyPayload(buf);
        assertArrayEquals(malformed, copy);
        buf.setByte(0, 9);
        assertEquals(0, copy[0]);
        buf.release();
    }

    @Test
    void malformedBytesThrowOnDecode() {
        assertThrows(RuntimeException.class, () -> Forge112PluginMessages.decode(new byte[]{0}));
    }
}
