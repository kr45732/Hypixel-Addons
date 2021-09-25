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

package com.kr45732.hypixeladdons.utils.chat;

import java.util.HashMap;
import java.util.Map;

public enum C {
	BLACK("BLACK", '0'), // O
	DARK_BLUE("DARK_BLUE", '1'),
	DARK_GREEN("DARK_GREEN", '2'),
	DARK_AQUA("DARK_AQUA", '3'),
	DARK_RED("DARK_RED", '4'),
	DARK_PURPLE("DARK_PURPLE", '5'),
	GOLD("GOLD", '6'),
	GRAY("GRAY", '7'),
	DARK_GRAY("DARK_GRAY", '8'),
	BLUE("BLUE", '9'),
	GREEN("GREEN", 'a'),
	AQUA("AQUA", 'b'),
	RED("RED", 'c'),
	LIGHT_PURPLE("LIGHT_PURPLE", 'd'),
	YELLOW("YELLOW", 'e'),
	WHITE("WHITE", 'f'),
	OBFUSCATED("OBFUSCATED", 'k'),
	BOLD("BOLD", 'l'),
	STRIKETHROUGH("STRIKETHROUGH", 'm'),
	UNDERLINE("UNDERLINE", 'n'),
	ITALIC("ITALIC", 'o'),
	RESET("RESET", 'r');

	private static final Map<String, C> nameMapping = new HashMap<>();

	static {
		for (C enumChatFormatting : values()) {
			nameMapping.put(doesSomeRegex(enumChatFormatting.name), enumChatFormatting);
		}
	}

	private final String name;
	private final String code;

	C(String name, char code) {
		this.name = name;
		this.code = "\u00a7" + code;
	}

	private static String doesSomeRegex(String inStr) {
		return inStr.toLowerCase().replaceAll("[^a-z]", "");
	}

	public static C getValueByName(String friendlyName) {
		return friendlyName == null ? null : nameMapping.get(doesSomeRegex(friendlyName));
	}

	public String toString() {
		return this.code;
	}
}
