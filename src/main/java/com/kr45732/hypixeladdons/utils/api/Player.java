/*
 * Hypixel Addons - A customizable quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.utils.api;

import static com.kr45732.hypixeladdons.utils.Constants.HOTM_EXP_TO_LEVEL;
import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.playerFromUuid;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.skyblockProfilesFromUuid;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.structs.InvItem;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import com.kr45732.hypixeladdons.utils.weight.senither.Weight;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

public class Player {

	private JsonArray profilesArray;
	private int profileIndex;
	private JsonObject hypixelProfileJson;
	private boolean validPlayer = false;
	private String uuid;
	private String username;
	private String profileName;
	private String failCause = "Unknown fail cause";

	/* Constructors */
	public Player(String username) {
		if (usernameToUuid(username)) {
			return;
		}

		try {
			HypixelResponse response = skyblockProfilesFromUuid(uuid);
			if (response.isNotValid()) {
				failCause = response.failCause;
				return;
			}

			this.profilesArray = response.response.getAsJsonArray();
			if (getLatestProfile(profilesArray)) {
				return;
			}
		} catch (Exception e) {
			return;
		}

		this.validPlayer = true;
	}

	public Player(String username, String profileName) {
		if (usernameToUuid(username)) {
			return;
		}

		try {
			HypixelResponse response = skyblockProfilesFromUuid(uuid);
			if (response.isNotValid()) {
				failCause = response.failCause;
				return;
			}

			this.profilesArray = response.response.getAsJsonArray();
			if (profileIdFromName(profileName, profilesArray)) {
				failCause = failCause.equals("Unknown fail cause") ? "Invalid profile name" : failCause;
				return;
			}
		} catch (Exception e) {
			return;
		}

		this.validPlayer = true;
	}

	public Player(String uuid, String username, JsonElement outerProfileJson) {
		this.uuid = uuid;
		this.username = username;

		try {
			if (outerProfileJson == null) {
				return;
			}

			this.profilesArray = outerProfileJson.getAsJsonArray();
			if (getLatestProfile(profilesArray)) {
				return;
			}
		} catch (Exception e) {
			return;
		}

		this.validPlayer = true;
	}

	public Player(String uuid, String username, String profileName, JsonElement outerProfileJson) {
		this.uuid = uuid;
		this.username = username;

		try {
			if (outerProfileJson == null) {
				return;
			}

			this.profilesArray = outerProfileJson.getAsJsonArray();
			if (profileIdFromName(profileName, profilesArray)) {
				failCause = failCause.equals("Unknown fail cause") ? "Invalid profile name" : "";
				return;
			}
		} catch (Exception e) {
			return;
		}

		this.validPlayer = true;
	}

	/* Constructor helper methods */
	private boolean usernameToUuid(String username) {
		UsernameUuidStruct response = ApiHandler.usernameUuid(username);
		if (response.isNotValid()) {
			failCause = response.getFailCause();
			return true;
		}

		this.username = response.getUsername();
		this.uuid = response.getUuid();
		return false;
	}

	private boolean profileIdFromName(String profileName, JsonArray profilesArray) {
		try {
			for (int i = 0; i < profilesArray.size(); i++) {
				String currentProfileName = higherDepth(profilesArray.get(i), "cute_name").getAsString();
				if (currentProfileName.equalsIgnoreCase(profileName)) {
					this.profileName = currentProfileName;
					this.profileIndex = i;
					return false;
				}
			}
		} catch (Exception ignored) {}
		return true;
	}

	private boolean getLatestProfile(JsonArray profilesArray) {
		try {
			Instant lastProfileSave = Instant.EPOCH;
			for (int i = 0; i < profilesArray.size(); i++) {
				Instant lastSaveLoop;
				try {
					lastSaveLoop =
						Instant.ofEpochMilli(higherDepth(profilesArray.get(i), "members." + this.uuid + ".last_save").getAsLong());
				} catch (Exception e) {
					continue;
				}

				if (lastSaveLoop.isAfter(lastProfileSave)) {
					this.profileIndex = i;
					lastProfileSave = lastSaveLoop;
					this.profileName = higherDepth(profilesArray.get(i), "cute_name").getAsString();
				}
			}
			return false;
		} catch (Exception ignored) {}
		return true;
	}

	/* Getters */
	public JsonElement profileJson() {
		return higherDepth(profilesArray.get(profileIndex), "members." + this.uuid);
	}

	public String skyblockStatsLink() {
		return Utils.skyblockStatsLink(username, profileName);
	}

	public JsonElement getOuterProfileJson() {
		return profilesArray.get(profileIndex);
	}

	public boolean isValid() {
		return validPlayer;
	}

	public String getFailCause() {
		return failCause;
	}

	public String getUsername() {
		return username;
	}

	/* Bank and purse */
	public double getBankBalance() {
		return higherDepth(getOuterProfileJson(), "banking.balance", -1.0);
	}

	public double getPurseCoins() {
		return higherDepth(profileJson(), "coin_purse", 0.0);
	}

	/* Skills */
	public int getFarmingCapUpgrade() {
		return higherDepth(profileJson(), "jacob2.perks.farming_level_cap", 0);
	}

	public int getSkillMaxLevel(String skillName, WeightType weightType) {
		if (weightType == WeightType.LILY) {
			return 60;
		}

		if (skillName.equals("hotm")) {
			return 7;
		}

		int maxLevel = higherDepth(getLevelingJson(), "leveling_caps." + skillName, 0);

		if (skillName.equals("farming")) {
			maxLevel = weightType == WeightType.SENITHER ? 60 : maxLevel + getFarmingCapUpgrade();
		}

		return maxLevel;
	}

	public double getSkillXp(String skillName) {
		try {
			return skillName.equals("catacombs") ? getCatacombs().getTotalExp() : getSkill(skillName).getTotalExp();
		} catch (Exception e) {
			return -1;
		}
	}

	public SkillsStruct getSkill(String skillName) {
		return getSkill(skillName, WeightType.NONE);
	}

	public SkillsStruct getSkill(String skillName, WeightType weightType) {
		try {
			return skillInfoFromExp(higherDepth(profileJson(), "experience_skill_" + skillName).getAsLong(), skillName, weightType);
		} catch (Exception e) {
			return null;
		}
	}

	public SkillsStruct skillInfoFromExp(long skillExp, String skill) {
		return skillInfoFromExp(skillExp, skill, WeightType.NONE);
	}

	public SkillsStruct skillInfoFromExp(long skillExp, String skill, WeightType weightType) {
		JsonArray skillsTable;
		switch (skill) {
			case "catacombs":
				skillsTable = higherDepth(getLevelingJson(), "catacombs").getAsJsonArray();
				break;
			case "runecrafting":
				skillsTable = higherDepth(getLevelingJson(), "runecrafting_xp").getAsJsonArray();
				break;
			case "hotm":
				skillsTable = HOTM_EXP_TO_LEVEL;
				break;
			default:
				skillsTable = higherDepth(getLevelingJson(), "leveling_xp").getAsJsonArray();
				break;
		}

		int maxLevel = getSkillMaxLevel(skill, weightType);

		long xpTotal = 0L;
		int level = 1;
		for (int i = 0; i < maxLevel; i++) {
			xpTotal += skillsTable.get(i).getAsLong();

			if (xpTotal > skillExp) {
				xpTotal -= skillsTable.get(i).getAsLong();
				break;
			} else {
				level = (i + 1);
			}
		}

		long xpCurrent = (long) Math.floor(skillExp - xpTotal);
		long xpForNext = 0;
		if (level < maxLevel) xpForNext = (long) Math.ceil(skillsTable.get(level).getAsLong());

		if (skillExp == 0) {
			level = 0;
			xpForNext = 0;
		}

		double progress = xpForNext > 0 ? Math.max(0, Math.min(((double) xpCurrent) / xpForNext, 1)) : 0;

		return new SkillsStruct(skill, level, maxLevel, skillExp, xpCurrent, xpForNext, progress);
	}

	public SkillsStruct getHOTM() {
		return skillInfoFromExp(higherDepth(profileJson(), "mining_core.experience").getAsLong(), "hotm");
	}

	/* Slayer */
	public int getTotalSlayer() {
		return getSlayer("sven") + getSlayer("rev") + getSlayer("tara") + getSlayer("enderman");
	}

	public int getSlayerBossKills(String slayerName, int tier) {
		return higherDepth(profileJson(), "slayer_bosses." + slayerName + ".boss_kills_tier_" + tier, 0);
	}

	public int getSlayer(String slayerName) {
		JsonElement profileSlayer = higherDepth(profileJson(), "slayer_bosses");
		switch (slayerName) {
			case "sven":
				return higherDepth(profileSlayer, "wolf.xp", 0);
			case "rev":
				return higherDepth(profileSlayer, "zombie.xp", 0);
			case "tara":
				return higherDepth(profileSlayer, "spider.xp", 0);
			case "enderman":
				return higherDepth(profileSlayer, "enderman.xp", 0);
		}
		return 0;
	}

	public int getSlayerLevel(String slayerName) {
		switch (slayerName) {
			case "sven":
				JsonArray wolfLevelArray = higherDepth(getLevelingJson(), "slayer_xp.wolf").getAsJsonArray();
				int wolfXp = getSlayer("sven");
				int prevWolfLevel = 0;
				for (int i = 0; i < wolfLevelArray.size(); i++) {
					if (wolfXp >= wolfLevelArray.get(i).getAsInt()) {
						prevWolfLevel = i + 1;
					} else {
						break;
					}
				}
				return prevWolfLevel;
			case "rev":
				JsonArray zombieLevelArray = higherDepth(getLevelingJson(), "slayer_xp.zombie").getAsJsonArray();
				int zombieXp = getSlayer("rev");
				int prevZombieMax = 0;
				for (int i = 0; i < zombieLevelArray.size(); i++) {
					if (zombieXp >= zombieLevelArray.get(i).getAsInt()) {
						prevZombieMax = i + 1;
					} else {
						break;
					}
				}
				return prevZombieMax;
			case "tara":
				JsonArray spiderLevelArray = higherDepth(getLevelingJson(), "slayer_xp.spider").getAsJsonArray();
				int spiderXp = getSlayer("tara");
				int prevSpiderMax = 0;
				for (int i = 0; i < spiderLevelArray.size(); i++) {
					if (spiderXp >= spiderLevelArray.get(i).getAsInt()) {
						prevSpiderMax = i + 1;
					} else {
						break;
					}
				}
				return prevSpiderMax;
			case "enderman":
				JsonArray endermanLevelArray = higherDepth(getLevelingJson(), "slayer_xp.enderman").getAsJsonArray();
				int endermanXp = getSlayer("enderman");
				int prevEndermanMax = 0;
				for (int i = 0; i < endermanLevelArray.size(); i++) {
					if (endermanXp >= endermanLevelArray.get(i).getAsInt()) {
						prevEndermanMax = i + 1;
					} else {
						break;
					}
				}
				return prevEndermanMax;
		}

		return 0;
	}

	/* Dungeons */
	public String getSelectedDungeonClass() {
		try {
			return higherDepth(profileJson(), "dungeons.selected_dungeon_class").getAsString();
		} catch (Exception e) {
			return "none";
		}
	}

	public Set<String> getItemsPlayerHas(List<String> items) {
		Map<Integer, InvItem> invItemMap = getInventoryMap();
		if (invItemMap == null) {
			return null;
		}

		Collection<InvItem> itemsMap = new ArrayList<>(invItemMap.values());
		itemsMap.addAll(new ArrayList<>(getEnderChestMap().values()));
		itemsMap.addAll(new ArrayList<>(getStorageMap().values()));
		Set<String> itemsPlayerHas = new HashSet<>();

		for (InvItem item : itemsMap) {
			if (item == null) {
				continue;
			}

			if (!item.getBackpackItems().isEmpty()) {
				List<InvItem> backpackItems = item.getBackpackItems();
				for (InvItem backpackItem : backpackItems) {
					if (backpackItem == null) {
						continue;
					}

					if (items.contains(backpackItem.getId())) {
						itemsPlayerHas.add(capitalizeString(backpackItem.getId().toLowerCase().replace("_", " ")));
					}
				}
			} else {
				if (items.contains(item.getId())) {
					itemsPlayerHas.add(capitalizeString(item.getId().toLowerCase().replace("_", " ")));
				}
			}
		}

		return itemsPlayerHas;
	}

	public String getFastestF7Time() {
		try {
			int f7TimeMilliseconds = higherDepth(profileJson(), "dungeons.dungeon_types.catacombs.fastest_time_s_plus.7").getAsInt();
			int minutes = f7TimeMilliseconds / 1000 / 60;
			int seconds = f7TimeMilliseconds / 1000 % 60;
			return minutes + ":" + (seconds >= 10 ? seconds : "0" + seconds);
		} catch (Exception e) {
			return "None";
		}
	}

	public int getDungeonSecrets() {
		if (hypixelProfileJson == null) {
			hypixelProfileJson = playerFromUuid(uuid).response.getAsJsonObject();
		}

		return higherDepth(hypixelProfileJson, "achievements.skyblock_treasure_hunter", 0);
	}

	public SkillsStruct getDungeonClass(String className) {
		return skillInfoFromExp(higherDepth(profileJson(), "dungeons.player_classes." + className + ".experience", 0L), "catacombs");
	}

	public SkillsStruct getCatacombs() {
		return skillInfoFromExp(higherDepth(profileJson(), "dungeons.dungeon_types.catacombs.experience", 0L), "catacombs");
	}

	/* Inventory */
	public Map<Integer, InvItem> getInventoryMap() {
		try {
			String contents = higherDepth(profileJson(), "inv_contents.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return getGenericInventoryMap(parsedContents);
		} catch (Exception ignored) {}
		return null;
	}

	public Map<Integer, InvItem> getStorageMap() {
		try {
			Map<Integer, InvItem> storageMap = new HashMap<>();
			int counter = 1;
			for (Map.Entry<String, JsonElement> bp : higherDepth(profileJson(), "backpack_contents").getAsJsonObject().entrySet()) {
				Collection<InvItem> curBpMap = getGenericInventoryMap(
					NBTReader.readBase64(higherDepth(bp.getValue(), "data").getAsString())
				)
					.values();
				for (InvItem itemSlot : curBpMap) {
					storageMap.put(counter, itemSlot);
					counter++;
				}
			}

			return storageMap;
		} catch (Exception ignored) {}
		return null;
	}

	public Map<Integer, InvItem> getEnderChestMap() {
		try {
			String contents = higherDepth(profileJson(), "ender_chest_contents.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return getGenericInventoryMap(parsedContents);
		} catch (Exception ignored) {}
		return null;
	}

	public List<NBTCompound> getInventoryArmorNBT() {
		try {
			String contents = higherDepth(profileJson(), "inv_armor.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return Lists.reverse(parsedContents.getList("i").stream().map(item -> ((NBTCompound) item)).collect(Collectors.toList()));
		} catch (Exception ignored) {}
		return null;
	}

	/* Miscellaneous */
	public double getWeight() {
		return new Weight(this, true).getTotalWeight().getRaw();
	}

	public String getFormattedUsername() {
		if (hypixelProfileJson == null) {
			this.hypixelProfileJson = playerFromUuid(uuid).response.getAsJsonObject();
		}

		return Utils.getFormattedUsername(hypixelProfileJson);
	}

	public IChatComponent defaultComponent() {
		return empty().appendSibling(getLink());
	}

	public enum WeightType {
		NONE,
		SENITHER,
		LILY,
	}

	public IChatComponent getLink() {
		return new ChatText(labelWithDesc("Player", C.UNDERLINE + username))
			.setClickEvent(ClickEvent.Action.OPEN_URL, skyblockStatsLink())
			.build();
	}

	public int getFairySouls() {
		return higherDepth(profileJson(), "fairy_souls_collected", 0);
	}

	@Override
	public String toString() {
		return (
			"Player{" +
			"validPlayer=" +
			validPlayer +
			", playerUuid='" +
			uuid +
			'\'' +
			", playerUsername='" +
			username +
			'\'' +
			", profileName='" +
			profileName +
			'\'' +
			'}'
		);
	}
}
