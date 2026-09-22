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
package net.skinsrestorer.forge112.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.skinsrestorer.shared.utils.SRHelpers;

public final class SkullNbt {
    public static NBTTagCompound applyOwner(ItemStack stack, String textureHash) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setTag("SkullOwner", skullOwner(
                SkullOwnerData.fromTextureValue(SRHelpers.encodeHashToTexturesValue(textureHash))));
        stack.setTagCompound(tag);
        return tag;
    }

    public static NBTTagCompound skullOwner(SkullOwnerData data) {
        NBTTagCompound tex = new NBTTagCompound();
        tex.setString("Value", data.textureValue());
        NBTTagList list = new NBTTagList();
        list.appendTag(tex);
        NBTTagCompound properties = new NBTTagCompound();
        properties.setTag(data.propertyName(), list);
        NBTTagCompound owner = new NBTTagCompound();
        owner.setString("Id", "00000000-0000-0000-0000-000000000000");
        owner.setString("Name", "SR");
        owner.setTag("Properties", properties);
        return owner;
    }

    private SkullNbt() {
    }
}
