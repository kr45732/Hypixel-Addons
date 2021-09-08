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
import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BitsCommand extends CommandBase {

	public static BitsCommand INSTANCE = new BitsCommand();

	public static ChatComponentText getBitsString(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return Utils.getUsage(INSTANCE);
		}

		String itemId = Utils.nameToId(args[0]);
		JsonElement bitsJson = Utils.getBitPricesJson();
		if (Utils.higherDepth(bitsJson, itemId) == null) {
			itemId = Utils.getClosestMatch(itemId, Constants.BITS_ITEM_NAMES);
		}

		return Utils.wrapText(
			Utils.labelWithDesc("Item", Utils.idToName(itemId)) +
			"\n" +
			Utils.labelWithDesc("Bits cost", Utils.formatNumber(Utils.higherDepth(bitsJson, itemId).getAsInt()))
		);
	}

	public static String getBitsChat(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return Utils.getUsageChat(INSTANCE);
		}

		String itemId = Utils.nameToId(args[0]);
		JsonElement bitsJson = Utils.getBitPricesJson();
		if (Utils.higherDepth(bitsJson, itemId) == null) {
			itemId = Utils.getClosestMatch(itemId, Constants.BITS_ITEM_NAMES);
		}

		return Utils.idToName(itemId) + " costs " + Utils.formatNumber(Utils.higherDepth(bitsJson, itemId).getAsInt()) + " bits";
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
		Utils.executor.submit(() -> sender.addChatMessage(getBitsString(args)));
	}
}
