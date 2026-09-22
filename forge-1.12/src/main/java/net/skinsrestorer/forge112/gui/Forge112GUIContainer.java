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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.skinsrestorer.forge112.wrapper.WrapperForge;
import net.skinsrestorer.shared.gui.ActionDataCallback;
import net.skinsrestorer.shared.gui.ClickEventType;
import net.skinsrestorer.shared.gui.SRInventory;

import java.util.HashMap;
import java.util.Map;

public class Forge112GUIContainer extends ContainerChest {
    private final int chestSize;
    private final ActionDataCallback dataCallback;
    private final WrapperForge wrapper;
    private final Map<Integer, Map<ClickEventType, SRInventory.ClickEventAction>> handlers = new HashMap<>();

    public Forge112GUIContainer(
            InventoryPlayer playerInv,
            InventoryBasic chest,
            EntityPlayer player,
            ActionDataCallback dataCallback,
            WrapperForge wrapper,
            Map<Integer, Map<ClickEventType, SRInventory.ClickEventAction>> handlers
    ) {
        super(playerInv, chest, player);
        this.chestSize = chest.getSizeInventory();
        this.dataCallback = dataCallback;
        this.wrapper = wrapper;
        this.handlers.putAll(handlers);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0 && slotId < chestSize && player instanceof EntityPlayerMP mp) {
            Map<ClickEventType, SRInventory.ClickEventAction> slotHandlers = handlers.get(slotId);
            if (slotHandlers != null) {
                ClickEventType type = clickType(clickTypeIn, dragType);
                SRInventory.ClickEventAction action = slotHandlers.get(type);
                if (action != null) {
                    dataCallback.handle(wrapper.player(mp), action);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static ClickEventType clickType(ClickType clickTypeIn, int dragType) {
        if (clickTypeIn == ClickType.PICKUP && dragType == 0) {
            return ClickEventType.LEFT;
        }
        if (clickTypeIn == ClickType.PICKUP && dragType == 1) {
            return ClickEventType.RIGHT;
        }
        if (clickTypeIn == ClickType.QUICK_MOVE && dragType == 0) {
            return ClickEventType.SHIFT_LEFT;
        }
        return ClickEventType.OTHER;
    }
}
