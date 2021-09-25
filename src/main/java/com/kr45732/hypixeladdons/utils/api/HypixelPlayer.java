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

import static com.kr45732.hypixeladdons.utils.Constants.*;
import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.playerFromUuid;

import com.google.gson.JsonObject;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class HypixelPlayer {

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
		.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
		.withLocale(Locale.getDefault())
		.withZone(ZoneId.systemDefault());
	private static final DateTimeFormatter dateTimeFormatterShort = DateTimeFormatter
		.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
		.withLocale(Locale.getDefault())
		.withZone(ZoneId.systemDefault());
	private JsonObject playerJson;
	private String playerUuid;
	private String playerUsername;
	private boolean validPlayer = false;
	private String failCause = "Unknown fail cause";

	/* Constructor */
	public HypixelPlayer(String username) {
		if (usernameToUuid(username)) {
			return;
		}

		try {
			HypixelResponse response = playerFromUuid(playerUuid);
			if (response.isNotValid()) {
				failCause = response.failCause;
				return;
			}

			this.playerJson = response.response.getAsJsonObject();
		} catch (Exception e) {
			return;
		}

		this.validPlayer = true;
	}

	/* Getters */
	public boolean isValid() {
		return validPlayer;
	}

	public String getFailCause() {
		return failCause;
	}

	/* Hypixel */
	public double getHypixelLevel() {
		return (Math.sqrt((2 * higherDepth(playerJson, "networkExp").getAsLong()) + 30625) / 50) - 2.5;
	}

	public String getFormattedUsername() {
		return Utils.getFormattedUsername(playerJson);
	}

	public String getStrippedFormattedUsername() {
		return StringUtils.stripControlCodes(getFormattedUsername());
	}

	public IChatComponent getLink() {
		return new ChatText(labelWithDesc("Player", C.UNDERLINE + getFormattedUsername()))
			.setClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/" + playerUsername)
			.build();
	}

	public boolean isOnline() {
		return higherDepth(playerJson, "lastLogin", 0) > higherDepth(playerJson, "lastLogout", 0);
	}

	public JsonObject getSocialMediaLinks() {
		return higherDepth(playerJson, "socialMedia.links") != null
			? higherDepth(playerJson, "socialMedia.links").getAsJsonObject()
			: new JsonObject();
	}

	public int getAchievementPoints() {
		return higherDepth(playerJson, "achievementPoints", 0);
	}

	public int getKarma() {
		return higherDepth(playerJson, "karma", 0);
	}

	public String getLastUpdatedFormatted() {
		if (higherDepth(playerJson, "lastLogout") == null) {
			return "unknown";
		}
		return dateTimeFormatter.format(Instant.ofEpochMilli(higherDepth(playerJson, "lastLogout").getAsLong()));
	}

	public String getLastUpdatedFormattedShort() {
		if (higherDepth(playerJson, "lastLogout") == null) {
			return "unknown";
		}
		return dateTimeFormatterShort.format(Instant.ofEpochMilli(higherDepth(playerJson, "lastLogout").getAsLong()));
	}

	public String getFirstLoginFormatted() {
		if (higherDepth(playerJson, "firstLogin") == null) {
			return "unknown";
		}
		return dateTimeFormatter.format(Instant.ofEpochMilli(higherDepth(playerJson, "firstLogin").getAsLong()));
	}

	public String getPropertyFormatted(String path) {
		return formatNumber(higherDepth(playerJson, path, 0));
	}

	public String getDiscordTag() {
		return higherDepth(playerJson, "socialMedia.links.DISCORD") != null
			? higherDepth(playerJson, "socialMedia.links.DISCORD").getAsString()
			: null;
	}

	/* Bedwars */
	public int getBedwarsKills(BedwarsMode mode) {
		return getBedwarsInt("kills_bedwars", mode);
	}

	public int getBedwarsDeaths(BedwarsMode mode) {
		return getBedwarsInt("deaths_bedwars", mode);
	}

	public int getBedwarsWins(BedwarsMode mode) {
		return getBedwarsInt("wins_bedwars", mode);
	}

	public int getBedwarsLosses(BedwarsMode mode) {
		return getBedwarsInt("losses_bedwars", mode);
	}

	public int getBedwarsFinalKills(BedwarsMode mode) {
		return getBedwarsInt("final_kills_bedwars", mode);
	}

	public int getBedwarsFinalDeaths(BedwarsMode mode) {
		return getBedwarsInt("final_deaths_bedwars", mode);
	}

	public int getBedwarsExperience() {
		return getBedwarsInt("Experience", BedwarsMode.NONE);
	}

	public int getBedwarsInt(String type, BedwarsMode mode) {
		return higherDepth(higherDepth(playerJson, "stats.Bedwars"), mode.getId() + type, 0);
	}

	public double getBedwarsLevel() {
		double exp = getBedwarsExperience();

		int prestige = (int) (exp / BEDWARS_EXP_PER_PRESTIGE);
		exp = exp % BEDWARS_EXP_PER_PRESTIGE;
		if (prestige > 5) {
			int over = prestige % 5;
			exp += over * BEDWARS_EXP_PER_PRESTIGE;
			prestige -= over;
		}

		if (exp < 500) {
			return (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
		} else if (exp < 1500) {
			return 1 + (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
		} else if (exp < 3500) {
			return 2 + (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
		} else if (exp < 5500) {
			return 3 + (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
		} else if (exp < 9000) {
			return 4 + (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
		}
		exp -= 9000;
		return (exp / 5000 + 4) + (prestige * BEDWARS_LEVELS_PER_PRESTIGE);
	}

	public enum BedwarsMode {
		NONE("", ""),
		SOLO("eight_one", "Solo"),
		DOUBLES("eight_two", "Doubles"),
		THREES("four_three", "3v3v3v3"),
		FOURS("four_four", "4v4v4v4"),
		TWO_FOUR("two_four", "4v4");

		private final String name;
		private final String id;

		BedwarsMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? id + "_" : id;
		}

		public static List<BedwarsMode> getModes() {
			List<BedwarsMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE);
			return values;
		}
	}

	/* Skywars */
	public int getSkywarsKills(SkywarsMode mode) {
		return getSkywarsInt("kills", mode);
	}

	public int getSkywarsDeaths(SkywarsMode mode) {
		return getSkywarsInt("deaths", mode);
	}

	public int getSkywarsWins(SkywarsMode mode) {
		return getSkywarsInt("wins", mode);
	}

	public int getSkywarsLosses(SkywarsMode mode) {
		return getSkywarsInt("losses", mode);
	}

	public int getSkywarsInt(String type, SkywarsMode mode) {
		return higherDepth(playerJson, "stats.SkyWars." + type + mode.getId(), 0);
	}

	public int getSkywarsLevel() {
		double xp = higherDepth(playerJson, "stats.SkyWars.skywars_experience").getAsLong();

		if (xp >= 15000) {
			return (int) Math.floor((xp - 15000) / 10000 + 12);
		}

		for (int i = 0; i < SKYWARS_LEVELING_XP.size(); i++) {
			if (SKYWARS_LEVELING_XP.get(i) * 10 - xp > 0) {
				return i;
			}
		}

		return 0;
	}

	public String getSkywarsLevelFormatted() {
		return higherDepth(playerJson, "stats.SkyWars.levelFormatted").getAsString();
	}

	public String getSkywarsPrestige() {
		int level = getSkywarsLevel();

		if (level >= 60) {
			return "Mythic";
		}

		int index = (int) Math.floor(level / 5.0);

		return SKYWARS_PRESTIGE_LIST.size() > index ? SKYWARS_PRESTIGE_LIST.get(index) : "Iron";
	}

	public enum SkywarsMode {
		NONE("", ""),
		RANKED("ranked", "Ranked"),
		SOLO_NORMAL("solo_normal", "Solo Normal"),
		SOLO_INSANE("solo_insane", "Solo Insane"),
		TEAM_NORMAL("team_normal", "Team Normal"),
		TEAM_INSANE("team_insane", "Team Insane"),
		MEGA("mega", "Mega"),
		MEGA_DOUBLES("mega_doubles", "Mega Doubles");

		private final String name;
		private final String id;

		SkywarsMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? "_" + id : id;
		}

		public static List<SkywarsMode> getModes() {
			List<SkywarsMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE);
			return values;
		}
	}

	/* Arcade */
	public int getArcadeCoins() {
		return getArcadeInt("coins", ArcadeMode.NONE);
	}

	public int getArcadeWins(ArcadeMode mode) {
		return getArcadeInt("wins", mode);
	}

	public int getArcadeKills(ArcadeMode mode) {
		return getArcadeInt("kills", mode);
	}

	public int getArcadeDeaths(ArcadeMode mode) {
		return getArcadeInt("deaths", mode);
	}

	public String getArcadeStr(String type) {
		return higherDepth(playerJson, "stats.Arcade." + type, "none");
	}

	public int getArcadeInt(String type, ArcadeMode mode) {
		return higherDepth(
			playerJson,
			"stats.Arcade." +
			(mode != ArcadeMode.NONE ? (mode == ArcadeMode.GALAXY_WARS ? mode.getId() + "_" + type : type + "_" + mode.getId()) : type),
			0
		);
	}

	public enum ArcadeMode {
		NONE("", ""),
		BLOCKING_DEAD("dayone", "Blocking Dead"),
		BOUNTY_HUNTERS("oneinthequiver", "Bounty Hunters"),
		DRAGON_WARS("dragonwars2", "Dragon Wars"),
		EASTER_SIMULATOR("easter_simulator", "Easter Simulator"),
		ENDER_SPLEEF("ender", "Ender Spleef"),
		FARM_HUNT("farm_hunt", "Farm Hunt"),
		FOOTBALL("soccer", "Football"),
		GALAXY_WARS("sw", "Galaxy Wars"),
		GRINCH_SIMULATOR_V2("grinch_simulator_v2", "Grinch Simulator v2"),
		HOLE_IN_THE_WALL("hole_in_the_wall", "Hole in the Wall"),
		SIMON_SAYS("simon_says", "Hypixel Says"),
		PARTY_GAMES("party", "Party Games"),
		PARTY_GAMES_2("party_2", "Party Games"),
		PARTY_GAMES_3("party_3", "Party Games"),
		SCUBA_SIMULATOR("scuba_simulator", "Scuba Simulator"),
		THROW_OUT("throw_out", "Throw Out"),
		MINI_WALLS("mini_walls", "Mini Walls"),
		ZOMBIES("zombies", "Zombies");

		private final String name;
		private final String id;

		ArcadeMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? id + "_" : id;
		}
	}

	/* Arena */
	public int getArenaCoins() {
		return getArenaInt("coins", ArenaMode.NONE);
	}

	public int getArenaWins(ArenaMode mode) {
		return getArenaInt("wins", mode);
	}

	public int getArenaKills(ArenaMode mode) {
		return getArenaInt("kills", mode);
	}

	public int getArenaDeaths(ArenaMode mode) {
		return getArenaInt("deaths", mode);
	}

	public int getArenaLosses(ArenaMode mode) {
		return getArenaInt("losses", mode);
	}

	public int getArenaWinstreak(ArenaMode mode) {
		return getArenaInt("win_streaks", mode);
	}

	public int getArenaInt(String type, ArenaMode mode) {
		if (mode == ArenaMode.ALL) {
			return ArenaMode.getModes().stream().mapToInt(arenaMode -> getArenaInt(type, arenaMode)).sum();
		}

		return higherDepth(playerJson, "stats.Arena." + type + mode.getId(), 0);
	}

	public enum ArenaMode {
		NONE("", ""),
		DUOS("2v2", "2v2"),
		FOURS("4v4", "4v4"),
		ALL("", "");

		private final String name;
		private final String id;

		ArenaMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? "_" + id : id;
		}

		public static List<ArenaMode> getModes() {
			List<ArenaMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE || value == ALL);
			return values;
		}
	}

	/* Duels */
	public String getDuelsOverallDivision() {
		int totalWins = getDuelsInt("wins", DuelsMode.NONE);

		String prevVal = "Rookie";
		for (Map.Entry<Integer, String> winToDivision : TOTAL_WINS_TO_DIVISION_MAP.entrySet()) {
			if (winToDivision.getKey() > totalWins) {
				return prevVal;
			}

			prevVal = winToDivision.getValue();
		}
		return prevVal;
	}

	public int getDuelsCoins() {
		return getDuelsInt("coins", DuelsMode.NONE);
	}

	public int getDuelsWins(DuelsMode mode) {
		return getDuelsInt("wins", mode);
	}

	public int getDuelsKills(DuelsMode mode) {
		return getDuelsInt("kills", mode);
	}

	public int getDuelsDeaths(DuelsMode mode) {
		return getDuelsInt("deaths", mode);
	}

	public int getDuelsLosses(DuelsMode mode) {
		return getDuelsInt("losses", mode);
	}

	public int getDuelsWinstreak(DuelsMode mode) {
		return getDuelsInt("current_winstreak", mode);
	}

	public double getDuelsMeleeAccuracy(DuelsMode mode) {
		return divide(getDuelsInt("melee_hits", mode), getDuelsInt("melee_swings", mode));
	}

	public double getDuelsArrowAccuracy(DuelsMode mode) {
		return divide(getDuelsInt("bow_hits", mode), getDuelsInt("bow_swings", mode));
	}

	public int getDuelsInt(String type, DuelsMode mode) {
		return higherDepth(playerJson, "stats.Duels." + mode.getId() + type, 0);
	}

	public enum DuelsMode {
		NONE("", ""),
		UHC("uhc_duel", "UHC 1v1"),
		UHC_DOUBLES("uhc_doubles", "UHC 2v2"),
		UHC_FOURS("uhc_four", "UHC 4v4"),
		OP("op_duel", "OP 1v1"),
		OP_DOUBLES("op_doubles", "OP 2v2"),
		SKYWARS("sw_duel", "SkyWars 1v1"),
		SKYWARS_DOUBLES("sw_doubles", "SkyWars 2v2"),
		BOW_SPLEFF("bowspleef_duel", "Bow Spleef 1v1"),
		BOW("bow_duel", "Bow 1v1"),
		BLITZ("blitz_duel", "Blitz 1v1"),
		MEGA_WALLS("mw_duel", "Mega Walls 1v1"),
		MEGA_WALLS_DOUBLES("mw_doubles", "Mega Walls 2v2"),
		SUMO("sumo_duel", "Sumo 1v1"),
		CLASSIC("classical_duel", "Classic 1v1"),
		COMBO("combo_duel", "Combo 1v1"),
		BRIDGE_SOLO("bridge_duel", "Bridge 1v1"),
		BRIDGE_DOUBLES("bridge_doubles", "Bridge 2v2"),
		BRIDGE_2V2V2V2("bridge_2v2v2v2", "Bridge 2v2v2v2"),
		BRIDGE_THREES("bridge_3v3v3v3", "Bridge 3v3v3v3"),
		BRIDGE_FOURS("bridge_four", "Bridge 4v4");

		private final String name;
		private final String id;

		DuelsMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? id + "_" : id;
		}

		public static List<DuelsMode> getModes() {
			List<DuelsMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE);
			return values;
		}
	}

	/* Murder mystery */
	public int getMurderMysteryCoins() {
		return getMurderMysteryInt("coins", MurderMysteryMode.NONE);
	}

	public int getMurderMysteryKills(MurderMysteryMode mode) {
		return getMurderMysteryInt("kills", mode);
	}

	public int getMurderMysteryDeaths(MurderMysteryMode mode) {
		return getMurderMysteryInt("deaths", mode);
	}

	public int getMurderMysteryWins(MurderMysteryMode mode) {
		return getMurderMysteryInt("wins", mode);
	}

	public int getMurderMysteryLosses(MurderMysteryMode mode) {
		return getMurderMysteryInt("losses", mode);
	}

	public double getMurderMysteryKDR(MurderMysteryMode mode) {
		return divide(getMurderMysteryKills(mode), getMurderMysteryDeaths(mode));
	}

	public double getMurderMysteryWLR(MurderMysteryMode mode) {
		return divide(getMurderMysteryWins(mode), getMurderMysteryLosses(mode));
	}

	public int getMurderMysteryInt(String type, MurderMysteryMode mode) {
		return higherDepth(playerJson, "stats.MurderMystery." + type + "_" + mode.getId(), 0);
	}

	public enum MurderMysteryMode {
		NONE("", ""),
		CLASSIC("MURDER_CLASSIC", "Classic"),
		INFECTION("MURDER_INFECTION", "Infection"),
		ASSASSINS("MURDER_ASSASSINS", "Assassins"),
		DOUBLE_UP("MURDER_DOUBLE_UP", "Double Up");

		private final String name;
		private final String id;

		MurderMysteryMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public static List<MurderMysteryMode> getModes() {
			List<MurderMysteryMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE);
			return values;
		}
	}

	/* Build Battle */
	public int getBuildBattleCoins() {
		return getBuildBattleInt("coins", BuildBattleMode.NONE);
	}

	public int getBuildBattleWins(BuildBattleMode mode) {
		return getBuildBattleInt("wins", mode);
	}

	public int getBuildBattleInt(String type, BuildBattleMode mode) {
		return higherDepth(playerJson, "stats.BuildBattle." + type + mode.getId(), 0);
	}

	public enum BuildBattleMode {
		NONE("", ""),
		SOLO("solo_normal", "Solo"),
		PRO("solo_pro", "Pro"),
		TEAMS("teams_normal", "Teams"),
		GUESS_THE_BUILD("guess_the_build", "Guess The Build");

		private final String name;
		private final String id;

		BuildBattleMode(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id.length() > 0 ? "_" + id : id;
		}

		public static List<BuildBattleMode> getModes() {
			List<BuildBattleMode> values = new ArrayList<>(Arrays.asList(values()));
			values.removeIf(value -> value == NONE);
			return values;
		}
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

	public IChatComponent defaultPlayerComponent() {
		return empty().appendSibling(getLink());
	}
}
