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
import net.skinsrestorer.shared.codec.SRInputReader;
import net.skinsrestorer.shared.codec.SRServerPluginMessage;

public final class Forge112PluginMessages {
    public static byte[] copyPayload(ByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);
        return data;
    }

    public static SRServerPluginMessage decode(byte[] data) {
        return SRServerPluginMessage.CODEC.read(new SRInputReader(data));
    }

    private Forge112PluginMessages() {
    }
}
