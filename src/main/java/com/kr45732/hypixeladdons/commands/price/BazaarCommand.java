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

package com.kr45732.hypixeladdons.commands.price;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import java.util.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BazaarCommand extends CommandBase {

	public static final BazaarCommand INSTANCE = new BazaarCommand();

	public ChatComponentText getBazaarString(String[] args) {
		if (args.length == 0) {
			return getUsage(INSTANCE);
		}
		args = convertArgs(args, 1);

		String itemId = nameToId(args[0]);
		JsonElement bazaarItems = getBazaarJson();
		if (higherDepth(bazaarItems, itemId) == null) {
			Map<String, String> itemNameToId = new HashMap<>();
			for (String itemIID : getJsonKeys(bazaarItems)) {
				String itemName;
				try {
					itemName = higherDepth(bazaarItems, itemIID + ".name").getAsString();
				} catch (Exception e) {
					itemName = capitalizeString(itemIID.replace("_", " "));
				}
				itemNameToId.put(itemName, itemIID);
			}

			itemId = itemNameToId.get(getClosestMatch(itemId, new ArrayList<>(itemNameToId.keySet())));
		}

		JsonElement itemInfo = higherDepth(bazaarItems, itemId);
		String output = labelWithDesc("Item", idToName(itemId));
		output +=
			"\n" +
			labelWithDesc(
				"Buy Price (Per)",
				simplifyNumber(higherDepth(higherDepth(itemInfo, "buy_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble())
			);
		output +=
			"\n" +
			labelWithDesc(
				"Sell Price (Per)",
				simplifyNumber(higherDepth(higherDepth(itemInfo, "sell_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble())
			);

		return wrapText(output);
	}

	public String getBazaarChat(String[] args) {
		if (args.length == 0) {
			return getUsageChat(INSTANCE);
		}
		args = convertArgs(args, 1);

		String itemId = nameToId(args[0]);
		JsonElement bazaarItems = getBazaarJson();
		if (higherDepth(bazaarItems, itemId) == null) {
			Map<String, String> itemNameToId = new HashMap<>();
			for (String itemIID : getJsonKeys(bazaarItems)) {
				String itemName;
				try {
					itemName = higherDepth(bazaarItems, itemIID + ".name").getAsString();
				} catch (Exception e) {
					itemName = capitalizeString(itemIID.replace("_", " "));
				}
				itemNameToId.put(itemName, itemIID);
			}

			itemId = itemNameToId.get(getClosestMatch(itemId, new ArrayList<>(itemNameToId.keySet())));
		}

		JsonElement itemInfo = higherDepth(bazaarItems, itemId);

		return (
			idToName(itemId) +
			" costs " +
			simplifyNumber(higherDepth(higherDepth(itemInfo, "buy_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble()) +
			" and sells for " +
			simplifyNumber(higherDepth(higherDepth(itemInfo, "sell_summary").getAsJsonArray().get(0), "pricePerUnit").getAsDouble())
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
		executor.submit(() -> sender.addChatMessage(getBazaarString(args)));
	}
}
