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

package com.kr45732.hypixeladdons.commands.dungeons;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.api.Player;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class EssenceCommand extends CommandBase {

	public static final EssenceCommand INSTANCE = new EssenceCommand();

	public static IChatComponent getEssenceString(String[] args) {
		if (args.length < 1) {
			return getUsage(INSTANCE);
		}

		if (args[0].equals("information") || args[0].equals("info")) {
			if (args.length < 2) {
				return getUsage(INSTANCE);
			}
			args = convertArgs(args, 2);

			JsonElement essenceCostsJson = getEssenceCostsJson();
			String itemId = nameToId(args[1]);

			if (higherDepth(essenceCostsJson, itemId) == null) {
				String closestMatch = getClosestMatch(itemId, Constants.ESSENCE_ITEM_NAMES);
				itemId = closestMatch != null ? closestMatch : itemId;
			}

			JsonObject itemJson = higherDepth(essenceCostsJson, itemId).getAsJsonObject();
			IChatComponent output = empty().appendSibling(new ChatComponentText(labelWithDesc("Item", idToName(itemId)) + "\n"));
			String essenceType = higherDepth(itemJson, "type").getAsString().toLowerCase();
			for (Map.Entry<String, JsonElement> entry : itemJson.entrySet()) {
				switch (entry.getKey()) {
					case "type":
						output.appendText(labelWithDesc("Essence Type", capitalizeString(essenceType) + " essence"));
						break;
					case "dungeonize":
						output.appendText(
							"\n" +
							arrow() +
							labelWithDesc("Dungeonize item", entry.getValue().getAsString() + " " + essenceType + " essence")
						);
						break;
					default:
						output.appendText(
							"\n" +
							arrow() +
							labelWithDesc(
								entry.getKey() + (entry.getKey().equals("1") ? " star" : " stars"),
								entry.getValue().getAsString() + " " + essenceType + " essence"
							)
						);
						break;
				}
			}

			return wrapText(output);
		} else if (args[0].equals("player")) {
			Player player = newPlayer(args, 1);
			if (!player.isValid()) {
				return getFailCause(player);
			}

			IChatComponent output = player.defaultComponent();
			output.appendText("\n\n" + label("Essence Amounts") + "\n");
			for (Map.Entry<String, JsonElement> entry : player.profileJson().getAsJsonObject().entrySet()) {
				if (entry.getKey().startsWith("essence_")) {
					output.appendText(
						arrow() +
						labelWithDesc(
							capitalizeString(entry.getKey().split("essence_")[1]) + " essence",
							formatNumber(entry.getValue().getAsInt()) + "\n"
						)
					);
				}
			}

			output.appendText("\n" + label("Dungeon Upgrades"));
			for (Map.Entry<String, JsonElement> perk : higherDepth(player.profileJson(), "perks").getAsJsonObject().entrySet()) {
				output.appendText(
					"\n" + arrow() + labelWithDesc(capitalizeString(perk.getKey().replace("_", " ")), "" + perk.getValue().getAsInt())
				);
			}

			return output;
		}

		return getUsage(INSTANCE);
	}

	@Override
	public String getCommandName() {
		return "hpa:essence";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " information <item>\n/" + getCommandName() + "/essence player [player] [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getEssenceString(args)));
	}
}
