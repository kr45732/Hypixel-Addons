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

package com.kr45732.hypixeladdons.commands.guild;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.*;
import com.kr45732.hypixeladdons.utils.api.ApiHandler;
import com.kr45732.hypixeladdons.utils.api.HypixelResponse;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class GuildCommand extends CommandBase {

	public static GuildCommand INSTANCE = new GuildCommand();

	private static int guildExpToLevel(int guildExp) {
		int guildLevel = 0;

		for (int i = 0;; i++) {
			int expNeeded = i >= Constants.GUILD_EXP_TO_LEVEL.size()
				? Constants.GUILD_EXP_TO_LEVEL.get(Constants.GUILD_EXP_TO_LEVEL.size() - 1)
				: Constants.GUILD_EXP_TO_LEVEL.get(i);
			guildExp -= expNeeded;
			if (guildExp < 0) {
				return guildLevel;
			} else {
				guildLevel++;
			}
		}
	}

	private static String getGuildInfo(JsonElement guildJson) {
		JsonArray guildMembers = Utils.higherDepth(guildJson, "members").getAsJsonArray();

		String output = "";
		output +=
			Utils.labelWithDesc(
				"Tag",
				C.getValueByName(Utils.higherDepth(guildJson, "tagColor").getAsString()) +
				"[" +
				Utils.higherDepth(guildJson, "tag").getAsString() +
				"]"
			);
		output += "\n" + Utils.labelWithDesc("Members", guildMembers.size() + "/125");

		for (int i = 0; i < guildMembers.size(); i++) {
			JsonElement currentMember = guildMembers.get(i).getAsJsonObject();
			if (Utils.higherDepth(currentMember, "rank").getAsString().equals("Guild Master")) {
				output +=
					"\n" +
					Utils.labelWithDesc(
						"Guild master",
						ApiHandler.usernameUuid(Utils.higherDepth(currentMember, "uuid").getAsString()).playerUsername
					);
				break;
			}
		}

		String[] date = Date.from(Instant.ofEpochMilli(Utils.higherDepth(guildJson, "created").getAsLong())).toString().split(" ");
		output += "\n" + Utils.labelWithDesc("Created", date[1] + " " + date[2] + ", " + date[5]);
		output += "\n" + Utils.labelWithDesc("Level", "" + guildExpToLevel(Utils.higherDepth(guildJson, "exp").getAsInt()));

		try {
			JsonArray preferredGames = Utils.higherDepth(guildJson, "guild.preferredGames").getAsJsonArray();
			if (preferredGames.size() > 1) {
				String prefString = preferredGames.toString();
				prefString = prefString.substring(1, prefString.length() - 1).toLowerCase().replace("\"", "").replace(",", ", ");
				String firstHalf = prefString.substring(0, prefString.lastIndexOf(","));
				String lastHalf = prefString.substring(prefString.lastIndexOf(",") + 1);
				if (preferredGames.size() > 2) {
					output += "\n" + Utils.labelWithDesc("Preferred games", firstHalf + ", and" + lastHalf);
				} else {
					output += "\n" + Utils.labelWithDesc("Preferred games", firstHalf + " and" + lastHalf);
				}
			} else if (preferredGames.size() == 1) {
				output += "\n" + Utils.labelWithDesc("Preferred game", preferredGames.get(0).getAsString().toLowerCase());
			}
		} catch (Exception ignored) {}

		return output;
	}

	public static IChatComponent getGuildString(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKey();
		}

		if (args.length == 1) {
			UsernameUuidStruct uuidUsername = ApiHandler.usernameUuid(args[0]);
			if (uuidUsername.isNotValid()) {
				return Utils.getFailCause(uuidUsername);
			}

			HypixelResponse response = ApiHandler.getGuildFromPlayer(uuidUsername.playerUuid);
			if (response.isNotValid()) {
				return Utils.getFailCause(response);
			}

			return Utils.wrapText(
				Utils
					.empty()
					.appendSibling(
						new ChatText(
							Utils.getFormattedUsername(uuidUsername.playerUuid) +
							Utils.desc(" is in " + C.UNDERLINE + response.get("name").getAsString())
						)
							.setClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/guild/player/" + uuidUsername.playerUuid)
							.build()
					)
					.appendSibling(new ChatComponentText("\n" + getGuildInfo(response.response)))
			);
		}

		return Utils.getUsage(INSTANCE);
	}

	public static String getGuildChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKeyChat();
		}

		if (args.length == 1) {
			UsernameUuidStruct uuidUsername = ApiHandler.usernameUuid(args[0]);
			if (uuidUsername.isNotValid()) {
				return Utils.getFailCauseChat(uuidUsername);
			}

			HypixelResponse response = ApiHandler.getGuildFromPlayer(uuidUsername.playerUuid);
			if (response.isNotValid()) {
				return Utils.getFailCauseChat(response);
			}

			return (
				StringUtils.stripControlCodes(Utils.getFormattedUsername(uuidUsername.playerUuid)) +
				" is in " +
				response.get("name").getAsString()
			);
		}

		return Utils.getUsageChat(INSTANCE);
	}

	@Override
	public String getCommandName() {
		return "hpa:guild";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:g");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>\n/" + getCommandName() + " information <player>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getGuildString(args)));
	}
}
