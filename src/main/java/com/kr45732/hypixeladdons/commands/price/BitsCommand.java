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
import com.kr45732.hypixeladdons.utils.Constants;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BitsCommand extends CommandBase {

	public static final BitsCommand INSTANCE = new BitsCommand();

	public ChatComponentText getBitsString(String[] args) {
		if (args.length == 0) {
			return getUsage(INSTANCE);
		}

		args = convertArgs(args, 1);

		String itemId = nameToId(args[0]);
		JsonElement bitsJson = getBitPricesJson();
		if (higherDepth(bitsJson, itemId) == null) {
			itemId = getClosestMatch(itemId, Constants.BITS_ITEM_NAMES);
		}

		return wrapText(
			labelWithDesc("Item", idToName(itemId)) +
			"\n" +
			labelWithDesc("Bits cost", formatNumber(higherDepth(bitsJson, itemId).getAsInt()))
		);
	}

	public String getBitsChat(String[] args) {
		if (args.length == 0) {
			return getUsageChat(INSTANCE);
		}
		args = convertArgs(args, 1);

		String itemId = nameToId(args[0]);
		JsonElement bitsJson = getBitPricesJson();
		if (higherDepth(bitsJson, itemId) == null) {
			itemId = getClosestMatch(itemId, Constants.BITS_ITEM_NAMES);
		}

		return idToName(itemId) + " costs " + formatNumber(higherDepth(bitsJson, itemId).getAsInt()) + " bits";
	}

	@Override
	public String getCommandName() {
		return "hpa:bits";
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
		executor.submit(() -> sender.addChatMessage(getBitsString(args)));
	}
}
