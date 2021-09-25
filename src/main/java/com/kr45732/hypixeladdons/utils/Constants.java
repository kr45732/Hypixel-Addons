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

package com.kr45732.hypixeladdons.utils;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.HypixelAddons;
import java.util.*;
import java.util.stream.Collectors;

public class Constants {

	public static final double CATACOMBS_LEVEL_50_XP = 569809640;
	public static final double SKILLS_LEVEL_50_XP = 55172425;
	public static final double SKILLS_LEVEL_60_XP = 111672425;
	public static final Map<String, String> RARITY_TO_NUMBER_MAP = new HashMap<>();
	public static final List<String> ENCHANT_NAMES = new ArrayList<>();
	public static final List<String> SKILL_NAMES = new ArrayList<>();
	public static final List<String> ALL_SKILL_NAMES = new ArrayList<>();
	public static final List<String> COSMETIC_SKILL_NAMES = Arrays.asList("runecrafting", "carpentry");
	public static final Map<String, String> SKILLS_EMOJI_MAP = new HashMap<>();
	public static final List<String> PET_NAMES = new ArrayList<>();
	public static final List<String> DUNGEON_CLASS_NAMES = Arrays.asList("healer", "mage", "berserk", "archer", "tank");
	public static final Map<String, String> SLAYER_NAMES_MAP = new HashMap<>();
	public static final List<String> ESSENCE_ITEM_NAMES = new ArrayList<>();
	public static final List<String> BITS_ITEM_NAMES = new ArrayList<>();
	public static final List<Integer> GUILD_EXP_TO_LEVEL = Arrays.asList(
		100000,
		150000,
		250000,
		500000,
		750000,
		1000000,
		1250000,
		1500000,
		2000000,
		2500000,
		2500000,
		2500000,
		2500000,
		2500000,
		3000000
	);
	public static final Map<String, Double[]> SLAYER_WEIGHTS = new HashMap<>();
	public static final Map<String, Double[]> SKILL_WEIGHTS = new HashMap<>();
	public static final Map<String, Double> DUNGEON_CLASS_WEIGHTS = new HashMap<>();
	public static final Map<String, Double> DUNGEON_WEIGHTS = new HashMap<>();
	public static final List<Integer> SKYWARS_LEVELING_XP = Arrays.asList(0, 2, 7, 15, 25, 50, 100, 200, 350, 600, 1000, 1500);
	public static final Map<Integer, String> TOTAL_WINS_TO_DIVISION_MAP = new HashMap<>();
	public static final int BEDWARS_EXP_PER_PRESTIGE = 489000;
	public static final int BEDWARS_LEVELS_PER_PRESTIGE = 100;
	public static final List<String> META_ITEMS = Arrays.asList(
		"HYPERION",
		"VALKYRIE",
		"SCYLLA",
		"AXE_OF_THE_SHREDDED",
		"JUJU_SHORTBOW",
		"TERMINATOR"
	);
	public static final List<String> SKYWARS_PRESTIGE_LIST = Arrays.asList(
		"Iron",
		"Iron",
		"Gold",
		"Diamond",
		"Emerald",
		"Sapphire",
		"Ruby",
		"Crystal",
		"Opal",
		"Amethyst",
		"Rainbow"
	);

	public static void initialize() {
		try {
			/* rarityToNumberMap */
			RARITY_TO_NUMBER_MAP.put("MYTHIC", ";5");
			RARITY_TO_NUMBER_MAP.put("LEGENDARY", ";4");
			RARITY_TO_NUMBER_MAP.put("EPIC", ";3");
			RARITY_TO_NUMBER_MAP.put("RARE", ";2");
			RARITY_TO_NUMBER_MAP.put("UNCOMMON", ";1");
			RARITY_TO_NUMBER_MAP.put("COMMON", ";0");

			/* enchantNames */
			for (Map.Entry<String, JsonElement> enchantName : Utils
				.getEnchantsJson()
				.getAsJsonObject()
				.getAsJsonObject("enchants_xp_cost")
				.entrySet()) {
				ENCHANT_NAMES.add(enchantName.getKey().toUpperCase());
			}
			if (!ENCHANT_NAMES.contains("ULTIMATE_JERRY")) {
				ENCHANT_NAMES.add("ULTIMATE_JERRY");
			}

			/* allSkillNames */
			ALL_SKILL_NAMES.addAll(
				Utils
					.higherDepth(Utils.getLevelingJson(), "leveling_caps")
					.getAsJsonObject()
					.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toCollection(ArrayList::new))
			);
			ALL_SKILL_NAMES.remove("catacombs");

			/* skillNames */
			SKILL_NAMES.addAll(ALL_SKILL_NAMES);
			SKILL_NAMES.removeIf(COSMETIC_SKILL_NAMES::contains);

			/* skillsEmojiMap */
			SKILLS_EMOJI_MAP.put("taming", "<:taming:800462115365716018>");
			SKILLS_EMOJI_MAP.put("farming", "<:farming:800462115055992832>");
			SKILLS_EMOJI_MAP.put("foraging", "<:foraging:800462114829500477>");
			SKILLS_EMOJI_MAP.put("combat", "<:combat:800462115009855548>");
			SKILLS_EMOJI_MAP.put("alchemy", "<:alchemy:800462114589376564>");
			SKILLS_EMOJI_MAP.put("fishing", "<:fishing:800462114853617705>");
			SKILLS_EMOJI_MAP.put("enchanting", "<:enchanting:800462115193225256>");
			SKILLS_EMOJI_MAP.put("mining", "<:mining:800462115009069076>");
			SKILLS_EMOJI_MAP.put("carpentry", "<:carpentry:800462115156131880>");
			SKILLS_EMOJI_MAP.put("runecrafting", "<:runecrafting:800462115172909086>");

			/* petNames */
			PET_NAMES.addAll(
				Utils
					.getPetNumsJson()
					.getAsJsonObject()
					.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toCollection(ArrayList::new))
			);

			/* slayerNamesMap */
			SLAYER_NAMES_MAP.put("sven", "wolf");
			SLAYER_NAMES_MAP.put("rev", "zombie");
			SLAYER_NAMES_MAP.put("tara", "spider");
			SLAYER_NAMES_MAP.put("enderman", "enderman");

			/* essenceItemNames */
			ESSENCE_ITEM_NAMES.addAll(
				Utils
					.getEssenceCostsJson()
					.getAsJsonObject()
					.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toCollection(ArrayList::new))
			);

			/* bitsItemNames */
			BITS_ITEM_NAMES.addAll(
				Utils
					.getBitPricesJson()
					.getAsJsonObject()
					.entrySet()
					.stream()
					.map(Map.Entry::getKey)
					.collect(Collectors.toCollection(ArrayList::new))
			);

			/* slayerWeights */
			SLAYER_WEIGHTS.put("rev", new Double[] { 2208D, 0.15D });
			SLAYER_WEIGHTS.put("tara", new Double[] { 2118D, 0.08D });
			SLAYER_WEIGHTS.put("sven", new Double[] { 1962D, 0.015D });
			SLAYER_WEIGHTS.put("enderman", new Double[] { 1430D, 0.017D });

			/* skillWeights */
			SKILL_WEIGHTS.put("mining", new Double[] { 1.18207448, 259634D });
			SKILL_WEIGHTS.put("foraging", new Double[] { 1.232826, 259634D });
			SKILL_WEIGHTS.put("enchanting", new Double[] { 0.96976583, 882758D });
			SKILL_WEIGHTS.put("farming", new Double[] { 1.217848139, 220689D });
			SKILL_WEIGHTS.put("combat", new Double[] { 1.15797687265, 275862D });
			SKILL_WEIGHTS.put("fishing", new Double[] { 1.406418, 88274D });
			SKILL_WEIGHTS.put("alchemy", new Double[] { 1.0, 1103448D });
			SKILL_WEIGHTS.put("taming", new Double[] { 1.14744, 441379D });

			/* dungeonClassWeights */
			DUNGEON_CLASS_WEIGHTS.put("healer", 0.0000045254834D);
			DUNGEON_CLASS_WEIGHTS.put("mage", 0.0000045254834D);
			DUNGEON_CLASS_WEIGHTS.put("berserk", 0.0000045254834D);
			DUNGEON_CLASS_WEIGHTS.put("archer", 0.0000045254834D);
			DUNGEON_CLASS_WEIGHTS.put("tank", 0.0000045254834D);

			/* dungeonWeights */
			DUNGEON_WEIGHTS.put("catacombs", 0.0002149604615D);

			/* totalWinsToDivisionMap */
			TOTAL_WINS_TO_DIVISION_MAP.put(100, "Rookie");
			TOTAL_WINS_TO_DIVISION_MAP.put(120, "Rookie II");
			TOTAL_WINS_TO_DIVISION_MAP.put(140, "Rookie III");
			TOTAL_WINS_TO_DIVISION_MAP.put(160, "Rookie IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(180, "Rookie V");
			TOTAL_WINS_TO_DIVISION_MAP.put(200, "Iron");
			TOTAL_WINS_TO_DIVISION_MAP.put(260, "Iron II");
			TOTAL_WINS_TO_DIVISION_MAP.put(320, "Iron III");
			TOTAL_WINS_TO_DIVISION_MAP.put(380, "Iron IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(440, "Iron V");
			TOTAL_WINS_TO_DIVISION_MAP.put(500, "Gold");
			TOTAL_WINS_TO_DIVISION_MAP.put(600, "Gold II");
			TOTAL_WINS_TO_DIVISION_MAP.put(700, "Gold III");
			TOTAL_WINS_TO_DIVISION_MAP.put(800, "Gold IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(900, "Gold V");
			TOTAL_WINS_TO_DIVISION_MAP.put(1000, "Diamond");
			TOTAL_WINS_TO_DIVISION_MAP.put(1200, "Diamond II");
			TOTAL_WINS_TO_DIVISION_MAP.put(1400, "Diamond III");
			TOTAL_WINS_TO_DIVISION_MAP.put(1600, "Diamond IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(1800, "Diamond V");
			TOTAL_WINS_TO_DIVISION_MAP.put(2000, "Master");
			TOTAL_WINS_TO_DIVISION_MAP.put(2400, "Master II");
			TOTAL_WINS_TO_DIVISION_MAP.put(2800, "Master III");
			TOTAL_WINS_TO_DIVISION_MAP.put(3200, "Master IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(3800, "Master V");
			TOTAL_WINS_TO_DIVISION_MAP.put(4000, "Legend");
			TOTAL_WINS_TO_DIVISION_MAP.put(5200, "Legend II");
			TOTAL_WINS_TO_DIVISION_MAP.put(6400, "Legend III");
			TOTAL_WINS_TO_DIVISION_MAP.put(7600, "Legend IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(8800, "Legend V");
			TOTAL_WINS_TO_DIVISION_MAP.put(10000, "Grandmaster");
			TOTAL_WINS_TO_DIVISION_MAP.put(12000, "Grandmaster II");
			TOTAL_WINS_TO_DIVISION_MAP.put(14000, "Grandmaster III");
			TOTAL_WINS_TO_DIVISION_MAP.put(16000, "Grandmaster IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(18000, "Grandmaster V");
			TOTAL_WINS_TO_DIVISION_MAP.put(20000, "Godlike");
			TOTAL_WINS_TO_DIVISION_MAP.put(24000, "Godlike II");
			TOTAL_WINS_TO_DIVISION_MAP.put(28000, "Godlike III");
			TOTAL_WINS_TO_DIVISION_MAP.put(32000, "Godlike IV");
			TOTAL_WINS_TO_DIVISION_MAP.put(36000, "Godlike V");
			TOTAL_WINS_TO_DIVISION_MAP.put(40000, "Godlike VI");
			TOTAL_WINS_TO_DIVISION_MAP.put(44000, "Godlike VII");
			TOTAL_WINS_TO_DIVISION_MAP.put(48000, "Godlike VIII");
			TOTAL_WINS_TO_DIVISION_MAP.put(52000, "Godlike IX ");
			TOTAL_WINS_TO_DIVISION_MAP.put(56000, "Godlike X");
		} catch (Exception e) {
			HypixelAddons.INSTANCE.logger.error("An error occurred when initializing constants", e);
		}
	}
}
