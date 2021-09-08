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

package com.kr45732.hypixeladdons.utils.api;

import static com.kr45732.hypixeladdons.utils.Constants.*;
import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.Hypixel.playerFromUuid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
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

	/* Bedwars */
	public int getBedwarsStatistic(String statisticType) {
		return getBedwarsStatistic(statisticType, "");
	}

	public int getBedwarsStatistic(String statisticType, String mode) {
		JsonElement bedwarsStats = higherDepth(playerJson, "stats.Bedwars");
		mode = mode.length() > 0 ? mode + "_" : "";

		switch (statisticType) {
			case "kills":
				return higherDepth(bedwarsStats, mode + "kills_bedwars", 0);
			case "deaths":
				return higherDepth(bedwarsStats, mode + "deaths_bedwars", 0);
			case "wins":
				return higherDepth(bedwarsStats, mode + "wins_bedwars", 0);
			case "losses":
				return higherDepth(bedwarsStats, mode + "losses_bedwars", 0);
			case "final_kills":
				return higherDepth(bedwarsStats, mode + "final_kills_bedwars", 0);
			case "final_deaths":
				return higherDepth(bedwarsStats, mode + "final_deaths_bedwars", 0);
			case "experience":
				return higherDepth(bedwarsStats, "Experience", 0);
			default:
				return 0;
		}
	}

	public double getBedwarsLevel() {
		double exp = getBedwarsStatistic("experience");

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

	/* Skywars */
	public int getSkywarsStatistic(String statisticType) {
		return getSkywarsStatistic(statisticType, "");
	}

	public int getSkywarsStatistic(String statisticType, String mode) {
		JsonElement skywarsStats = higherDepth(playerJson, "stats.SkyWars");
		mode = mode.length() > 0 ? "_" + mode : "";
		switch (statisticType) {
			case "kills":
				return higherDepth(skywarsStats, "kills" + mode, 0);
			case "deaths":
				return higherDepth(skywarsStats, "deaths" + mode, 0);
			case "wins":
				return higherDepth(skywarsStats, "wins" + mode, 0);
			case "losses":
				return higherDepth(skywarsStats, "losses" + mode, 0);
			case "heads":
				return higherDepth(skywarsStats, "heads", 0);
			default:
				return 0;
		}
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

	/* Arcade */
	public String getArcadeStatStr(String type) {
		return higherDepth(playerJson, "stats.Arcade." + type) != null
			? higherDepth(playerJson, "stats.Arcade." + type).getAsString()
			: "none";
	}

	public int getArcadeStatInt(String type) {
		return higherDepth(playerJson, "stats.Arcade." + type, 0);
	}

	public String getArcadeStatIntFormatted(String type) {
		return formatNumber(higherDepth(playerJson, "stats.Arcade." + type, 0));
	}

	/* Arena */
	public int getArenaStat(String type) {
		return higherDepth(playerJson, "stats.Arena." + type, 0);
	}

	/* Duels */
	public String getOverallDivision() {
		int totalWins = getDuelsStat("wins");

		String prevVal = "Rookie";
		for (Map.Entry<Integer, String> winToDivision : TOTAL_WINS_TO_DIVISION_MAP.entrySet()) {
			if (winToDivision.getKey() > totalWins) {
				return prevVal;
			}

			prevVal = winToDivision.getValue();
		}
		return prevVal;
	}

	public int getDuelsStat(String type) {
		return getDuelsStat(type, "");
	}

	public int getDuelsStat(String type, String mode) {
		mode = mode.length() > 0 ? mode + "_" : mode;

		return higherDepth(playerJson, "stats.Duels." + mode + type, 0);
	}

	/* Helper methods */
	private boolean usernameToUuid(String username) {
		UsernameUuidStruct response = Hypixel.usernameUuid(username);
		if (response.isNotValid()) {
			failCause = response.failCause;
			return true;
		}

		this.playerUsername = response.playerUsername;
		this.playerUuid = response.playerUuid;
		return false;
	}

	public String getDiscordTag() {
		return higherDepth(playerJson, "socialMedia.links.DISCORD") != null
			? higherDepth(playerJson, "socialMedia.links.DISCORD").getAsString()
			: null;
	}
}
