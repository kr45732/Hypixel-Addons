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

package com.kr45732.hypixeladdons.commands.miscellaneous;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class BankCommand extends CommandBase {

	public static final BankCommand INSTANCE = new BankCommand();

	public IChatComponent getBankString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		double bankBal = player.getBankBalance();
		double purseCoins = player.getPurseCoins();

		return wrapText(
			player
				.defaultComponent()
				.appendText(
					"\n\n" +
					labelWithDesc("Total coins", "" + simplifyNumber(Math.max(bankBal, 0) + purseCoins)) +
					"\n" +
					labelWithDesc("Bank balance", bankBal == -1 ? "Banking API disabled" : simplifyNumber(bankBal) + " coins") +
					"\n" +
					labelWithDesc("Purse coins", simplifyNumber(purseCoins) + " coins")
				)
		);
	}

	public String getBankChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKeyChat();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCauseChat(player);
		}

		double bankBal = player.getBankBalance();
		double purseCoins = player.getPurseCoins();

		return (
			player.getUsername() +
			" has " +
			simplifyNumber(Math.max(bankBal, 0) + purseCoins) +
			" coins (" +
			(bankBal == -1 ? "Bank API disabled" : simplifyNumber(bankBal) + " in bank") +
			" & " +
			simplifyNumber(purseCoins) +
			" in purse)"
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bank";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("hpa:coins", "hpa:purse");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [player] [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getBankString(args)));
	}
}
