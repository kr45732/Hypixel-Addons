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
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.BuildBattleMode.NONE;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class BuildBattleCommand extends CommandBase {

	public static BuildBattleCommand INSTANCE = new BuildBattleCommand();

	public static IChatComponent getMurderMysteryString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = new HypixelPlayer(getUsername(args, 0));
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
				labelWithDesc("Coins", roundAndFormat(player.getBuildBattleCoins())) +
				"\n" +
				arrow() +
				labelWithDesc("Score", formatNumber(player.getBuildBattleInt("score", NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Correct guesses", formatNumber(player.getBuildBattleInt("correct_guesses", NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Super votes", formatNumber(player.getBuildBattleInt("super_votes", NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("Wins", formatNumber(player.getBuildBattleWins(NONE))) +
				"\n\n" +
				label("Modes")
			);

		for (HypixelPlayer.BuildBattleMode mode : HypixelPlayer.BuildBattleMode.getModes()) {
			output.appendText("\n" + arrow() + labelWithDesc(mode.getName(), formatNumber(player.getBuildBattleWins(mode)) + " wins"));
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:build_battle";
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
		executor.submit(() -> sender.addChatMessage(getMurderMysteryString(args)));
	}
}
