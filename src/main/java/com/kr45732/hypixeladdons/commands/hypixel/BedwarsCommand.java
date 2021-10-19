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
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.BedwarsMode.NONE;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class BedwarsCommand extends CommandBase {

	public static final BedwarsCommand INSTANCE = new BedwarsCommand();

	public IChatComponent getBedwarsString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = newHypixelPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		int kills = player.getBedwarsKills(NONE);
		int deaths = player.getBedwarsDeaths(NONE);
		int wins = player.getBedwarsWins(NONE);
		int losses = player.getBedwarsLosses(NONE);
		int finalKills = player.getBedwarsFinalKills(NONE);
		int finalDeaths = player.getBedwarsFinalDeaths(NONE);

		IChatComponent output = player
			.defaultComponent()
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

		for (HypixelPlayer.BedwarsMode mode : HypixelPlayer.BedwarsMode.getModes()) {
			int modeKills = player.getBedwarsKills(mode);
			int modeDeaths = player.getBedwarsDeaths(mode);
			int modeWins = player.getBedwarsWins(mode);
			int modeLosses = player.getBedwarsLosses(mode);
			int modeFinalKills = player.getBedwarsFinalKills(mode);
			int modeFinalDeaths = player.getBedwarsFinalDeaths(mode);

			output.appendSibling(
				new ChatText("\n" + arrow() + label(capitalizeString(mode.getName())))
					.setHoverEvent(
						capitalizeString(mode.getName()) + " statistics",
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

	public String getBedwarsChat(String[] args) {
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

		int kills = player.getBedwarsKills(NONE);
		int deaths = player.getBedwarsDeaths(NONE);
		int wins = player.getBedwarsWins(NONE);
		int losses = player.getBedwarsLosses(NONE);
		int finalKills = player.getBedwarsFinalKills(NONE);
		int finalDeaths = player.getBedwarsFinalDeaths(NONE);

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
		return "/" + getCommandName() + " [player]";
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
