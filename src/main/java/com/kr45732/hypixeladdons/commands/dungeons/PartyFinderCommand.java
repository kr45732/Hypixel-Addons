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

package com.kr45732.hypixeladdons.commands.dungeons;

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.kr45732.hypixeladdons.utils.Utils.*;

public class PartyFinderCommand extends CommandBase {

	public static final PartyFinderCommand INSTANCE = new PartyFinderCommand();

	public static IChatComponent getPartyFinderString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player.defaultComponent();

		Set<String> playerMetaItems = player.getItemsPlayerHas(Constants.DUNGEON_META_ITEMS);
		output.appendText(
			"\n\n" +
			labelWithDesc("Catacombs Level", roundAndFormat(player.getCatacombs().getProgressLevel())) +
			"\n" +
			labelWithDesc("Secrets", formatNumber(player.getDungeonSecrets())) +
			"\n" +
			labelWithDesc("Selected Class", player.getSelectedDungeonClass()) +
			"\n" +
			labelWithDesc("Fastest F7 S+", player.getFastestF7Time()) +
			"\n" +
			labelWithDesc(
				"Meta items player has",
				(
					playerMetaItems != null
						? (playerMetaItems.size() > 0 ? String.join(", ", playerMetaItems) : "None")
						: "Inventory API disabled"
				)
			)
		);

		return wrapText(output);
	}

	public static String getPartyFinderChat(String[] args) {
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

		Set<String> playerMetaItems = player.getItemsPlayerHas(Constants.DUNGEON_META_ITEMS);

		return (
			player.getUsername() +
			", is cata " +
			roundAndFormat(player.getCatacombs().getProgressLevel()) +
			" " +
			player.getSelectedDungeonClass() +
			" with " +
			formatNumber(player.getDungeonSecrets()) +
			" secrets and has: " +
			(
				playerMetaItems != null
					? (playerMetaItems.size() > 0 ? String.join(", ", playerMetaItems) : "no meta items")
					: "Inventory API disabled"
			)
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:partyfinder";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:pf");
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
		executor.submit(() -> sender.addChatMessage(getPartyFinderString(args)));
	}
}
