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

package com.kr45732.hypixeladdons.commands.dungeons;

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class PartyFinderCommand extends CommandBase {

	public static PartyFinderCommand INSTANCE = new PartyFinderCommand();

	public static IChatComponent getPartyFinderString(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKey();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCause(player);
		}

		IChatComponent output = Utils.empty().appendSibling(player.getLink());

		Set<String> playerMetaItems = player.getItemsPlayerHas(Constants.META_ITEMS);
		output.appendText(
			"\n\n" +
			Utils.labelWithDesc("Catacombs Level", Utils.roundAndFormat(player.getCatacombsLevel())) +
			"\n" +
			Utils.labelWithDesc("Secrets", Utils.formatNumber(player.getDungeonSecrets())) +
			"\n" +
			Utils.labelWithDesc("Selected Class", player.getSelectedDungeonClass()) +
			"\n" +
			Utils.labelWithDesc("Fastest F7 S+", player.getFastestF7Time()) +
			"\n" +
			Utils.labelWithDesc(
				"Meta Items player has",
				(
					playerMetaItems != null
						? (playerMetaItems.size() > 0 ? String.join(", ", playerMetaItems) : "None")
						: "Inventory API disabled"
				)
			)
		);

		return Utils.wrapText(output);
	}

	public static String getPartyFinderChat(String[] args) {
		if (args.length != 1 && args.length != 2) {
			return Utils.getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKeyChat();
		}

		Player player = args.length == 1 ? new Player(args[0]) : new Player(args[1], args[1]);
		if (!player.isValid()) {
			return Utils.getFailCauseChat(player);
		}

		Set<String> playerMetaItems = player.getItemsPlayerHas(Constants.META_ITEMS);

		return (
			player.getUsername() +
			", is cata " +
			Utils.roundAndFormat(player.getCatacombsLevel()) +
			" " +
			player.getSelectedDungeonClass() +
			" with " +
			Utils.formatNumber(player.getDungeonSecrets()) +
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
		return "/" + getCommandName() + " <player> [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getPartyFinderString(args)));
	}
}
