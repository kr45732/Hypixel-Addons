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

package com.kr45732.hypixeladdons.commands.hypixel;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.DuelsMode.NONE;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class DuelsCommand extends CommandBase {

	public static DuelsCommand INSTANCE = new DuelsCommand();

	public static IChatComponent getDuelsString(String[] args) {
		if (args.length != 1) {
			return getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = new HypixelPlayer(args[0]);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player
			.defaultPlayerComponent()
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc("Overall division", player.getDuelsOverallDivision()) +
				"\n" +
				arrow() +
				labelWithDesc("Coins", formatNumber(player.getDuelsCoins())) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Kills | Deaths",
					formatNumber(player.getDuelsKills(NONE)) + " | " + formatNumber(player.getDuelsDeaths(NONE))
				) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Wins | Losses",
					formatNumber(player.getDuelsWins(NONE)) + " | " + formatNumber(player.getDuelsLosses(NONE))
				) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(player.getDuelsKills(NONE), player.getDuelsDeaths(NONE)))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(player.getDuelsWins(NONE), player.getDuelsLosses(NONE)))) +
				"\n" +
				arrow() +
				labelWithDesc("Current winstreak", formatNumber(player.getDuelsWinstreak(NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Best winstreak", formatNumber(player.getDuelsInt("best_overall_winstreak", NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Melee accuracy", roundAndFormat(player.getDuelsMeleeAccuracy(NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Arrow hit accuracy", roundAndFormat(player.getDuelsArrowAccuracy(NONE)))
			)
			.appendText("\n\n" + label("Modes"));

		for (HypixelPlayer.DuelsMode mode : HypixelPlayer.DuelsMode.getModes()) {
			output.appendSibling(
				new ChatText("\n" + arrow() + label(mode.getName()))
					.setHoverEvent(
						mode.getName(),
						arrow() +
						labelWithDesc(
							"Kills | Deaths",
							formatNumber(player.getDuelsKills(mode)) + " | " + formatNumber(player.getDuelsDeaths(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Wins | Losses",
							formatNumber(player.getDuelsWins(mode)) + " | " + formatNumber(player.getDuelsLosses(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(player.getDuelsKills(mode), player.getDuelsDeaths(mode)))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(player.getDuelsWins(mode), player.getDuelsLosses(mode)))) +
						"\n" +
						arrow() +
						labelWithDesc("Current winstreak", formatNumber(player.getDuelsWinstreak(mode))) +
						"\n" +
						arrow() +
						labelWithDesc("Melee accuracy", roundAndFormat(player.getDuelsMeleeAccuracy(mode))) +
						"\n" +
						arrow() +
						labelWithDesc("Arrow hit accuracy", roundAndFormat(player.getDuelsArrowAccuracy(mode)))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:duels";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getDuelsString(args)));
	}
}
