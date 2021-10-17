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

package com.kr45732.hypixeladdons.utils;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kr45732.hypixeladdons.HypixelAddons;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class Constants {

	/* Constants JSON */
	public static double CATACOMBS_LEVEL_50_XP;
	public static double SKILLS_LEVEL_50_XP;
	public static double SKILLS_LEVEL_60_XP;
	public static List<String> COSMETIC_SKILL_NAMES;
	public static List<String> DUNGEON_CLASS_NAMES;
	public static List<String> SLAYER_NAMES;
	public static List<Integer> GUILD_EXP_TO_LEVEL;
	public static Map<String, Double[]> SLAYER_WEIGHTS;
	public static Map<String, Double[]> SKILL_WEIGHTS;
	public static Map<String, Double> DUNGEON_CLASS_WEIGHTS;
	public static Map<String, Double> DUNGEON_WEIGHTS;
	public static List<Integer> HOTM_EXP_TO_LEVEL;
	public static JsonElement SLAYER_DEPRECATION_SCALING;
	public static JsonElement SKILL_RATIO_WEIGHT;
	public static JsonElement SKILL_FACTORS;
	public static JsonElement SKILL_OVERFLOW_MULTIPLIERS;
	public static JsonObject DUNGEON_COMPLETION_WORTH;
	public static JsonObject DUNGEON_COMPLETION_BUFFS;
	public static Map<String, String> SLAYER_NAMES_MAP;
	public static List<Integer> SKYWARS_LEVELING_XP;
	public static List<String> SKYWARS_PRESTIGE_LIST;
	public static List<String> DUNGEON_META_ITEMS;
	public static double BEDWARS_EXP_PER_PRESTIGE;
	public static double BEDWARS_LEVELS_PER_PRESTIGE;
	public static Map<Integer, String> TOTAL_WINS_TO_DIVISION_MAP;

	/* Fetched from other sources */
	public static List<String> ALL_SKILL_NAMES;
	public static List<String> SKILL_NAMES;
	public static List<String> ESSENCE_ITEM_NAMES;
	public static List<String> BITS_ITEM_NAMES;

	public static void initialize() {
		try {
			JsonObject constantsJson = getJson("https://raw.githubusercontent.com/kr45732/skyblock-plus-data/main/Constants.json")
				.getAsJsonObject();

			Type mapStringString = new TypeToken<Map<String, String>>() {}.getType();
			Type listInteger = new TypeToken<List<Integer>>() {}.getType();
			Type listString = new TypeToken<List<String>>() {}.getType();
			Type mapStringDoubleArray = new TypeToken<Map<String, Double[]>>() {}.getType();
			Type mapStringDouble = new TypeToken<Map<String, Double>>() {}.getType();
			Type mapIntegerString = new TypeToken<Map<Integer, String>>() {}.getType();

			/* CATACOMBS_LEVEL_50_XP */
			CATACOMBS_LEVEL_50_XP = higherDepth(constantsJson, "CATACOMBS_LEVEL_50_XP").getAsDouble();

			/* SKILLS_LEVEL_50_XP */
			SKILLS_LEVEL_50_XP = higherDepth(constantsJson, "SKILLS_LEVEL_50_XP").getAsDouble();

			/* SKILLS_LEVEL_60_XP */
			SKILLS_LEVEL_60_XP = higherDepth(constantsJson, "SKILLS_LEVEL_60_XP").getAsDouble();

			/* DUNGEON_CLASS_NAMES */
			DUNGEON_CLASS_NAMES = gson.fromJson(higherDepth(constantsJson, "DUNGEON_CLASS_NAMES"), listString);

			/* SLAYER_NAMES */
			SLAYER_NAMES = gson.fromJson(higherDepth(constantsJson, "SLAYER_NAMES"), listString);

			/* GUILD_EXP_TO_LEVEL */
			GUILD_EXP_TO_LEVEL = gson.fromJson(higherDepth(constantsJson, "GUILD_EXP_TO_LEVEL"), listInteger);

			/* COSMETIC_SKILL_NAMES */
			COSMETIC_SKILL_NAMES = gson.fromJson(higherDepth(constantsJson, "COSMETIC_SKILL_NAMES"), listString);

			/* SLAYER_WEIGHTS */
			SLAYER_WEIGHTS = gson.fromJson(higherDepth(constantsJson, "SLAYER_WEIGHTS"), mapStringDoubleArray);

			/* SKILL_WEIGHTS */
			SKILL_WEIGHTS = gson.fromJson(higherDepth(constantsJson, "SKILL_WEIGHTS"), mapStringDoubleArray);

			/* DUNGEON_CLASS_WEIGHTS */
			DUNGEON_CLASS_WEIGHTS = gson.fromJson(higherDepth(constantsJson, "DUNGEON_CLASS_WEIGHTS"), mapStringDouble);

			/* DUNGEON_WEIGHTS */
			DUNGEON_WEIGHTS = gson.fromJson(higherDepth(constantsJson, "DUNGEON_WEIGHTS"), mapStringDouble);

			/* HOTM_EXP_TO_LEVEL */
			HOTM_EXP_TO_LEVEL = gson.fromJson(higherDepth(constantsJson, "HOTM_EXP_TO_LEVEL"), listInteger);

			/* SLAYER_DEPRECATION_SCALING */
			SLAYER_DEPRECATION_SCALING = higherDepth(constantsJson, "SLAYER_DEPRECATION_SCALING");

			/* SKILL_RATIO_WEIGHT */
			SKILL_RATIO_WEIGHT = higherDepth(constantsJson, "SKILL_RATIO_WEIGHT");

			/* SKILL_FACTORS */
			SKILL_FACTORS = higherDepth(constantsJson, "SKILL_FACTORS");

			/* SKILL_OVERFLOW_MULTIPLIERS */
			SKILL_OVERFLOW_MULTIPLIERS = higherDepth(constantsJson, "SKILL_OVERFLOW_MULTIPLIERS");

			/* DUNGEON_COMPLETION_WORTH */
			DUNGEON_COMPLETION_WORTH = higherDepth(constantsJson, "DUNGEON_COMPLETION_WORTH").getAsJsonObject();

			/* DUNGEON_COMPLETION_BUFFS */
			DUNGEON_COMPLETION_BUFFS = higherDepth(constantsJson, "DUNGEON_COMPLETION_BUFFS").getAsJsonObject();

			/* SLAYER_NAMES_MAP */
			SLAYER_NAMES_MAP = gson.fromJson(higherDepth(constantsJson, "SLAYER_NAMES_MAP"), mapStringString);

			/* SKYWARS_LEVELING_XP */
			SKYWARS_LEVELING_XP = gson.fromJson(higherDepth(constantsJson, "SKYWARS_LEVELING_XP"), listInteger);

			/* SKYWARS_PRESTIGE_LIST */
			SKYWARS_PRESTIGE_LIST = gson.fromJson(higherDepth(constantsJson, "SKYWARS_PRESTIGE_LIST"), listString);

			/* DUNGEON_META_ITEMS */
			DUNGEON_META_ITEMS = gson.fromJson(higherDepth(constantsJson, "DUNGEON_META_ITEMS"), listString);

			/* BEDWARS_EXP_PER_PRESTIGE */
			BEDWARS_EXP_PER_PRESTIGE = higherDepth(constantsJson, "BEDWARS_EXP_PER_PRESTIGE").getAsDouble();

			/* BEDWARS_LEVELS_PER_PRESTIGE */
			BEDWARS_LEVELS_PER_PRESTIGE = higherDepth(constantsJson, "BEDWARS_LEVELS_PER_PRESTIGE").getAsDouble();

			/* TOTAL_WINS_TO_DIVISION_MAP */
			TOTAL_WINS_TO_DIVISION_MAP = gson.fromJson(higherDepth(constantsJson, "TOTAL_WINS_TO_DIVISION_MAP"), mapIntegerString);

			/* ALL_SKILL_NAMES */
			ALL_SKILL_NAMES =
				higherDepth(getLevelingJson(), "leveling_caps")
					.getAsJsonObject()
					.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toCollection(ArrayList::new));
			ALL_SKILL_NAMES.remove("catacombs");

			/* SKILL_NAMES */
			SKILL_NAMES = new ArrayList<>(ALL_SKILL_NAMES);
			SKILL_NAMES.removeIf(COSMETIC_SKILL_NAMES::contains);

			/* ESSENCE_ITEM_NAMES */
			ESSENCE_ITEM_NAMES =
				getEssenceCostsJson().getAsJsonObject().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());

			/* BITS_ITEM_NAMES */
			BITS_ITEM_NAMES = getBitPricesJson().getAsJsonObject().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("An error occurred when initializing constants", e);
		}
	}
}
