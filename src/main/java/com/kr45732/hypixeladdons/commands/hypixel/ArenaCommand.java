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

package com.kr45732.hypixeladdons.commands.hypixel;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class ArenaCommand extends CommandBase {

	public static ArenaCommand INSTANCE = new ArenaCommand();

	public static IChatComponent getArenaString(String[] args) {
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

		int kills2v2 = player.getArenaStat("kills_2v2");
		int deaths2v2 = player.getArenaStat("deaths_2v2");
		int wins2v2 = player.getArenaStat("wins_2v2");
		int losses2v2 = player.getArenaStat("losses_2v2");
		int winstreak2v2 = player.getArenaStat("win_streaks_2v2");

		int kills4v4 = player.getArenaStat("kills_4v4");
		int deaths4v4 = player.getArenaStat("deaths_4v4");
		int wins4v4 = player.getArenaStat("wins_4v4");
		int losses4v4 = player.getArenaStat("losses_4v4");
		int winstreak4v4 = player.getArenaStat("win_streaks_4v4");

		int totalKills = kills2v2 + kills4v4;
		int totalDeaths = deaths2v2 + deaths4v4;
		int totalWins = wins2v2 + wins4v4;
		int totalLosses = losses2v2 + losses4v4;
		int totalWinstreak = winstreak2v2 + winstreak4v4;

		IChatComponent output = empty()
			.appendSibling(player.getLink())
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc("Kills | Deaths", formatNumber(totalKills) + " | " + formatNumber(totalDeaths)) +
				"\n" +
				arrow() +
				labelWithDesc("Wins | Losses", formatNumber(totalWins) + " | " + formatNumber(totalLosses)) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(totalKills, totalDeaths))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(totalWins, totalLosses))) +
				"\n" +
				arrow() +
				labelWithDesc("Winstreak", formatNumber(totalWinstreak)) +
				"\n" +
				arrow() +
				labelWithDesc("Coins", formatNumber(player.getArenaStat("coins"))) +
				"\n" +
				arrow() +
				labelWithDesc("Keys", formatNumber(player.getArenaStat("keys"))) +
				"\n\n" +
				label("Modes")
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("2v2"))
					.setHoverEvent(
						"2v2 statistics",
						arrow() +
						labelWithDesc("Kills | Deaths", formatNumber(kills2v2) + " | " + formatNumber(deaths2v2)) +
						"\n" +
						arrow() +
						labelWithDesc("Wins | Losses", formatNumber(wins2v2) + " | " + formatNumber(losses2v2)) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(kills2v2, deaths2v2))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(wins2v2, losses2v2))) +
						"\n" +
						arrow() +
						labelWithDesc("Winstreak", formatNumber(wins2v2))
					)
					.build()
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("4v4"))
					.setHoverEvent(
						"4v4 statistics",
						arrow() +
						labelWithDesc("Kills | Deaths", formatNumber(kills4v4) + " | " + formatNumber(deaths4v4)) +
						"\n" +
						arrow() +
						labelWithDesc("Wins | Losses", formatNumber(wins4v4) + " | " + formatNumber(losses4v4)) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(kills4v4, deaths4v4))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(wins4v4, losses4v4))) +
						"\n" +
						arrow() +
						labelWithDesc("Winstreak", formatNumber(wins4v4))
					)
					.build()
			);

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:arena";
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
		executor.submit(() -> sender.addChatMessage(getArenaString(args)));
	}
}
