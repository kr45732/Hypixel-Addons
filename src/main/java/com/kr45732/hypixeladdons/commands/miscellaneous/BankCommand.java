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

package com.kr45732.hypixeladdons.commands.miscellaneous;

import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class BankCommand extends CommandBase {

	public static BankCommand INSTANCE = new BankCommand();

	public static IChatComponent getBankString(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKey();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCause(player);
		}

		double bankBal = player.getBankBalance();
		double purseCoins = player.getPurseCoins();

		return Utils.wrapText(
			Utils
				.empty()
				.appendSibling(player.getLink())
				.appendText(
					"\n\n" +
					Utils.labelWithDesc("Total coins", "" + Utils.simplifyNumber(Math.max(bankBal, 0) + purseCoins)) +
					"\n" +
					Utils.labelWithDesc("Bank balance", bankBal == -1 ? "Banking API disabled" : Utils.simplifyNumber(bankBal) + " coins") +
					"\n" +
					Utils.labelWithDesc("Purse coins", Utils.simplifyNumber(purseCoins) + " coins")
				)
		);
	}

	public static String getBankChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKeyChat();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCauseChat(player);
		}

		double bankBal = player.getBankBalance();
		double purseCoins = player.getPurseCoins();

		return (
			player.getUsername() +
			" has " +
			Utils.simplifyNumber(Math.max(bankBal, 0) + purseCoins) +
			" coins (" +
			(bankBal == -1 ? "Bank API disabled" : Utils.simplifyNumber(bankBal) + " in bank") +
			" & " +
			Utils.simplifyNumber(purseCoins) +
			" in purse)"
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bank";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:coins");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player> [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getBankString(args)));
	}
}
