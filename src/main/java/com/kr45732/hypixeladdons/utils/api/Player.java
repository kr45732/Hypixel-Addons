/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021-2021 kr45732
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
import com.kr45732.hypixeladdons.utils.weight.Weight;
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
	private JsonObject hypixelJson;
	private boolean validPlayer = false;
	private String playerUuid;
	private String playerUsername;
	private String profileName;
	private String failCause = "Unknown fail cause";

	/* Constructors */
	public Player(String username) {
		if (usernameToUuid(username)) {
			return;
		}

		try {
			HypixelResponse response = skyblockProfilesFromUuid(playerUuid);
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
			HypixelResponse response = skyblockProfilesFromUuid(playerUuid);
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

	public Player(String playerUuid, String playerUsername, JsonElement outerProfileJson) {
		this.playerUuid = playerUuid;
		this.playerUsername = playerUsername;

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

	public Player(String playerUuid, String playerUsername, String profileName, JsonElement outerProfileJson) {
		this.playerUuid = playerUuid;
		this.playerUsername = playerUsername;

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

	/* Getters */
	public JsonElement profileJson() {
		return Utils.higherDepth(profilesArray.get(profileIndex), "members." + this.playerUuid);
	}

	public String skyblockStatsLink() {
		return Utils.skyblockStatsLink(playerUsername, profileName);
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
		return playerUsername;
	}

	/* Bank and purse */
	public double getBankBalance() {
		try {
			return Utils.higherDepth(getOuterProfileJson(), "banking.balance").getAsDouble();
		} catch (Exception e) {
			return -1;
		}
	}

	public double getPurseCoins() {
		try {
			return Utils.higherDepth(profileJson(), "coin_purse").getAsLong();
		} catch (Exception e) {
			return -1;
		}
	}

	/* Skills */
	public int getFarmingCapUpgrade() {
		try {
			return Utils.higherDepth(profileJson(), "jacob2.perks.farming_level_cap").getAsInt();
		} catch (Exception e) {
			return 0;
		}
	}

	public int getSkillMaxLevel(String skillName, boolean isWeight) {
		int maxLevel = Utils.higherDepth(Utils.getLevelingJson(), "leveling_caps." + skillName).getAsInt();

		if (skillName.equals("farming")) {
			maxLevel = isWeight ? 60 : maxLevel + getFarmingCapUpgrade();
		}

		return maxLevel;
	}

	public double getSkillXp(JsonElement profile, String skillName) {
		try {
			if (skillName.equals("catacombs")) {
				return Utils.higherDepth(profile, "dungeons.dungeon_types.catacombs.experience").getAsDouble();
			}
			return Utils.higherDepth(profile, "experience_skill_" + skillName).getAsDouble();
		} catch (Exception ignored) {}
		return -1;
	}

	public SkillsStruct getSkill(String skillName) {
		return getSkill(profileJson(), skillName);
	}

	public SkillsStruct getSkill(JsonElement profile, String skillName) {
		return getSkill(profile, skillName, false);
	}

	public SkillsStruct getSkill(JsonElement profile, String skillName, boolean isWeight) {
		try {
			double skillExp = Utils.higherDepth(profile, "experience_skill_" + skillName).getAsDouble();
			return skillInfoFromExp(skillExp, skillName, isWeight);
		} catch (Exception ignored) {}
		return null;
	}

	public SkillsStruct skillInfoFromExp(double skillExp, String skill) {
		return skillInfoFromExp(skillExp, skill, false);
	}

	public SkillsStruct skillInfoFromExp(double skillExp, String skill, boolean isWeight) {
		JsonArray skillsTable;
		if (skill.equals("catacombs")) {
			skillsTable = Utils.higherDepth(Utils.getLevelingJson(), "catacombs").getAsJsonArray();
		} else if (skill.equals("runecrafting")) {
			skillsTable = Utils.higherDepth(Utils.getLevelingJson(), "runecrafting_xp").getAsJsonArray();
		} else {
			skillsTable = Utils.higherDepth(Utils.getLevelingJson(), "leveling_xp").getAsJsonArray();
		}

		int maxLevel = getSkillMaxLevel(skill, isWeight);

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

		double progress = xpForNext > 0 ? Math.max(0, Math.min(((double) xpCurrent) / xpForNext, 1)) : 0;

		return new SkillsStruct(skill, level, maxLevel, (long) skillExp, xpCurrent, xpForNext, progress);
	}

	/* Slayer */
	public int getTotalSlayer() {
		return getTotalSlayer(profileJson());
	}

	public int getTotalSlayer(JsonElement profile) {
		return getSlayer(profile, "sven") + getSlayer(profile, "rev") + getSlayer(profile, "tara") + getSlayer(profile, "enderman");
	}

	public int getSlayerBossKills(String slayerName, int tier) {
		return Utils.higherDepth(profileJson(), "slayer_bosses." + slayerName + ".boss_kills_tier_" + tier, 0);
	}

	public int getSlayer(String slayerName) {
		return getSlayer(profileJson(), slayerName);
	}

	public int getSlayer(JsonElement profile, String slayerName) {
		JsonElement profileSlayer = Utils.higherDepth(profile, "slayer_bosses");
		switch (slayerName) {
			case "sven":
				return Utils.higherDepth(profileSlayer, "wolf.xp", 0);
			case "rev":
				return Utils.higherDepth(profileSlayer, "zombie.xp", 0);
			case "tara":
				return Utils.higherDepth(profileSlayer, "spider.xp", 0);
			case "enderman":
				return Utils.higherDepth(profileSlayer, "enderman.xp", 0);
		}
		return -1;
	}

	public int getSlayerLevel(String slayerName) {
		switch (slayerName) {
			case "sven":
				JsonArray wolfLevelArray = Utils.higherDepth(Utils.getLevelingJson(), "slayer_xp.wolf").getAsJsonArray();
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
				JsonArray zombieLevelArray = Utils.higherDepth(Utils.getLevelingJson(), "slayer_xp.zombie").getAsJsonArray();
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
				JsonArray spiderLevelArray = Utils.higherDepth(Utils.getLevelingJson(), "slayer_xp.spider").getAsJsonArray();
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
				JsonArray endermanLevelArray = Utils.higherDepth(Utils.getLevelingJson(), "slayer_xp.enderman").getAsJsonArray();
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
			return Utils.higherDepth(profileJson(), "dungeons.selected_dungeon_class").getAsString();
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
						itemsPlayerHas.add(Utils.capitalizeString(backpackItem.getId().toLowerCase().replace("_", " ")));
					}
				}
			} else {
				if (items.contains(item.getId())) {
					itemsPlayerHas.add(Utils.capitalizeString(item.getId().toLowerCase().replace("_", " ")));
				}
			}
		}

		return itemsPlayerHas;
	}

	public String getFastestF7Time() {
		try {
			int f7TimeMilliseconds = Utils.higherDepth(profileJson(), "dungeons.dungeon_types.catacombs.fastest_time_s_plus.7").getAsInt();
			int minutes = f7TimeMilliseconds / 1000 / 60;
			int seconds = f7TimeMilliseconds / 1000 % 60;
			return minutes + ":" + (seconds >= 10 ? seconds : "0" + seconds);
		} catch (Exception e) {
			return "None";
		}
	}

	public int getDungeonSecrets() {
		if (hypixelJson == null) {
			this.hypixelJson = playerFromUuid(playerUuid).response.getAsJsonObject();
		}

		try {
			return Utils.higherDepth(hypixelJson, "achievements.skyblock_treasure_hunter").getAsInt();
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDungeonClassLevel(JsonElement profile, String className) {
		return skillInfoFromExp(getDungeonClassXp(profile, className), "catacombs").getProgressLevel();
	}

	public SkillsStruct getDungeonClass(String className) {
		return skillInfoFromExp(getDungeonClassXp(className), "catacombs");
	}

	public double getCatacombsLevel() {
		return getCatacombsLevel(profileJson());
	}

	public double getCatacombsLevel(JsonElement profile) {
		return getCatacombsSkill(profile).getProgressLevel();
	}

	public SkillsStruct getCatacombsSkill() {
		return getCatacombsSkill(profileJson());
	}

	public SkillsStruct getCatacombsSkill(JsonElement profile) {
		double skillExp = Utils.higherDepth(profile, "dungeons.dungeon_types.catacombs.experience") != null
			? Utils.higherDepth(profile, "dungeons.dungeon_types.catacombs.experience").getAsDouble()
			: 0;
		return skillInfoFromExp(skillExp, "catacombs");
	}

	public double getDungeonClassXp(String className) {
		return getDungeonClassXp(profileJson(), className);
	}

	public double getDungeonClassXp(JsonElement profile, String className) {
		try {
			return Utils.higherDepth(profile, "dungeons.player_classes." + className + ".experience").getAsDouble();
		} catch (Exception e) {
			return 0;
		}
	}

	/* Inventory */
	public Map<Integer, InvItem> getInventoryMap() {
		try {
			String contents = Utils.higherDepth(profileJson(), "inv_contents.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return Utils.getGenericInventoryMap(parsedContents);
		} catch (Exception ignored) {}
		return null;
	}

	public Map<Integer, InvItem> getStorageMap() {
		try {
			JsonElement backpackContents = Utils.higherDepth(profileJson(), "backpack_contents");
			List<String> backpackCount = Utils.getJsonKeys(backpackContents);
			Map<Integer, InvItem> storageMap = new HashMap<>();
			int counter = 1;
			for (String bp : backpackCount) {
				Collection<InvItem> curBpMap = Utils
					.getGenericInventoryMap(NBTReader.readBase64(Utils.higherDepth(backpackContents, bp + ".data").getAsString()))
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
			String contents = Utils.higherDepth(profileJson(), "ender_chest_contents.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return Utils.getGenericInventoryMap(parsedContents);
		} catch (Exception ignored) {}
		return null;
	}

	public IChatComponent getLink() {
		return new ChatText(Utils.labelWithDesc("Player", C.UNDERLINE + playerUsername))
			.setClickEvent(ClickEvent.Action.OPEN_URL, skyblockStatsLink())
			.build();
	}

	public int getFairySouls() {
		try {
			return Utils.higherDepth(profileJson(), "fairy_souls_collected").getAsInt();
		} catch (Exception e) {
			return 0;
		}
	}

	public List<NBTCompound> getInventoryArmorNBT() {
		try {
			String contents = Utils.higherDepth(profileJson(), "inv_armor.data").getAsString();
			NBTCompound parsedContents = NBTReader.readBase64(contents);
			return Lists.reverse(parsedContents.getList("i").stream().map(item -> ((NBTCompound) item)).collect(Collectors.toList()));
		} catch (Exception ignored) {}

		return null;
	}

	/* Miscellaneous */
	public double getWeight(JsonElement profile) {
		return new Weight(profile, this).getTotalWeight(true).getRaw();
	}

	public double getWeight() {
		return getWeight(profileJson());
	}

	public String getFormattedUsername() {
		if (hypixelJson == null) {
			this.hypixelJson = playerFromUuid(playerUuid).response.getAsJsonObject();
		}

		return Utils.getFormattedUsername(hypixelJson);
	}

	@Override
	public String toString() {
		return (
			"Player{" +
			"validPlayer=" +
			validPlayer +
			", playerUuid='" +
			playerUuid +
			'\'' +
			", playerUsername='" +
			playerUsername +
			'\'' +
			", profileName='" +
			profileName +
			'\'' +
			'}'
		);
	}

	/* Helper methods */
	private boolean usernameToUuid(String username) {
		UsernameUuidStruct response = ApiHandler.usernameUuid(username);
		if (response.isNotValid()) {
			failCause = response.failCause;
			return true;
		}

		this.playerUsername = response.playerUsername;
		this.playerUuid = response.playerUuid;
		return false;
	}

	private boolean profileIdFromName(String profileName, JsonArray profilesArray) {
		try {
			for (int i = 0; i < profilesArray.size(); i++) {
				String currentProfileName = Utils.higherDepth(profilesArray.get(i), "cute_name").getAsString();
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
						Instant.ofEpochMilli(
							Utils.higherDepth(profilesArray.get(i), "members." + this.playerUuid + ".last_save").getAsLong()
						);
				} catch (Exception e) {
					continue;
				}

				if (lastSaveLoop.isAfter(lastProfileSave)) {
					this.profileIndex = i;
					lastProfileSave = lastSaveLoop;
					this.profileName = Utils.higherDepth(profilesArray.get(i), "cute_name").getAsString();
				}
			}
			return false;
		} catch (Exception ignored) {}
		return true;
	}
}
