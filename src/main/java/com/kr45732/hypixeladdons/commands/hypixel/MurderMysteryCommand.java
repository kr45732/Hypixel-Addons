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

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class MurderMysteryCommand extends CommandBase {

	public static final MurderMysteryCommand INSTANCE = new MurderMysteryCommand();

	public IChatComponent getMurderMysteryString(String[] args) {
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
				labelWithDesc("Coins", roundAndFormat(player.getMurderMysteryCoins())) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Kills | Deaths",
					roundAndFormat(player.getMurderMysteryKills(HypixelPlayer.MurderMysteryMode.NONE)) +
					" | " +
					roundAndFormat(player.getMurderMysteryDeaths(HypixelPlayer.MurderMysteryMode.NONE))
				) +
				"\n" +
				arrow() +
				labelWithDesc(
					"Wins | Losses",
					roundAndFormat(player.getMurderMysteryWins(HypixelPlayer.MurderMysteryMode.NONE)) +
					" | " +
					roundAndFormat(player.getMurderMysteryLosses(HypixelPlayer.MurderMysteryMode.NONE))
				) +
				"\n" +
				arrow() +
				labelWithDesc("K/D", roundAndFormat(player.getMurderMysteryKDR(HypixelPlayer.MurderMysteryMode.NONE))) +
				"\n" +
				arrow() +
				labelWithDesc("W/L", roundAndFormat(player.getMurderMysteryWLR(HypixelPlayer.MurderMysteryMode.NONE))) +
				"\n\n" +
				label("Modes")
			);

		for (HypixelPlayer.MurderMysteryMode mode : HypixelPlayer.MurderMysteryMode.getModes()) {
			output.appendSibling(
				new ChatText("\n" + arrow() + label(capitalizeString(mode.getName())))
					.setHoverEvent(
						capitalizeString(mode.getName()) + " statistics",
						arrow() +
						labelWithDesc(
							"Kills | Deaths",
							formatNumber(player.getMurderMysteryKills(mode)) + " | " + formatNumber(player.getMurderMysteryDeaths(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc(
							"Wins | Losses",
							formatNumber(player.getMurderMysteryWins(mode)) + " | " + formatNumber(player.getMurderMysteryLosses(mode))
						) +
						"\n" +
						arrow() +
						labelWithDesc("K/D", roundAndFormat(player.getMurderMysteryKDR(mode))) +
						"\n" +
						arrow() +
						labelWithDesc("W/L", roundAndFormat(player.getMurderMysteryWLR(mode)))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:murder_mystery";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:murder");
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
