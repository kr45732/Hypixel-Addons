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

package com.kr45732.hypixeladdons.commands.hypixel;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.ArenaMode.ALL;
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.ArenaMode.NONE;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class ArenaCommand extends CommandBase {

	public static final ArenaCommand INSTANCE = new ArenaCommand();

	public static IChatComponent getArenaString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = newHypixelPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player
			.defaultComponent()
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc(
					"Kills | Deaths",
					formatNumber(player.getArenaKills(ALL)) + " | " + formatNumber(player.getArenaDeaths(ALL))
				) +
				"\n" +
				arrow() +
				labelWithDesc("Wins | Losses", formatNumber(player.getArenaWins(ALL)) + " | " + formatNumber(player.getArenaLosses(ALL))) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(player.getArenaKills(ALL), player.getArenaDeaths(ALL)))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(player.getArenaWins(ALL), player.getArenaLosses(ALL)))) +
				"\n" +
				arrow() +
				labelWithDesc("Winstreak", formatNumber(player.getArenaWinstreak(ALL))) +
				"\n" +
				arrow() +
				labelWithDesc("Coins", formatNumber(player.getArenaCoins())) +
				"\n" +
				arrow() +
				labelWithDesc("Keys", formatNumber(player.getArenaInt("keys", NONE))) +
				"\n\n" +
				label("Modes")
			);

		for (HypixelPlayer.ArenaMode mode : HypixelPlayer.ArenaMode.getModes()) {
			output.appendSibling(
				new ChatText("\n" + arrow() + label(mode.getName()))
					.setHoverEvent(
						mode.getName() + " statistics",
						arrow() +
						labelWithDesc(
							"Kills | Deaths",
							formatNumber(player.getArenaKills(mode)) + " | " + formatNumber(player.getArenaDeaths(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Wins | Losses",
							formatNumber(player.getArenaWins(mode)) + " | " + formatNumber(player.getArenaLosses(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(player.getArenaKills(mode), player.getArenaDeaths(mode)))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(player.getArenaWins(mode), player.getArenaLosses(mode)))) +
						"\n" +
						arrow() +
						labelWithDesc("Winstreak", formatNumber(player.getArenaWinstreak(mode)))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:arena";
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
		executor.submit(() -> sender.addChatMessage(getArenaString(args)));
	}
}
