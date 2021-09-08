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

import static com.kr45732.hypixeladdons.utils.Constants.DUELS_GAME_ID_TO_NAME;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Map;
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

		IChatComponent output = empty()
			.appendSibling(player.getLink())
			.appendText(
				"\n\n" +
				label("Statistics") +
				"\n" +
				arrow() +
				labelWithDesc("Overall division", player.getOverallDivision()) +
				"\n" +
				arrow() +
				labelWithDesc("Coins", formatNumber(player.getDuelsStat("coins"))) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Kills | Deaths",
					formatNumber(player.getDuelsStat("kills")) + " | " + formatNumber(player.getDuelsStat("deaths"))
				) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Wins | Losses",
					formatNumber(player.getDuelsStat("wins")) + " | " + formatNumber(player.getDuelsStat("losses"))
				) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(divide(player.getDuelsStat("kills"), player.getDuelsStat("deaths")))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(divide(player.getDuelsStat("wins"), player.getDuelsStat("losses")))) +
				"\n" +
				arrow() +
				labelWithDesc("Current winstreak", formatNumber(player.getDuelsStat("current_winstreak"))) +
				"\n" +
				arrow() +
				labelWithDesc("Best winstreak", formatNumber(player.getDuelsStat("best_overall_winstreak"))) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Melee accuracy",
					roundAndFormat(divide(player.getDuelsStat("melee_hits"), player.getDuelsStat("melee_swings")))
				) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Arrow hit accuracy",
					roundAndFormat(divide(player.getDuelsStat("bow_hits"), player.getDuelsStat("bow_swings")))
				)
			)
			.appendText("\n\n" + label("Modes"));

		for (Map.Entry<String, String> mode : DUELS_GAME_ID_TO_NAME.entrySet()) {
			String modeId = mode.getKey();

			output.appendSibling(
				new ChatText("\n" + arrow() + label(mode.getValue()))
					.setHoverEvent(
						mode.getValue(),
						arrow() +
						labelWithDesc(
							"Kills | Deaths",
							formatNumber(player.getDuelsStat("kills", modeId)) + " | " + formatNumber(player.getDuelsStat("deaths", modeId))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Wins | Losses",
							formatNumber(player.getDuelsStat("wins", modeId)) + " | " + formatNumber(player.getDuelsStat("losses", modeId))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"K/D",
							roundAndFormat(divide(player.getDuelsStat("kills", modeId), player.getDuelsStat("deaths", modeId)))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"W/L",
							roundAndFormat(divide(player.getDuelsStat("wins", modeId), player.getDuelsStat("losses", modeId)))
						) +
						"\n" +
						arrow() +
						labelWithDesc("Current winstreak", formatNumber(player.getDuelsStat("current_winstreak", modeId))) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Melee accuracy",
							roundAndFormat(divide(player.getDuelsStat("melee_hits", modeId), player.getDuelsStat("melee_swings", modeId)))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Arrow hit accuracy",
							roundAndFormat(divide(player.getDuelsStat("bow_hits", modeId), player.getDuelsStat("bow_swings", modeId)))
						)
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
