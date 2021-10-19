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

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class DungeonsCommand extends CommandBase {

	public static final DungeonsCommand INSTANCE = new DungeonsCommand();

	public IChatComponent getDungeonsString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		SkillsStruct skillInfo = player.getCatacombs();
		IChatComponent output = player
			.defaultComponent()
			.appendText(
				"\n\n" +
				labelWithDesc("True catacombs level", "" + skillInfo.getCurrentLevel()) +
				"\n" +
				labelWithDesc("Secrets", formatNumber(player.getDungeonSecrets()) + "\n")
			)
			.appendSibling(
				new ChatText(
					"\n" + arrow() + labelWithDesc(capitalizeString(skillInfo.getName()), roundAndFormat(skillInfo.getProgressLevel()))
				)
					.setHoverEvent(
						capitalizeString(skillInfo.getName()),
						labelWithDesc(
							"XP progress",
							simplifyNumber(skillInfo.getExpCurrent()) + " / " + simplifyNumber(skillInfo.getExpForNext())
						) +
						"\n" +
						labelWithDesc("Total XP", simplifyNumber(skillInfo.getTotalExp())) +
						"\n" +
						labelWithDesc("Progress", (skillInfo.isMaxed() ? "MAX" : roundProgress(skillInfo.getProgressToNext())))
					)
					.build()
			);

		for (String className : Constants.DUNGEON_CLASS_NAMES) {
			skillInfo = player.getDungeonClass(className);
			output.appendSibling(
				new ChatText("\n" + arrow() + labelWithDesc(capitalizeString(className), roundAndFormat(skillInfo.getProgressLevel())))
					.setHoverEvent(
						capitalizeString(className),
						labelWithDesc(
							"XP progress",
							simplifyNumber(skillInfo.getExpCurrent()) + " / " + simplifyNumber(skillInfo.getExpForNext())
						) +
						"\n" +
						labelWithDesc("Total XP", simplifyNumber(skillInfo.getTotalExp())) +
						"\n" +
						labelWithDesc("Progress", (skillInfo.isMaxed() ? "MAX" : roundProgress(skillInfo.getProgressToNext())))
					)
					.build()
			);
		}

		return wrapText(output);
	}

	public String getDungeonsChat(String[] args) {
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

		return (
			player.getUsername() +
			" is catacombs " +
			roundAndFormat(player.getCatacombs().getProgressLevel()) +
			" with " +
			formatNumber(player.getDungeonSecrets()) +
			"secrets and playing as a" +
			player.getSelectedDungeonClass()
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:dungeons";
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList("hpa:catacombs", "hpa:cata");
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
		executor.submit(() -> sender.addChatMessage(getDungeonsString(args)));
	}
}
