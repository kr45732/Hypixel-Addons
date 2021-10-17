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

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.util.regex.Pattern;

public class ApiHandler {

	private static final Pattern minecraftUsernameRegex = Pattern.compile("^\\w+$", Pattern.CASE_INSENSITIVE);
	private static final Pattern minecraftUuidRegex = Pattern.compile(
		"[0-9a-f]{32}|[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
	);

	/* Username to uuid & uuid to username */
	public static UsernameUuidStruct usernameUuid(String username) {
		try {
			if (!isValidMinecraftUsername(username)) {
				if (!isValidMinecraftUuid(username)) {
					return new UsernameUuidStruct("No user with the name '" + username + "' was found");
				}
			}

			JsonElement usernameJson = Utils.getJson("https://api.ashcon.app/mojang/v2/user/" + username);
			//			JsonElement usernameJson = getJson("https://api.minetools.eu/uuid/" + username);
			try {
				return new UsernameUuidStruct(
					Utils.higherDepth(usernameJson, "username").getAsString(),
					Utils.higherDepth(usernameJson, "uuid").getAsString().replace("-", "")
					//					higherDepth(usernameJson, "name").getAsString(),
					//					higherDepth(usernameJson, "id").getAsString()
				);
			} catch (Exception e) {
				return new UsernameUuidStruct(Utils.higherDepth(usernameJson, "reason").getAsString());
			}
		} catch (Exception ignored) {}
		return new UsernameUuidStruct();
	}

	/* Hypixel API */
	public static HypixelResponse skyblockProfilesFromUuid(String uuid) {
		try {
			JsonElement profilesJson = Utils.getJson(
				"https://api.hypixel.net/skyblock/profiles?key=" + ConfigUtils.getHypixelKey() + "&uuid=" + uuid
			);

			try {
				if (Utils.higherDepth(profilesJson, "profiles").isJsonNull()) {
					return new HypixelResponse("Player has no SkyBlock profiles");
				}

				return new HypixelResponse(Utils.higherDepth(profilesJson, "profiles").getAsJsonArray());
			} catch (Exception e) {
				return new HypixelResponse(Utils.higherDepth(profilesJson, "cause").getAsString());
			}
		} catch (Exception ignored) {}

		return new HypixelResponse();
	}

	public static HypixelResponse playerFromUuid(String uuid) {
		try {
			JsonElement playerJson = Utils.getJson("https://api.hypixel.net/player?key=" + ConfigUtils.getHypixelKey() + "&uuid=" + uuid);

			try {
				if (Utils.higherDepth(playerJson, "player").isJsonNull()) {
					return new HypixelResponse("Player has not played on Hypixel");
				}

				return new HypixelResponse(Utils.higherDepth(playerJson, "player").getAsJsonObject());
			} catch (Exception e) {
				return new HypixelResponse(Utils.higherDepth(playerJson, "cause").getAsString());
			}
		} catch (Exception ignored) {}

		return new HypixelResponse();
	}

	public static HypixelResponse getGuildGeneric(String query) {
		try {
			JsonElement guildResponse = Utils.getJson("https://api.hypixel.net/guild?key=" + ConfigUtils.getHypixelKey() + query);

			try {
				if (Utils.higherDepth(guildResponse, "guild").isJsonNull()) {
					if (query.startsWith("&player=")) {
						return new HypixelResponse("Player is not in a guild");
					} else if (query.startsWith("&id=")) {
						return new HypixelResponse("Invalid guild id");
					} else if (query.startsWith("&name=")) {
						return new HypixelResponse("Invalid guild name");
					}
				}
				return new HypixelResponse(Utils.higherDepth(guildResponse, "guild").getAsJsonObject());
			} catch (Exception e) {
				return new HypixelResponse(Utils.higherDepth(guildResponse, "cause").getAsString());
			}
		} catch (Exception ignored) {}

		return new HypixelResponse();
	}

	public static HypixelResponse getGuildFromPlayer(String playerUuid) {
		return getGuildGeneric("&player=" + playerUuid);
	}

	public static HypixelResponse getAuctionGeneric(String query) {
		try {
			JsonElement auctionResponse = Utils.getJson(
				"https://api.hypixel.net/skyblock/auction?key=" + ConfigUtils.getHypixelKey() + query
			);
			try {
				return new HypixelResponse(Utils.higherDepth(auctionResponse, "auctions").getAsJsonArray());
			} catch (Exception e) {
				return new HypixelResponse(Utils.higherDepth(auctionResponse, "cause").getAsString());
			}
		} catch (Exception ignored) {}

		return new HypixelResponse();
	}

	public static HypixelResponse getAuctionFromPlayer(String playerUuid) {
		return getAuctionGeneric("&player=" + playerUuid);
	}

	/* Auctions API */
	//	public static JsonArray getBidsFromPlayer(String uuid) {
	//		try {
	//			HttpGet httpget = new HttpGet("https://api.eastarcti.ca/skyblock/auctions/prod/");
	//			httpget.addHeader("content-type", "application/json; charset=UTF-8");
	//
	//			URI uri = new URIBuilder(httpget.getURI())
	//				.addParameter("query", "{\"bids\":{\"$elemMatch\":{\"bidder\":\"" + uuid + "\"}}}")
	//				.build();
	//			httpget.setURI(uri);
	//
	//			try (CloseableHttpResponse httpResponse = Utils.httpClient.execute(httpget)) {
	//				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
	//			}
	//		} catch (Exception ignored) {}
	//		return null;
	//	}
	//
	//	public static JsonArray queryLowestBin(String query) {
	//		try {
	//			HttpGet httpget = new HttpGet("https://api.eastarcti.ca/skyblock/auctions/dev/");
	//			httpget.addHeader("content-type", "application/json; charset=UTF-8");
	//
	//			query = query.replace("[", "\\\\[");
	//			URI uri = new URIBuilder(httpget.getURI())
	//				.addParameter(
	//					"query",
	//					"{\"item_name\":{\"$regex\":\"" +
	//					query +
	//					"\",\"$options\":\"i\"},\"bin\":true,\"end\":{\"$gt\":" +
	//					Instant.now().toEpochMilli() +
	//					"}}"
	//				)
	//				.addParameter("sort", "{\"starting_bid\":1}")
	//				.addParameter("limit", "1")
	//				.build();
	//			httpget.setURI(uri);
	//
	//			try (CloseableHttpResponse httpResponse = Utils.httpClient.execute(httpget)) {
	//				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
	//			}
	//		} catch (Exception ignored) {}
	//		return null;
	//	}
	//
	//	public static JsonArray queryLowestBinPet(String petName, String rarity) {
	//		try {
	//			HttpGet httpGet = new HttpGet("https://api.eastarcti.ca/skyblock/auctions/dev/");
	//			httpGet.addHeader("content-type", "application/json; charset=UTF-8");
	//
	//			petName = petName.replace("[", "\\\\[");
	//			URI uri = new URIBuilder(httpGet.getURI())
	//				.addParameter(
	//					"query",
	//					"{\"item_name\":{\"$regex\":\"" +
	//					petName +
	//					"\",\"$options\":\"i\"}," +
	//					(!rarity.equalsIgnoreCase("any") ? "\"tier\":\"" + rarity.toUpperCase() + "\"," : "") +
	//					"\"bin\":true,\"end\":{\"$gt\":" +
	//					Instant.now().toEpochMilli() +
	//					"},\"item_id\":\"PET\"}"
	//				)
	//				.addParameter("sort", "{\"starting_bid\":1}")
	//				.addParameter("limit", "1")
	//				.build();
	//			httpGet.setURI(uri);
	//
	//			try (CloseableHttpResponse httpResponse = Utils.httpClient.execute(httpGet)) {
	//				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
	//			}
	//		} catch (Exception ignored) {}
	//		return null;
	//	}
	//
	//	public static JsonArray queryLowestBinEnchant(String enchantId, int enchantLevel) {
	//		try {
	//			HttpGet httpGet = new HttpGet("https://api.eastarcti.ca/skyblock/auctions/dev/");
	//			httpGet.addHeader("content-type", "application/json; charset=UTF-8");
	//
	//			URI uri = new URIBuilder(httpGet.getURI())
	//				.addParameter(
	//					"query",
	//					"{\"item_id\":\"ENCHANTED_BOOK\",\"end\":{\"$gt\":" +
	//					Instant.now().toEpochMilli() +
	//					"},\"nbt.value.i.value.value.0.tag.value.ExtraAttributes.value.enchantments.value." +
	//					enchantId.toLowerCase() +
	//					".value\":" +
	//					enchantLevel +
	//					",\"bin\":true}"
	//				)
	//				.addParameter("sort", "{\"starting_bid\":1}")
	//				.addParameter("limit", "1")
	//				.build();
	//			httpGet.setURI(uri);
	//
	//			try (CloseableHttpResponse httpResponse = Utils.httpClient.execute(httpGet)) {
	//				return new JsonParser().parse(new InputStreamReader(httpResponse.getEntity().getContent())).getAsJsonArray();
	//			}
	//		} catch (Exception ignored) {}
	//		return null;
	//	}

	/* Helper functions */
	private static boolean isValidMinecraftUsername(String username) {
		return username.length() > 2 && username.length() < 17 && minecraftUsernameRegex.matcher(username).find();
	}

	private static boolean isValidMinecraftUuid(String username) {
		return minecraftUuidRegex.matcher(username).matches();
	}
}
