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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.skinsrestorer.forge112.Forge112ComponentHelper;
import net.skinsrestorer.shared.gui.ClickEventType;
import net.skinsrestorer.shared.gui.GUIManager;
import net.skinsrestorer.shared.gui.SRInventory;
import net.skinsrestorer.shared.utils.SRHelpers;

import java.util.HashMap;
import java.util.Map;

public class Forge112GUI implements GUIManager<Forge112OpenGUI> {
    @Override
    public Forge112OpenGUI createGUI(SRInventory srInventory) {
        InventoryBasic inventory = new InventoryBasic(
                Forge112ComponentHelper.toLegacy(srInventory.title()),
                false,
                srInventory.rows() * 9
        );
        Map<Integer, Map<ClickEventType, SRInventory.ClickEventAction>> handlers = new HashMap<>();
        for (Map.Entry<Integer, SRInventory.Item> entry : srInventory.items().entrySet()) {
            inventory.setInventorySlotContents(entry.getKey(), createItem(entry.getValue()));
            handlers.put(entry.getKey(), entry.getValue().clickHandlers());
        }
        return new Forge112OpenGUI(inventory, handlers);
    }

    private ItemStack createItem(SRInventory.Item entry) {
        Item item = switch (entry.materialType()) {
            case DIRT -> Item.getItemFromBlock(Blocks.DIRT);
            case SKULL -> Items.SKULL;
            case ARROW -> Items.ARROW;
            case BARRIER -> Item.getItemFromBlock(Blocks.BARRIER);
            case BOOKSHELF -> Item.getItemFromBlock(Blocks.BOOKSHELF);
            case ENDER_EYE -> Items.ENDER_EYE;
            case ENCHANTING_TABLE -> Item.getItemFromBlock(Blocks.ENCHANTING_TABLE);
        };
        ItemStack stack = new ItemStack(item);
        if (entry.materialType() == SRInventory.MaterialType.SKULL) {
            stack.setItemDamage(3);
            entry.textureHash().ifPresent(hash -> {
                NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
                tag.setTag("SkullOwner", SkullNbt.skullOwner(
                        SkullOwnerData.fromTextureValue(SRHelpers.encodeHashToTexturesValue(hash))));
                stack.setTagCompound(tag);
            });
        }
        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", Forge112ComponentHelper.toLegacy(entry.displayName()));
        NBTTagList lore = new NBTTagList();
        entry.lore().forEach(line -> lore.appendTag(new NBTTagString(Forge112ComponentHelper.toLegacy(line))));
        display.setTag("Lore", lore);
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setTag("display", display);
        if (entry.enchantmentGlow()) {
            tag.setInteger("HideFlags", tag.getInteger("HideFlags") | 1);
        }
        stack.setTagCompound(tag);
        if (entry.enchantmentGlow()) {
            stack.addEnchantment(Enchantment.getEnchantmentByLocation("lure"), 1);
        }
        return stack;
    }
}
