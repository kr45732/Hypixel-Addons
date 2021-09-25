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

package com.kr45732.hypixeladdons.commands.dungeons;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class EssenceCommand extends CommandBase {

	public static EssenceCommand INSTANCE = new EssenceCommand();

	public static IChatComponent getEssenceString(String[] args) {
		args = String.join(" ", args).split(" ", 2);
		if (args.length != 2 || args[1].length() == 0) {
			return Utils.getUsage(INSTANCE);
		}

		if (args[0].equals("information") || args[0].equals("info")) {
			JsonElement essenceCostsJson = Utils.getEssenceCostsJson();
			String itemId = Utils.nameToId(args[1]);

			if (Utils.higherDepth(essenceCostsJson, itemId) == null) {
				String closestMatch = Utils.getClosestMatch(itemId, Constants.ESSENCE_ITEM_NAMES);
				itemId = closestMatch != null ? closestMatch : itemId;
			}

			JsonElement itemJson = Utils.higherDepth(essenceCostsJson, itemId);
			IChatComponent output = Utils
				.empty()
				.appendSibling(new ChatComponentText(Utils.labelWithDesc("Item", Utils.idToName(itemId)) + "\n"));
			String essenceType = Utils.higherDepth(itemJson, "type").getAsString().toLowerCase();
			for (String level : Utils.getJsonKeys(itemJson)) {
				switch (level) {
					case "type":
						output.appendText(Utils.labelWithDesc("Essence Type", Utils.capitalizeString(essenceType) + " essence") + "\n");
						break;
					case "dungeonize":
						output.appendText(
							"\n" +
							Utils.arrow() +
							Utils.labelWithDesc(
								"Dungeonize item",
								Utils.higherDepth(itemJson, level).getAsString() + " " + essenceType + " essence"
							)
						);
						break;
					default:
						output.appendText(
							"\n" +
							Utils.arrow() +
							Utils.labelWithDesc(
								level + (level.equals("1") ? " star" : " stars"),
								Utils.higherDepth(itemJson, level).getAsString() + " " + essenceType + " essence"
							)
						);
						break;
				}
			}

			return Utils.wrapText(output);
		}

		return Utils.getUsage(INSTANCE);
	}

	@Override
	public String getCommandName() {
		return "hpa:essence";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " information <item>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getEssenceString(args)));
	}
}
