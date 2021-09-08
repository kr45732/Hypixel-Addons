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

package com.kr45732.hypixeladdons.commands.price;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.Utils;
import java.util.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BazaarCommand extends CommandBase {

	public static BazaarCommand INSTANCE = new BazaarCommand();

	public static ChatComponentText getBazaarString(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return Utils.getUsage(INSTANCE);
		}

		String itemId = Utils.nameToId(args[0]);
		JsonElement bazaarItems = Utils.getBazaarJson();
		if (Utils.higherDepth(bazaarItems, itemId) == null) {
			Map<String, String> itemNameToId = new HashMap<>();
			for (String itemIID : Utils.getJsonKeys(bazaarItems)) {
				String itemName;
				try {
					itemName = Utils.higherDepth(bazaarItems, itemIID + ".name").getAsString();
				} catch (Exception e) {
					itemName = Utils.capitalizeString(itemIID.replace("_", " "));
				}
				itemNameToId.put(itemName, itemIID);
			}

			itemId = itemNameToId.get(Utils.getClosestMatch(itemId, new ArrayList<>(itemNameToId.keySet())));
		}

		JsonElement itemInfo = Utils.higherDepth(bazaarItems, itemId);
		String output = Utils.labelWithDesc("Item", Utils.idToName(itemId));
		output +=
			"\n" +
			Utils.labelWithDesc(
				"Buy Price (Per)",
				Utils.simplifyNumber(
					Utils.higherDepth(Utils.higherDepth(itemInfo, "buy_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble()
				)
			);
		output +=
			"\n" +
			Utils.labelWithDesc(
				"Sell Price (Per)",
				Utils.simplifyNumber(
					Utils.higherDepth(Utils.higherDepth(itemInfo, "sell_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble()
				)
			);

		return Utils.wrapText(output);
	}

	public static String getBazaarChat(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return Utils.getUsageChat(INSTANCE);
		}

		String itemId = Utils.nameToId(args[0]);
		JsonElement bazaarItems = Utils.getBazaarJson();
		if (Utils.higherDepth(bazaarItems, itemId) == null) {
			Map<String, String> itemNameToId = new HashMap<>();
			for (String itemIID : Utils.getJsonKeys(bazaarItems)) {
				String itemName;
				try {
					itemName = Utils.higherDepth(bazaarItems, itemIID + ".name").getAsString();
				} catch (Exception e) {
					itemName = Utils.capitalizeString(itemIID.replace("_", " "));
				}
				itemNameToId.put(itemName, itemIID);
			}

			itemId = itemNameToId.get(Utils.getClosestMatch(itemId, new ArrayList<>(itemNameToId.keySet())));
		}

		JsonElement itemInfo = Utils.higherDepth(bazaarItems, itemId);

		return (
			Utils.idToName(itemId) +
			" costs " +
			Utils.simplifyNumber(
				Utils.higherDepth(Utils.higherDepth(itemInfo, "buy_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble()
			) +
			" and sells for " +
			Utils.simplifyNumber(
				Utils.higherDepth(Utils.higherDepth(itemInfo, "sell_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble()
			)
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bazaar";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:bz");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <item>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getBazaarString(args)));
	}
}
