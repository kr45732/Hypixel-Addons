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

package com.kr45732.hypixeladdons.utils.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.nullicorn.nedit.type.NBTCompound;
import net.minecraft.util.StringUtils;

public class InvItem {

	private String name;
	private String lore;
	private int count = 1;
	private String modifier;
	private String creationOrigin;
	private String id;
	private String creationTimestamp;
	private List<String> enchantsFormatted = new ArrayList<>();
	private int hbpCount = 0;
	private int fumingCount = 0;
	private boolean recombobulated = false;
	private final List<String> extraStats = new ArrayList<>();
	private final List<InvItem> backpackItems = new ArrayList<>();
	private String rarity;
	private int dungeonFloor = 0;
	private NBTCompound nbtTag;

	public void setHbpCount(int hbpCount) {
		if (hbpCount > 10) {
			this.fumingCount = hbpCount - 10;
			this.hbpCount = 10;
		} else {
			this.hbpCount = hbpCount;
		}
	}

	public void addExtraValue(String itemId) {
		extraStats.add(itemId);
	}

	public void setLore(String lore) {
		this.lore = lore;
		if (lore != null) {
			String[] loreArr = StringUtils.stripControlCodes(lore).split("\n");
			this.rarity = loreArr[loreArr.length - 1].trim().split(" ")[0];
		}
	}

	public void setBackpackItems(Collection<InvItem> backpackItems) {
		this.backpackItems.clear();
		this.backpackItems.addAll(backpackItems);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLore() {
		return lore;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getCreationOrigin() {
		return creationOrigin;
	}

	public void setCreationOrigin(String creationOrigin) {
		this.creationOrigin = creationOrigin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(String creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public List<String> getEnchantsFormatted() {
		return enchantsFormatted;
	}

	public void setEnchantsFormatted(List<String> enchantsFormatted) {
		this.enchantsFormatted = enchantsFormatted;
	}

	public int getHbpCount() {
		return hbpCount;
	}

	public int getFumingCount() {
		return fumingCount;
	}

	public boolean isRecombobulated() {
		return recombobulated;
	}

	public void setRecombobulated(boolean recombobulated) {
		this.recombobulated = recombobulated;
	}

	public List<String> getExtraStats() {
		return extraStats;
	}

	public List<InvItem> getBackpackItems() {
		return backpackItems;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public int getDungeonFloor() {
		return dungeonFloor;
	}

	public void setDungeonFloor(int dungeonFloor) {
		this.dungeonFloor = dungeonFloor;
	}

	public NBTCompound getNbtTag() {
		return nbtTag;
	}

	public void setNbtTag(NBTCompound nbtTag) {
		this.nbtTag = nbtTag;
	}
}
