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

import static com.kr45732.hypixeladdons.utils.Constants.BEDWARS_GAME_ID_TO_NAME;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class BedwarsCommand extends CommandBase {

	public static BedwarsCommand INSTANCE = new BedwarsCommand();

	public static IChatComponent getBedwarsString(String[] args) {
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

		int kills = player.getBedwarsStatistic("kills");
		int deaths = player.getBedwarsStatistic("deaths");
		int wins = player.getBedwarsStatistic("wins");
		int losses = player.getBedwarsStatistic("losses");
		int finalKills = player.getBedwarsStatistic("final_kills");
		int finalDeaths = player.getBedwarsStatistic("final_deaths");

		IChatComponent output = empty()
			.appendSibling(player.getLink())
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc("Hypixel level", roundAndFormat(player.getHypixelLevel())) +
				"\n" +
				arrow() +
				labelWithDesc("Bedwars level", roundAndFormat(player.getBedwarsLevel())) +
				"\n" +
				arrow() +
				labelWithDesc("Kills | Deaths", formatNumber(kills) + " | " + formatNumber(deaths)) +
				"\n" +
				arrow() +
				labelWithDesc("Wins | Losses", formatNumber(wins) + " | " + formatNumber(losses)) +
				"\n" +
				arrow() +
				labelWithDesc("Final Kills | Final Deaths", formatNumber(finalKills) + " | " + formatNumber(finalDeaths)) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(kills, deaths))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(wins, losses))) +
				"\n" +
				arrow() +
				labelWithDesc("Final K/D", roundAndFormat(divide(finalKills, finalDeaths))) +
				"\n\n" +
				label("Modes")
			);

		for (Map.Entry<String, String> mode : BEDWARS_GAME_ID_TO_NAME.entrySet()) {
			int modeKills = player.getBedwarsStatistic("kills", mode.getKey());
			int modeDeaths = player.getBedwarsStatistic("deaths", mode.getKey());
			int modeWins = player.getBedwarsStatistic("wins", mode.getKey());
			int modeLosses = player.getBedwarsStatistic("losses", mode.getKey());
			int modeFinalKills = player.getBedwarsStatistic("final_kills", mode.getKey());
			int modeFinalDeaths = player.getBedwarsStatistic("final_deaths", mode.getKey());

			output.appendSibling(
				new ChatText("\n" + arrow() + label(capitalizeString(mode.getValue())))
					.setHoverEvent(
						capitalizeString(mode.getValue()) + " statistics",
						arrow() +
						labelWithDesc("Kills | Deaths", formatNumber(modeKills) + " | " + formatNumber(modeDeaths)) +
						"\n" +
						arrow() +
						labelWithDesc("Wins | Losses", formatNumber(modeWins) + " | " + formatNumber(modeLosses)) +
						"\n" +
						arrow() +
						labelWithDesc("Final Kills | Final Deaths", formatNumber(modeFinalKills) + " | " + formatNumber(modeFinalDeaths)) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(modeKills, modeDeaths))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(modeWins, modeLosses))) +
						"\n" +
						arrow() +
						labelWithDesc("Final K/D", roundAndFormat(divide(modeFinalKills, modeFinalDeaths)))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	public static String getBedwarsChat(String[] args) {
		if (args.length != 1) {
			return getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKeyChat();
		}

		HypixelPlayer player = new HypixelPlayer(args[0]);
		if (!player.isValid()) {
			return getFailCauseChat(player);
		}

		int kills = player.getBedwarsStatistic("kills");
		int deaths = player.getBedwarsStatistic("deaths");
		int wins = player.getBedwarsStatistic("wins");
		int losses = player.getBedwarsStatistic("losses");
		int finalKills = player.getBedwarsStatistic("final_kills");
		int finalDeaths = player.getBedwarsStatistic("final_deaths");

		return (
			player.getStrippedFormattedUsername() +
			", Bedwars level: " +
			roundAndFormat(player.getBedwarsLevel()) +
			", K÷D: " +
			roundAndFormat(divide(kills, deaths)) +
			", W÷L: " +
			roundAndFormat(divide(wins, losses)) +
			", FKDR: " +
			roundAndFormat(divide(finalKills, finalDeaths))
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bedwars";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:bw");
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
		executor.submit(() -> sender.addChatMessage(getBedwarsString(args)));
	}
}
