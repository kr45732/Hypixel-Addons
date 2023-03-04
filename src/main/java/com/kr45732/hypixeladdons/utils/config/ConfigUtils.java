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

package com.kr45732.hypixeladdons.utils.config;

import com.kr45732.hypixeladdons.HypixelAddons;

public class ConfigUtils {

	private static final Configuration config = new Configuration();

	/* Hypixel Guild Events */
	public static boolean toggleGuildRequestHelper;
	public static String guildRequestType;
	public static boolean toggleGuildLeaveMessage;
	public static String guildJoinMessage;
	public static boolean toggleGuildJoinMessage;
	public static String guildLeaveMessage;
	/* Hypixel Guild Chat */
	public static boolean toggleGuildChatResponder;
	public static boolean toggleGuildChatCooldownMessage;
	public static int guildChatCooldown;
	/* Mystery box */
	public static boolean toggleMysteryBoxSorter;
	public static String mysteryBoxSortType;
	/* Scoreboard */
	public static boolean enableCustomSidebar;
	public static boolean hideSidebarRedNumbers;
	public static double sidebarScale;
	public static int sidebarXOffset;
	public static int sidebarYOffset;
	public static int sidebarBackgroundColor;
	public static double sidebarAlpha;
	public static boolean sidebarChromaBackground;
	public static int sidebarChromaSpeed;
	/* MOTD */
	public static String motdText;
	/* Misc */
	public static String jacobKey;
	public static String jacobUrl;
	public static int jacobLastYear;

	public static void initialize() {
		try {
			/* API Key */
			config.initialize("api", "api_key", "");

			/* Hypixel Guild Chat */
			toggleGuildChatResponder = config.initialize("guild_chat", "toggle", false);
			guildChatCooldown = config.initialize("guild_chat", "cooldown", 3);
			toggleGuildChatCooldownMessage = config.initialize("guild_chat", "toggle_cooldown_message", false);

			/* Hypixel Guild Events */
			toggleGuildRequestHelper = config.initialize("guild_event", "toggle_request_helper", false);
			guildRequestType = config.initialize("guild_event", "request_type", "skyblock");
			toggleGuildJoinMessage = config.initialize("guild_event", "toggle_join_message", false);
			guildJoinMessage = config.initialize("guild_event", "join_message", "Welcome!");
			toggleGuildLeaveMessage = config.initialize("guild_event", "toggle_leave_message", false);
			guildLeaveMessage = config.initialize("guild_event", "leave_message", "Goodbye :(");

			/* Mystery box */
			toggleMysteryBoxSorter = config.initialize("mystery_box", "toggle", false);
			mysteryBoxSortType = config.initialize("mystery_box", "sort_type", "expiry");

			/* Sidebar */
			enableCustomSidebar = config.initialize("sidebar", "toggle", false);
			hideSidebarRedNumbers = config.initialize("sidebar", "toggle_red_numbers", false);
			sidebarScale = config.initialize("sidebar", "scale", 1);
			sidebarXOffset = config.initialize("sidebar", "x_offset", 0);
			sidebarYOffset = config.initialize("sidebar", "y_offset", 0);
			sidebarBackgroundColor = config.initialize("sidebar", "background_color", 0);
			sidebarAlpha = config.initialize("sidebar", "alpha", 0.275);
			sidebarChromaBackground = config.initialize("sidebar", "toggle_chroma_background", false);
			sidebarChromaSpeed = config.initialize("sidebar", "chroma_speed", 2);

			/* MOTD */
			motdText = config.initialize("motd", "text", "");

			/* Misc */
			jacobKey = config.initialize("misc", "jacob_key", "");
			jacobUrl = config.initialize("misc", "jacob_url", "");
			jacobLastYear = config.initialize("misc", "jacob_last_year", -1);
		} catch (Exception e) {
			HypixelAddons.INSTANCE.getLogger().error("An error occurred when initializing the configuration", e);
		}
	}

	/* Getters */
	public static String getHypixelKey() {
		String key = config.get("api", "api_key", "");
		return key.length() == 0 ? null : key;
	}

	public static String getSidebarBackgroundColorFormatted() {
		int red = (sidebarBackgroundColor >> 16) & 0x0ff;
		int green = (sidebarBackgroundColor >> 8) & 0x0ff;
		int blue = (sidebarBackgroundColor) & 0x0ff;
		return red + "," + green + "," + blue;
	}

	public static int getSidebarAlphaScaled() {
		return (int) (sidebarAlpha * 255);
	}

	/* Setters */
	public static void setHypixelKey(String hypixelKey) {
		config.write("api", "api_key", hypixelKey);
	}

	public static void setToggleGuildChatResponder(boolean toggleGuildChatResponder) {
		if (ConfigUtils.toggleGuildChatResponder != toggleGuildChatResponder) {
			ConfigUtils.toggleGuildChatResponder = toggleGuildChatResponder;
			config.write("guild_chat", "toggle", toggleGuildChatResponder);
		}
	}

	public static void setGuildChatCooldown(int guildChatCooldown) {
		if (ConfigUtils.guildChatCooldown != guildChatCooldown) {
			ConfigUtils.guildChatCooldown = guildChatCooldown;
			config.write("guild_chat", "cooldown", guildChatCooldown);
		}
	}

	public static void setToggleGuildChatCooldownMessage(boolean toggleGuildChatCooldownMessage) {
		if (ConfigUtils.toggleGuildChatCooldownMessage != toggleGuildChatCooldownMessage) {
			ConfigUtils.toggleGuildChatCooldownMessage = toggleGuildChatCooldownMessage;
			config.write("guild_chat", "toggle_cooldown_message", toggleGuildChatCooldownMessage);
		}
	}

	public static void setToggleGuildRequestHelper(boolean toggleGuildRequestHelper) {
		if (ConfigUtils.toggleGuildRequestHelper != toggleGuildRequestHelper) {
			ConfigUtils.toggleGuildRequestHelper = toggleGuildRequestHelper;
			config.write("guild_event", "toggle_request_helper", toggleGuildRequestHelper);
		}
	}

	public static void setGuildRequestType(String guildRequestType) {
		if (!ConfigUtils.guildRequestType.equals(guildRequestType)) {
			ConfigUtils.guildRequestType = guildRequestType;
			config.write("guild_event", "request_type", guildRequestType);
		}
	}

	public static void setToggleGuildLeaveMessage(boolean toggleGuildLeaveMessage) {
		if (ConfigUtils.toggleGuildLeaveMessage != toggleGuildLeaveMessage) {
			ConfigUtils.toggleGuildLeaveMessage = toggleGuildLeaveMessage;
			config.write("guild_event", "toggle_join_message", toggleGuildJoinMessage);
		}
	}

	public static void setGuildJoinMessage(String guildJoinMessage) {
		if (!ConfigUtils.guildJoinMessage.equals(guildJoinMessage)) {
			ConfigUtils.guildJoinMessage = guildJoinMessage;
			config.write("guild_event", "join_message", guildJoinMessage);
		}
	}

	public static void setToggleGuildJoinMessage(boolean toggleGuildJoinMessage) {
		if (ConfigUtils.toggleGuildJoinMessage != toggleGuildJoinMessage) {
			ConfigUtils.toggleGuildJoinMessage = toggleGuildJoinMessage;
			config.write("guild_event", "toggle_leave_message", toggleGuildLeaveMessage);
		}
	}

	public static void setGuildLeaveMessage(String guildLeaveMessage) {
		if (!ConfigUtils.guildLeaveMessage.equals(guildLeaveMessage)) {
			ConfigUtils.guildLeaveMessage = guildLeaveMessage;
			config.write("guild_event", "leave_message", guildLeaveMessage);
		}
	}

	public static void setToggleMysteryBoxSorter(boolean toggleMysteryBoxSorter) {
		if (ConfigUtils.toggleMysteryBoxSorter != toggleMysteryBoxSorter) {
			ConfigUtils.toggleMysteryBoxSorter = toggleMysteryBoxSorter;
			config.write("mystery_box", "toggle", toggleMysteryBoxSorter);
		}
	}

	public static void setMysteryBoxSortType(String mysteryBoxSortType) {
		if (!ConfigUtils.mysteryBoxSortType.equals(mysteryBoxSortType)) {
			ConfigUtils.mysteryBoxSortType = mysteryBoxSortType;
			config.write("mystery_box", "sort_type", mysteryBoxSortType);
		}
	}

	public static void setEnableCustomSidebar(boolean enableCustomSidebar) {
		if (ConfigUtils.enableCustomSidebar != enableCustomSidebar) {
			ConfigUtils.enableCustomSidebar = enableCustomSidebar;
			config.write("sidebar", "toggle", enableCustomSidebar);
		}
	}

	public static void setHideSidebarRedNumbers(boolean hideSidebarRedNumbers) {
		if (ConfigUtils.hideSidebarRedNumbers != hideSidebarRedNumbers) {
			ConfigUtils.hideSidebarRedNumbers = hideSidebarRedNumbers;
			config.write("sidebar", "toggle_red_numbers", hideSidebarRedNumbers);
		}
	}

	public static void setSidebarScale(double sidebarScale) {
		if (ConfigUtils.sidebarScale != sidebarScale) {
			ConfigUtils.sidebarScale = sidebarScale;
			config.write("sidebar", "scale", sidebarScale);
		}
	}

	public static void setSidebarXOffset(int sidebarXOffset) {
		if (ConfigUtils.sidebarXOffset != sidebarXOffset) {
			ConfigUtils.sidebarXOffset = sidebarXOffset;
			config.write("sidebar", "x_offset", sidebarXOffset);
		}
	}

	public static void setSidebarYOffset(int sidebarYOffset) {
		if (ConfigUtils.sidebarYOffset != sidebarYOffset) {
			ConfigUtils.sidebarYOffset = sidebarYOffset;
			config.write("sidebar", "y_offset", sidebarYOffset);
		}
	}

	public static void setSidebarBackgroundColor(String rgbCommaSeparated) {
		try {
			String[] rgb = rgbCommaSeparated.replace(" ", "").split(",");
			int r = Integer.parseInt(rgb[0]);
			int g = Integer.parseInt(rgb[1]);
			int b = Integer.parseInt(rgb[2]);
			int rgbDecimal = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
			if (sidebarBackgroundColor != rgbDecimal) {
				sidebarBackgroundColor = rgbDecimal;
				config.write("sidebar", "background_color", rgbDecimal);
			}
		} catch (Exception ignored) {}
	}

	public static void setSidebarAlpha(double sidebarAlpha) {
		if (ConfigUtils.sidebarAlpha != sidebarAlpha) {
			ConfigUtils.sidebarAlpha = sidebarAlpha;
			config.write("sidebar", "alpha", sidebarAlpha);
		}
	}

	public static void setSidebarChromaBackground(boolean sidebarChromaBackground) {
		if (ConfigUtils.sidebarChromaBackground != sidebarChromaBackground) {
			ConfigUtils.sidebarChromaBackground = sidebarChromaBackground;
			config.write("sidebar", "toggle_chroma_background", sidebarChromaBackground);
		}
	}

	public static void setSidebarChromaSpeed(int sidebarChromaSpeed) {
		if (ConfigUtils.sidebarChromaSpeed != sidebarChromaSpeed) {
			ConfigUtils.sidebarChromaSpeed = sidebarChromaSpeed;
			config.write("sidebar", "chroma_speed", sidebarChromaSpeed);
		}
	}

	public static void setMotdText(String motdText) {
		if (!ConfigUtils.motdText.equals(motdText)) {
			ConfigUtils.motdText = motdText;
			config.write("motd", "text", motdText);
		}
	}

	public static void setJacobLastYear(int jacobLastYear) {
		if (ConfigUtils.jacobLastYear != jacobLastYear) {
			ConfigUtils.jacobLastYear = jacobLastYear;
			config.write("misc", "jacob_last_year", jacobLastYear);
		}
	}
}
