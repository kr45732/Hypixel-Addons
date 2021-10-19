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

package com.kr45732.hypixeladdons.commands.guild;

import static com.kr45732.hypixeladdons.utils.Constants.GUILD_EXP_TO_LEVEL;
import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.getGuildFromPlayer;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.usernameUuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

	public static final GuildCommand INSTANCE = new GuildCommand();

	private int guildExpToLevel(int guildExp) {
		int guildLevel = 0;

		for (int i = 0;; i++) {
			int expNeeded = i >= GUILD_EXP_TO_LEVEL.size()
				? GUILD_EXP_TO_LEVEL.get(GUILD_EXP_TO_LEVEL.size() - 1)
				: GUILD_EXP_TO_LEVEL.get(i);
			guildExp -= expNeeded;
			if (guildExp < 0) {
				return guildLevel;
			} else {
				guildLevel++;
			}
		}
	}

	private String getGuildInfo(JsonElement guildJson) {
		JsonArray guildMembers = higherDepth(guildJson, "members").getAsJsonArray();

		String output = "";
		output +=
			labelWithDesc(
				"Tag",
				C.getValueByName(higherDepth(guildJson, "tagColor").getAsString()) + "[" + higherDepth(guildJson, "tag").getAsString() + "]"
			);
		output += "\n" + labelWithDesc("Members", guildMembers.size() + "/125");

		for (int i = 0; i < guildMembers.size(); i++) {
			JsonElement currentMember = guildMembers.get(i).getAsJsonObject();
			if (higherDepth(currentMember, "rank").getAsString().equals("Guild Master")) {
				output +=
					"\n" + labelWithDesc("Guild master", usernameUuid(higherDepth(currentMember, "uuid").getAsString()).getUsername());
				break;
			}
		}

		String[] date = Date.from(Instant.ofEpochMilli(higherDepth(guildJson, "created").getAsLong())).toString().split(" ");
		output += "\n" + labelWithDesc("Created", date[1] + " " + date[2] + ", " + date[5]);
		output += "\n" + labelWithDesc("Level", "" + guildExpToLevel(higherDepth(guildJson, "exp").getAsInt()));

		try {
			JsonArray preferredGames = higherDepth(guildJson, "guild.preferredGames").getAsJsonArray();
			if (preferredGames.size() > 1) {
				String prefString = preferredGames.toString();
				prefString = prefString.substring(1, prefString.length() - 1).toLowerCase().replace("\"", "").replace(",", ", ");
				String firstHalf = prefString.substring(0, prefString.lastIndexOf(","));
				String lastHalf = prefString.substring(prefString.lastIndexOf(",") + 1);
				if (preferredGames.size() > 2) {
					output += "\n" + labelWithDesc("Preferred games", firstHalf + ", and" + lastHalf);
				} else {
					output += "\n" + labelWithDesc("Preferred games", firstHalf + " and" + lastHalf);
				}
			} else if (preferredGames.size() == 1) {
				output += "\n" + labelWithDesc("Preferred game", preferredGames.get(0).getAsString().toLowerCase());
			}
		} catch (Exception ignored) {}

		return output;
	}

	public IChatComponent getGuildString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		UsernameUuidStruct uuidUsername = usernameUuid(getUsername(args, 0));
		if (uuidUsername.isNotValid()) {
			return getFailCause(uuidUsername);
		}

		HypixelResponse response = getGuildFromPlayer(uuidUsername.getUuid());
		if (response.isNotValid()) {
			return getFailCause(response);
		}

		return wrapText(
			empty()
				.appendSibling(
					new ChatText(
						getFormattedUsername(uuidUsername.getUuid()) + desc(" is in " + C.UNDERLINE + response.get("name").getAsString())
					)
						.setClickEvent(ClickEvent.Action.OPEN_URL, "https://plancke.io/hypixel/guild/player/" + uuidUsername.getUuid())
						.build()
				)
				.appendSibling(new ChatComponentText("\n" + getGuildInfo(response.response)))
		);
	}

	public String getGuildChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKeyChat();
		}

		if (args.length == 1) {
			UsernameUuidStruct uuidUsername = usernameUuid(args[0]);
			if (uuidUsername.isNotValid()) {
				return getFailCauseChat(uuidUsername);
			}

			HypixelResponse response = getGuildFromPlayer(uuidUsername.getUuid());
			if (response.isNotValid()) {
				return getFailCauseChat(response);
			}

			return (
				StringUtils.stripControlCodes(getFormattedUsername(uuidUsername.getUuid())) + " is in " + response.get("name").getAsString()
			);
		}

		return getUsageChat(INSTANCE);
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
		return "/" + getCommandName() + " [player]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getGuildString(args)));
	}
}
