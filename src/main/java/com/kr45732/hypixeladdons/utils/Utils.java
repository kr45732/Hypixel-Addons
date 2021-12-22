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

import static com.kr45732.hypixeladdons.utils.api.ApiHandler.playerFromUuid;

import com.google.gson.*;
import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.api.HypixelResponse;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.structs.InvItem;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import com.kr45732.hypixeladdons.utils.structs.WrappedText;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Utils {

	public static final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	public static final ExecutorService executor = new ExceptionExecutor();
	public static final Gson gson = new Gson();
	private static final Pattern SERVER_BRAND_PATTERN = Pattern.compile("(.+) <- .+");
	public static boolean onSkyblock;
	private static Instant bazaarJsonLastUpdated = Instant.now();
	/* JSONs */
	private static JsonElement levelingJson;
	private static JsonObject internalJsonMappings;
	private static JsonElement essenceCostsJson;
	private static JsonElement bazaarJson;
	private static JsonElement bitPricesJson;
	private static JsonElement petNumsJson;
	private static JsonElement enchantsJson;

	/* Getters */
	public static JsonElement getBitPricesJson() {
		if (bitPricesJson == null) {
			bitPricesJson = getJson("https://raw.githubusercontent.com/kr45732/skyblock-plus-data/main/BitPricesJson.json");
		}

		return bitPricesJson;
	}

	public static JsonElement getEnchantsJson() {
		if (enchantsJson == null) {
			enchantsJson =
				getJson("https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/constants/enchants.json");
		}
		return enchantsJson;
	}

	public static JsonElement getBazaarJson() {
		if (bazaarJson == null || Duration.between(bazaarJsonLastUpdated, Instant.now()).toMinutes() >= 1) {
			bazaarJson = getJson("https://api.slothpixel.me/api/skyblock/bazaar");
			bazaarJsonLastUpdated = Instant.now();
		}

		return bazaarJson;
	}

	public static JsonElement getPetNumsJson() {
		if (petNumsJson == null) {
			petNumsJson = getJson("https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/constants/petnums.json");
		}
		return petNumsJson;
	}

	public static JsonElement getEssenceCostsJson() {
		if (essenceCostsJson == null) {
			essenceCostsJson =
				getJson("https://raw.githubusercontent.com/Moulberry/NotEnoughUpdates-REPO/master/constants/essencecosts.json");
		}
		return essenceCostsJson;
	}

	public static JsonElement getLevelingJson() {
		if (levelingJson == null) {
			levelingJson = getJson("https://raw.githubusercontent.com/Moulberry/NotEnoughUpdates-REPO/master/constants/leveling.json");
		}
		return levelingJson;
	}

	public static void setInternalJsonMappings() {
		if (internalJsonMappings == null) {
			internalJsonMappings =
				getJson("https://raw.githubusercontent.com/kr45732/skyblock-plus-data/main/InternalNameMappings.json").getAsJsonObject();
		}
	}

	/* Http requests */
	public static JsonElement getJson(String jsonUrl) {
		try {
			HttpGet httpget = new HttpGet(jsonUrl);
			httpget.addHeader("content-type", "application/json; charset=UTF-8");

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpget)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent()));
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static String makeHastePost(String body) {
		try {
			HttpPost httpPost = new HttpPost("https://hst.sh/documents");

			StringEntity entity = new StringEntity(body);
			httpPost.setEntity(entity);

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
				return (
					"https://hst.sh/" +
					higherDepth(new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())), "key").getAsString()
				);
			}
		} catch (Exception ignored) {}
		return null;
	}

	/* Chat formatting */
	public static String label(String text) {
		return C.AQUA + text + C.RESET;
	}

	public static String desc(String text) {
		return C.GOLD + text + C.RESET;
	}

	public static String labelWithDesc(String label, String desc) {
		return label(label + ": ") + desc(desc);
	}

	public static String arrow() {
		return C.GRAY + " ➜ " + C.RESET;
	}

	public static ChatComponentText getUsage(CommandBase command) {
		return wrapText("Usage: " + command.getCommandUsage(null), true);
	}

	public static String getUsageChat(CommandBase command) {
		return "Usage: " + command.getCommandUsage(null);
	}

	public static ChatComponentText invalidKey() {
		return wrapText("API key not set. Use /hpa:setkey.", true);
	}

	public static String invalidKeyChat() {
		return "API key not set. Please tell the sender to set the key.";
	}

	public static ChatComponentText getFailCause(Object obj) {
		if (obj instanceof Player) {
			return wrapText(((Player) obj).getFailCause(), true);
		} else if (obj instanceof UsernameUuidStruct) {
			return wrapText(((UsernameUuidStruct) obj).getFailCause(), true);
		} else if (obj instanceof HypixelResponse) {
			return wrapText(((HypixelResponse) obj).failCause, true);
		} else if (obj instanceof String) {
			return wrapText(((String) obj), true);
		} else if (obj instanceof HypixelPlayer) {
			return wrapText(((HypixelPlayer) obj).getFailCause(), true);
		}
		return wrapText("Unknown Fail Cause", true);
	}

	public static String getFailCauseChat(Object obj) {
		if (obj instanceof Player) {
			return ((Player) obj).getFailCause();
		} else if (obj instanceof UsernameUuidStruct) {
			return ((UsernameUuidStruct) obj).getFailCause();
		} else if (obj instanceof HypixelResponse) {
			return ((HypixelResponse) obj).failCause;
		} else if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof HypixelPlayer) {
			return ((HypixelPlayer) obj).getFailCause();
		}

		return "Unknown Fail Cause";
	}

	public static IChatComponent empty() {
		return new ChatComponentText("");
	}

	public static ChatComponentText wrapText(String text) {
		return wrapText(text, false);
	}

	public static ChatComponentText wrapText(String text, boolean isError) {
		String color = "" + C.RESET + (isError ? C.DARK_RED : C.DARK_BLUE);
		String dashColor = color + C.STRIKETHROUGH;
		String logoColor = "" + C.RESET + C.GOLD;
		return new ChatComponentText(
			dashColor +
			"---------------" +
			color +
			"{" +
			logoColor +
			"HPA" +
			color +
			"}" +
			dashColor +
			"---------------" +
			C.RESET +
			"\n" +
			text +
			"\n" +
			dashColor +
			"----------------------------------"
		);
	}

	public static IChatComponent wrapText(IChatComponent text) {
		String color = "" + C.RESET + C.DARK_BLUE;
		String dashColor = color + C.STRIKETHROUGH;
		String logoColor = "" + C.RESET + C.GOLD;
		return new ChatComponentText(
			dashColor + "---------------" + color + "{" + logoColor + "HPA" + color + "}" + dashColor + "---------------" + C.RESET + "\n"
		)
			.appendSibling(text)
			.appendText("\n" + dashColor + "----------------------------------");
	}

	public static String getFormattedUsername(String playerUuid) {
		return getFormattedUsername(playerFromUuid(playerUuid).response.getAsJsonObject());
	}

	public static String getFormattedUsername(JsonObject hypixelJson) {
		String hypixelRank = "NONE";
		if (higherDepth(hypixelJson, "prefix") != null) {
			return (
				higherDepth(hypixelJson, "prefix").getAsString() +
				(higherDepth(hypixelJson, "prefix").getAsString().endsWith("]") ? " " : "") +
				higherDepth(hypixelJson, "displayname").getAsString()
			);
		} else if (higherDepth(hypixelJson, "rank") != null && !higherDepth(hypixelJson, "rank").getAsString().equals("NORMAL")) {
			hypixelRank = capitalizeString(higherDepth(hypixelJson, "rank").getAsString());
		} else if (
			higherDepth(hypixelJson, "monthlyPackageRank") != null &&
			higherDepth(hypixelJson, "monthlyPackageRank").getAsString().equals("SUPERSTAR")
		) {
			hypixelRank = "MVP_PLUS_PLUS";
		} else if (
			higherDepth(hypixelJson, "newPackageRank") != null && !higherDepth(hypixelJson, "newPackageRank").getAsString().equals("NONE")
		) {
			hypixelRank = higherDepth(hypixelJson, "newPackageRank").getAsString();
		} else if (
			higherDepth(hypixelJson, "packageRank") != null && !higherDepth(hypixelJson, "packageRank").getAsString().equals("NONE")
		) {
			hypixelRank = higherDepth(hypixelJson, "packageRank").getAsString();
		}
		hypixelRank = hypixelRank.toUpperCase();

		String plusColour =
			"" + (hypixelJson.has("rankPlusColor") ? C.getValueByName(hypixelJson.get("rankPlusColor").getAsString()) : C.RED);
		String plusPlusColour =
			"" + (hypixelJson.has("monthlyRankColor") ? C.getValueByName(hypixelJson.get("monthlyRankColor").getAsString()) : C.GOLD);

		switch (hypixelRank) {
			case "VIP":
				hypixelRank = C.GREEN + "[VIP]";
				break;
			case "VIP_PLUS":
				hypixelRank = C.GREEN + "[VIP" + C.GOLD + "+" + C.GREEN + "]";
				break;
			case "MVP":
				hypixelRank = C.AQUA + "[MVP]";
				break;
			case "MVP_PLUS":
				hypixelRank = C.AQUA + "[MVP" + plusColour + "+" + C.AQUA + "]";
				break;
			case "MVP_PLUS_PLUS":
				hypixelRank = plusPlusColour + "[MVP" + plusColour + "++" + plusPlusColour + "]";
				break;
			case "JR_HELPER":
				hypixelRank = C.BLUE + "[JR HELPER]";
				break;
			case "HELPER":
				hypixelRank = C.BLUE + "[HELPER]";
				break;
			case "MODERATOR":
				hypixelRank = C.DARK_GREEN + "[MOD]";
				break;
			case "GAME_MASTER":
				hypixelRank = C.DARK_GREEN + "[GM]";
				break;
			case "ADMIN":
				hypixelRank = C.RED + "[ADMIN]";
				break;
			case "YOUTUBER":
				hypixelRank = C.RED + "[" + C.WHITE + "YOUTUBE" + C.RED + "]";
				break;
			default:
				hypixelRank = "" + C.GRAY;
				break;
		}

		return hypixelRank + (hypixelRank.endsWith("]") ? " " : "") + higherDepth(hypixelJson, "displayname").getAsString();
	}

	public static String nbtToTooltip(NBTCompound nbt) {
		StringBuilder output = new StringBuilder();
		output.append(nbt.getString("tag.display.Name", "Error")).append(C.RESET).append("\n");

		NBTList lore = nbt.getList("tag.display.Lore");
		if (lore != null) {
			for (Object line : lore) {
				output.append(line).append("\n");
			}
		}

		output.append(C.RESET);

		return output.toString().trim();
	}

	/* Number utils */
	public static String roundProgress(double number) {
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(number * 100) + "%";
	}

	public static String roundAndFormat(double number) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return formatNumber(Double.parseDouble(df.format(number)));
	}

	public static String formatNumber(double number) {
		return NumberFormat.getInstance(Locale.US).format(number);
	}

	public static String simplifyNumber(double number) {
		String formattedNumber;
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		if (1000000000000D > number && number >= 1000000000) {
			df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
			number = number >= 999999999950D ? 999999999949D : number;
			formattedNumber = df.format(number / 1000000000) + "B";
		} else if (number >= 1000000) {
			number = number >= 999999950D ? 999999949D : number;
			formattedNumber = df.format(number / 1000000) + "M";
		} else if (number >= 1000) {
			number = number >= 999950D ? 999949D : number;
			formattedNumber = df.format(number / 1000) + "K";
		} else if (number < 1) {
			formattedNumber = "0";
		} else {
			df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
			formattedNumber = df.format(number);
		}
		return formattedNumber;
	}

	/* Miscellaneous */
	public static Map<Integer, InvItem> getGenericInventoryMap(NBTCompound parsedContents) {
		try {
			NBTList items = parsedContents.getList("i");
			Map<Integer, InvItem> itemsMap = new HashMap<>();

			for (int i = 0; i < items.size(); i++) {
				try {
					NBTCompound item = items.getCompound(i);
					if (!item.isEmpty()) {
						InvItem itemInfo = new InvItem();
						itemInfo.setName(StringUtils.stripControlCodes(item.getString("tag.display.Name", "None")));
						itemInfo.setLore(
							StringUtils.stripControlCodes(
								item.getString("tag.display.Lore", "None").replace(", ", "\n").replace("[", "").replace("]", "")
							)
						);
						itemInfo.setCount(Integer.parseInt(item.getString("Count", "0").replace("b", " ")));
						itemInfo.setId(item.getString("tag.ExtraAttributes.id", "None"));
						itemInfo.setCreationTimestamp(item.getString("tag.ExtraAttributes.timestamp", "None"));
						itemInfo.setHbpCount(item.getInt("tag.ExtraAttributes.hot_potato_count", 0));
						itemInfo.setRecombobulated(item.getInt("tag.ExtraAttributes.rarity_upgrades", 0) == 1);
						itemInfo.setModifier(item.getString("tag.ExtraAttributes.modifier", "None"));
						itemInfo.setDungeonFloor(Integer.parseInt(item.getString("tag.ExtraAttributes.item_tier", "-1")));
						itemInfo.setNbtTag(item);

						try {
							NBTCompound enchants = item.getCompound("tag.ExtraAttributes.enchantments");
							List<String> enchantsList = new ArrayList<>();
							for (Map.Entry<String, Object> enchant : enchants.entrySet()) {
								enchantsList.add(enchant.getKey() + ";" + enchant.getValue());
							}
							itemInfo.setEnchantsFormatted(enchantsList);
						} catch (Exception ignored) {}

						String itemSkinStr = item.getString("tag.ExtraAttributes.skin", "None");
						if (!itemSkinStr.equals("None")) {
							itemInfo.addExtraValue("PET_SKIN_" + itemSkinStr);
						}

						try {
							NBTList necronBladeScrolls = item.getList("tag.ExtraAttributes.ability_scroll");
							for (Object scroll : necronBladeScrolls) {
								try {
									itemInfo.addExtraValue("" + scroll);
								} catch (Exception ignored) {}
							}
						} catch (Exception ignored) {}

						if (item.getInt("tag.ExtraAttributes.wood_singularity_count", 0) == 1) {
							itemInfo.addExtraValue("WOOD_SINGULARITY");
						}

						try {
							byte[] backpackContents = item.getByteArray("tag.ExtraAttributes." + itemInfo.getId().toLowerCase() + "_data");
							NBTCompound parsedContentsBackpack = NBTReader.read(new ByteArrayInputStream(backpackContents));
							itemInfo.setBackpackItems(getGenericInventoryMap(parsedContentsBackpack).values());
						} catch (Exception ignored) {}

						itemsMap.put(i, itemInfo);
						continue;
					}
				} catch (Exception ignored) {}
				itemsMap.put(i, null);
			}

			return itemsMap;
		} catch (Exception ignored) {}

		return null;
	}

	public static String nameToId(String itemName) {
		setInternalJsonMappings();

		String internalName = itemName
			.trim()
			.toUpperCase()
			.replace(" ", "_")
			.replace("'S", "")
			.replace("FRAG", "FRAGMENT")
			.replace(".", "");

		switch (internalName) {
			case "GOD_POT":
				internalName = "GOD_POTION";
				break;
			case "AOTD":
				internalName = "ASPECT_OF_THE_DRAGON";
				break;
			case "AOTE":
				internalName = "ASPECT_OF_THE_END";
				break;
			case "AOTV":
				internalName = "ASPECT_OF_THE_VOID";
				break;
			case "AOTS:":
				internalName = "AXE_OF_THE_SHREDDED";
				break;
			case "LASR_EYE":
				internalName = "GIANT_FRAGMENT_LASER";
				break;
		}

		try {
			internalName = internalJsonMappings.getAsJsonArray(internalName).get(0).getAsString();
		} catch (Exception ignored) {}

		return internalName;
	}

	public static String skyblockStatsLink(String username, String profileName) {
		if (username == null) {
			return null;
		}
		return ("https://sky.shiiyu.moe/stats/" + username + (profileName != null ? "/" + profileName : ""));
	}

	public static String idToName(String id) {
		setInternalJsonMappings();

		id = id.toUpperCase();

		for (Map.Entry<String, JsonElement> i : internalJsonMappings.entrySet()) {
			for (JsonElement j : i.getValue().getAsJsonArray()) {
				if (j.getAsString().equals(id)) {
					return capitalizeString(i.getKey().replace("_", " "));
				}
			}
		}

		return capitalizeString(id.replace("_", " "));
	}

	public static JsonElement higherDepth(JsonElement element, String path) {
		String[] paths = path.split("\\.");

		try {
			for (String key : paths) {
				if (key.length() >= 3 && key.startsWith("[") && key.endsWith("]")) {
					element = element.getAsJsonArray().get(Integer.parseInt(key.substring(1, key.length() - 1)));
				} else {
					element = element.getAsJsonObject().get(key);
				}
			}
			return element;
		} catch (Exception e) {
			return null;
		}
	}

	public static JsonObject higherDepth(JsonElement element, String path, JsonObject defaultValue) {
		try {
			return higherDepth(element, path).getAsJsonObject();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String higherDepth(JsonElement element, String path, String defaultValue) {
		try {
			return higherDepth(element, path).getAsString();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int higherDepth(JsonElement element, String path, int defaultValue) {
		try {
			return higherDepth(element, path).getAsInt();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long higherDepth(JsonElement element, String path, long defaultValue) {
		try {
			return higherDepth(element, path).getAsLong();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double higherDepth(JsonElement element, String path, double defaultValue) {
		try {
			return higherDepth(element, path).getAsDouble();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static boolean higherDepth(JsonElement element, String path, boolean defaultValue) {
		try {
			return higherDepth(element, path).getAsBoolean();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static ArrayList<String> getJsonKeys(JsonElement jsonElement) {
		try {
			return jsonElement
				.getAsJsonObject()
				.entrySet()
				.stream()
				.map(Map.Entry::getKey)
				.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public static String getClosestMatch(String toMatch, List<String> matchFrom) {
		if (matchFrom == null || matchFrom.size() == 0) {
			return toMatch;
		}

		int minDistance = applyLevenshteinDistance(matchFrom.get(0), toMatch);
		String closestMatch = matchFrom.get(0);
		for (String itemF : matchFrom) {
			int currentDistance = applyLevenshteinDistance(itemF, toMatch);
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
				closestMatch = itemF;
			}
		}

		return closestMatch;
	}

	public static String capitalizeString(String str) {
		return Stream
			.of(str.trim().split("\\s"))
			.filter(word -> word.length() > 0)
			.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
			.collect(Collectors.joining(" "));
	}

	public static String instantToDHM(Duration duration) {
		if (duration.toMinutes() < 1) {
			return instantToMS(duration);
		}

		long daysUntil = duration.toMinutes() / 1440;
		long hoursUntil = duration.toMinutes() / 60 % 24;
		long minutesUntil = duration.toMinutes() % 60;
		String timeUntil = daysUntil > 0 ? daysUntil + "d " : "";
		timeUntil += hoursUntil > 0 ? hoursUntil + "h " : "";
		timeUntil += minutesUntil > 0 ? minutesUntil + "m " : "";

		return timeUntil.length() > 0 ? timeUntil.trim() : "0m";
	}

	public static String instantToMS(Duration duration) {
		long secondsDuration = duration.toMillis() / 1000;
		long minutesUntil = secondsDuration / 60 % 60;
		long secondsUntil = secondsDuration % 60;

		String timeUntil = minutesUntil > 0 ? minutesUntil + "m " : "";
		timeUntil += secondsUntil > 0 ? secondsUntil + "s " : "";

		return timeUntil.length() > 0 ? timeUntil.trim() : "0s";
	}

	public static double divide(double d1, double d2) {
		return d2 == 0 ? 0 : (d1 / d2);
	}

	/**
	 * @return Returns whether the current player is on Hypixel & Skyblock
	 */
	public static boolean isOnSkyblock() {
		return onSkyblock;
	}

	/**
	 * @return Returns whether the current player is on Hypixel
	 */
	public static boolean isOnHypixel() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && !mc.isSingleplayer() && mc.thePlayer != null && mc.thePlayer.getClientBrand() != null) {
			Matcher matcher = SERVER_BRAND_PATTERN.matcher(mc.thePlayer.getClientBrand());

			if (matcher.find()) {
				return matcher.group(1).startsWith("Hypixel BungeeCord");
			}
		}

		return false;
	}

	/**
	 * @param left String one
	 * @param right String two
	 * @return The levenshtein distance between the two strings
	 */
	public static int applyLevenshteinDistance(CharSequence left, CharSequence right) {
		int n = left.length();
		int m = right.length();

		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}

		if (n > m) {
			final CharSequence tmp = left;
			left = right;
			right = tmp;
			n = m;
			m = right.length();
		}

		final int[] p = new int[n + 1];

		int i;
		int j;
		int upperLeft;
		int upper;

		char rightJ;
		int cost;

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			upperLeft = p[0];
			rightJ = right.charAt(j - 1);
			p[0] = j;

			for (i = 1; i <= n; i++) {
				upper = p[i];
				cost = left.charAt(i - 1) == rightJ ? 0 : 1;
				p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
				upperLeft = upper;
			}
		}

		return p[n];
	}

	public static String getUsername(String[] args, int idx) {
		return args.length > idx ? args[idx] : Minecraft.getMinecraft().thePlayer.getName();
	}

	/**
	 * If index isn't in the array, then it will use the current player's username. Profile, if provided, should always be at index + 1.
	 * @param args Command args
	 * @param index Where the username should be located
	 * @return New player instance
	 */
	public static Player newPlayer(String[] args, int index) {
		String username = getUsername(args, index);
		return args.length > index + 1 ? new Player(username, args[index + 1]) : new Player(username);
	}

	/**
	 * Username index of 0 (since most commands have the username at 0)
	 */
	public static Player newPlayer(String[] args) {
		return newPlayer(args, 0);
	}

	/**
	 * If index isn't in the array, then it will use the current player's username
	 * @param args Command args
	 * @param index Where the username should be located
	 * @return New HypixelPlayer instance
	 */
	public static HypixelPlayer newHypixelPlayer(String[] args, int index) {
		return new HypixelPlayer(getUsername(args, index));
	}

	/**
	 * Username index of 0 (since most commands have the username at 0)
	 */
	public static HypixelPlayer newHypixelPlayer(String[] args) {
		return newHypixelPlayer(args, 0);
	}

	/**
	 * Convert args to new array with a set limit
	 */
	public static String[] convertArgs(String[] args, int limit) {
		return String.join(" ", args).split(" ", limit);
	}

	public static List<String> wrapToWidth(String str, int wrapWidth) {
		List<String> strings = new ArrayList<>();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n' || Minecraft.getMinecraft().fontRendererObj.getStringWidth(temp.toString() + c) >= wrapWidth) {
				strings.add(temp.toString());
				temp = new StringBuilder();
			}

			if (c != '\n') {
				temp.append(c);
			}
		}
		strings.add(temp.toString());
		return strings;
	}

	public static List<WrappedText> wrapToWidthWithIndication(String str, int wrapWidth) {
		List<WrappedText> strings = new ArrayList<>();
		StringBuilder temp = new StringBuilder();
		boolean wrapped = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\n') {
				strings.add(new WrappedText(temp.toString(), wrapped));
				temp = new StringBuilder();
				wrapped = false;
			} else if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(temp.toString() + c) >= wrapWidth) {
				strings.add(new WrappedText(temp.toString(), wrapped));
				temp = new StringBuilder();
				wrapped = true;
			}

			if (c != '\n') {
				temp.append(c);
			}
		}
		strings.add(new WrappedText(temp.toString(), wrapped));
		return strings;
	}

	public static JsonArray getBidsFromPlayer(String uuid) {
		try {
			HttpGet httpget = new HttpGet("https://query-api.herokuapp.com/query");
			httpget.addHeader("content-type", "application/json; charset=UTF-8");

			URI uri = new URIBuilder(httpget.getURI()).addParameter("bids", uuid).addParameter("limit", "-1").build();
			httpget.setURI(uri);

			try (CloseableHttpResponse httpResponse = Utils.httpClient.execute(httpget)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static JsonArray queryLowestBin(String query) {
		try {
			HttpGet httpget = new HttpGet("hhttps://query-api.herokuapp.com/query");
			httpget.addHeader("content-type", "application/json; charset=UTF-8");

			URI uri = new URIBuilder(httpget.getURI())
				.addParameter("end", "" + Instant.now().toEpochMilli())
				.addParameter("item_name", "%" + query + "%")
				.addParameter("bin", "true")
				.addParameter("sort", "ASC")
				.build();
			httpget.setURI(uri);

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpget)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static JsonArray queryLowestBinPet(String petName, String rarity) {
		try {
			HttpGet httpGet = new HttpGet("https://query-api.herokuapp.com//query");
			httpGet.addHeader("content-type", "application/json; charset=UTF-8");

			URIBuilder uri = new URIBuilder(httpGet.getURI())
				.addParameter("end", "" + Instant.now().toEpochMilli())
				.addParameter("item_name", "%" + petName + "%")
				.addParameter("item_id", "PET")
				.addParameter("bin", "true")
				.addParameter("sort", "ASC");
			if (!rarity.equals("ANY")) {
				uri.addParameter("tier", rarity);
			}
			httpGet.setURI(uri.build());

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static JsonArray queryLowestBinEnchant(String enchantId, int enchantLevel) {
		try {
			HttpGet httpGet = new HttpGet("https://query-api.herokuapp.com/query");
			httpGet.addHeader("content-type", "application/json; charset=UTF-8");

			URI uri = new URIBuilder(httpGet.getURI())
				.addParameter("end", "" + Instant.now().toEpochMilli())
				.addParameter("item_id", "ENCHANTED_BOOK")
				.addParameter("enchants", enchantId.toUpperCase() + ";" + enchantLevel)
				.addParameter("bin", "true")
				.addParameter("sort", "ASC")
				.build();
			httpGet.setURI(uri);

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static JsonElement postJson(String url, JsonElement body, Header... headers) {
		try {
			HttpPost httpPost = new HttpPost(url);

			StringEntity entity = new StringEntity(body.toString());
			httpPost.setEntity(entity);
			httpPost.setHeaders(headers);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "application/json");

			try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent()));
			}
		} catch (Exception ignored) {}
		return null;
	}
}
/*
Black         §0
Dark Blue     §1
Dark Green    §2
Dark Aqua     §3
Dark Red      §4
DARK_PURPLE	  §5
GOLD	      §6
GRAY	      §7
DARK_GRAY	  §8
BLUE	      §9
GREEN	      §a
AQUA	      §b
RED	          §c
LIGHT_PURPLE  §d
YELLOW	      §e
WHITE	      §f
*/
