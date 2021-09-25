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
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.SkywarsMode.NONE;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class SkywarsCommand extends CommandBase {

	public static SkywarsCommand INSTANCE = new SkywarsCommand();

	public static IChatComponent getSkywarsString(String[] args) {
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

		int kills = player.getSkywarsKills(NONE);
		int deaths = player.getSkywarsDeaths(NONE);
		int wins = player.getSkywarsWins(NONE);
		int losses = player.getSkywarsLosses(NONE);

		IChatComponent output = player
			.defaultPlayerComponent()
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc("Hypixel level", roundAndFormat(player.getHypixelLevel())) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Skywars level",
					player.getSkywarsLevelFormatted() + " | " + player.getSkywarsPrestige().toLowerCase() + " prestige"
				) +
				"\n" +
				arrow() +
				labelWithDesc("Kills | Deaths", formatNumber(kills) + " | " + formatNumber(deaths)) +
				"\n" +
				arrow() +
				labelWithDesc("Wins | Losses", formatNumber(wins) + " | " + formatNumber(losses)) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(kills, deaths))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(wins, losses))) +
				"\n" +
				arrow() +
				labelWithDesc("Heads", formatNumber(player.getSkywarsInt("heads", NONE)))
			);

		output.appendText("\n\n" + label("Modes"));

		for (HypixelPlayer.SkywarsMode mode : HypixelPlayer.SkywarsMode.getModes()) {
			int modeKills = player.getSkywarsKills(mode);
			int modeDeaths = player.getSkywarsDeaths(mode);
			int modeWins = player.getSkywarsWins(mode);
			int modeLosses = player.getSkywarsLosses(mode);

			output.appendSibling(
				new ChatText("\n" + arrow() + label(capitalizeString(mode.getName())))
					.setHoverEvent(
						mode.getName() + " statistics",
						arrow() +
						labelWithDesc("Kills | Deaths", formatNumber(modeKills) + " | " + formatNumber(modeDeaths)) +
						"\n" +
						arrow() +
						labelWithDesc("Wins | Losses", formatNumber(modeWins) + " | " + formatNumber(modeLosses)) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(divide(modeKills, modeDeaths))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(divide(modeWins, modeLosses)))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	public static String getSkywarsChat(String[] args) {
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

		int kills = player.getSkywarsKills(NONE);
		int deaths = player.getSkywarsDeaths(NONE);
		int wins = player.getSkywarsWins(NONE);
		int losses = player.getSkywarsLosses(NONE);

		return (
			player.getStrippedFormattedUsername() +
			", Skywars level: " +
			player.getSkywarsLevelFormatted() +
			", K/D: " +
			roundAndFormat(divide(kills, deaths)) +
			", W/L: " +
			roundAndFormat(divide(wins, losses))
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:skywars";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:sw");
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
		executor.submit(() -> sender.addChatMessage(getSkywarsString(args)));
	}
}
