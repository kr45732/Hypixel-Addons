/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
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

package com.kr45732.hypixeladdons.features;

import static com.kr45732.hypixeladdons.utils.Utils.isOnHypixel;

import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.StringUtils;

public class MysteryBoxOverlay {

	public static MysteryBoxOverlay INSTANCE = new MysteryBoxOverlay();
	private Map<ItemStack, Integer> itemStackToOriginalSlot = new HashMap<>();

	public boolean onStackClick(int windowId, int slotId, int mouseButtonClicked, int mode) {
		if (!isGuiOpen()) {
			return false;
		}

		int counter = 0;
		int value = -1;
		for (Map.Entry<ItemStack, Integer> integerMysteryBoxEntry : itemStackToOriginalSlot.entrySet()) {
			if (counter == slotId) {
				value = integerMysteryBoxEntry.getValue();
			}
			counter++;
		}

		if (value == -1) {
			return false;
		}

		Minecraft
			.getMinecraft()
			.playerController.windowClick(windowId, value, mouseButtonClicked, mode, Minecraft.getMinecraft().thePlayer);
		return true;
	}

	public ItemStack overrideStack(IInventory inventory, int slotIndex, ItemStack stack) {
		if (!isGuiOpen()) {
			return null;
		}

		if (stack != null && stack.getDisplayName() != null) {
			itemStackToOriginalSlot =
				itemStackToOriginalSlot
					.entrySet()
					.stream()
					.sorted(
						Comparator.comparingInt(
							box -> {
								NBTTagList lore = box.getKey().getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
								for (int i = 0; i < lore.tagCount(); i++) {
									if (ConfigUtils.mysteryBoxSortType.equals("expiry")) {
										if (lore.getStringTagAt(i).startsWith("§7§cExpires in ")) {
											String[] endingStringArr = lore.getStringTagAt(i).split("§7§cExpires in ")[1].split(", ");
											int hoursLeft = 0;
											for (String endingString : endingStringArr) {
												int number = 0;
												int factor = 1;

												try {
													number = Integer.parseInt(endingString.replaceAll("\\D", ""));
												} catch (Exception ignored) {}

												if (endingString.contains("week")) {
													factor = 7 * 24;
												} else if (endingString.contains("days")) {
													factor = 24;
												}

												hoursLeft += number * factor;
											}

											return hoursLeft;
										}
									} else {
										if (lore.getStringTagAt(i).startsWith("§7§7Quality: §e")) {
											String starsStr = lore.getStringTagAt(i).split("§7§7Quality: §e")[1];
											return starsStr.contains("§7") ? starsStr.indexOf("§7") : 5;
										}
									}
								}

								return ConfigUtils.mysteryBoxSortType.equals("expiry") ? 0 : 6;
							}
						)
					)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;
			ContainerChest container = (ContainerChest) chest.inventorySlots;
			IInventory lower = container.getLowerChestInventory();

			if (lower != inventory) {
				return null;
			}

			try {
				return new ArrayList<>(itemStackToOriginalSlot.keySet()).get(slotIndex);
			} catch (Exception ignored) {}
		}
		return null;
	}

	public void setSlotPacket(S2FPacketSetSlot packet) {
		if (!isGuiOpen()) {
			return;
		}

		if (getCurrentWindowId() == -1 || getCurrentWindowId() != packet.func_149175_c()) {
			return;
		}

		ItemStack stack = packet.func_149174_e();
		if (StringUtils.stripControlCodes(stack.getDisplayName()).endsWith("Mystery Box")) {
			NBTTagList lore = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
			for (int i = 0; i < lore.tagCount(); i++) {
				if (lore.getStringTagAt(i).startsWith("§7§cExpires in ")) {
					itemStackToOriginalSlot.put(stack, packet.func_149173_d());
					return;
				}
			}
		}
	}

	private int getCurrentWindowId() {
		if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) {
			return -1;
		}

		GuiChest chest = (GuiChest) Minecraft.getMinecraft().currentScreen;

		return chest.inventorySlots.windowId;
	}

	public static boolean isGuiOpen() {
		if (!ConfigUtils.toggleMysteryBoxSorter) {
			return false;
		}

		if (!isOnHypixel() || !(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) {
			return false;
		}

		String containerName =
			((ContainerChest) ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots).getLowerChestInventory()
				.getDisplayName()
				.getUnformattedText();
		return containerName != null && containerName.equals("Mystery Vault");
	}
}
