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

package com.kr45732.hypixeladdons.commands.dungeons;

import com.kr45732.hypixeladdons.utils.*;
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

	public static DungeonsCommand INSTANCE = new DungeonsCommand();

	public static IChatComponent getDungeonsString(String[] args) {
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
		SkillsStruct skillInfo = player.getCatacombsSkill();
		output
			.appendText(
				"\n\n" +
				Utils.labelWithDesc("True catacombs level", "" + skillInfo.skillLevel) +
				"\n" +
				Utils.labelWithDesc("Secrets", Utils.formatNumber(player.getDungeonSecrets()) + "\n")
			)
			.appendSibling(
				new ChatText(
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(Utils.capitalizeString(skillInfo.skillName), Utils.roundAndFormat(skillInfo.getProgressLevel()))
				)
					.setHoverEvent(
						Utils.capitalizeString(skillInfo.skillName),
						Utils.labelWithDesc(
							"XP progress",
							Utils.simplifyNumber(skillInfo.expCurrent) + " / " + Utils.simplifyNumber(skillInfo.expForNext)
						) +
						"\n" +
						Utils.labelWithDesc("Total XP", Utils.simplifyNumber(skillInfo.totalSkillExp)) +
						"\n" +
						Utils.labelWithDesc(
							"Progress",
							(skillInfo.skillLevel == skillInfo.maxSkillLevel ? "MAX" : Utils.roundProgress(skillInfo.progressToNext))
						)
					)
					.build()
			);

		for (String className : Constants.DUNGEON_CLASS_NAMES) {
			skillInfo = player.getDungeonClass(className);
			output.appendSibling(
				new ChatText(
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(Utils.capitalizeString(className), Utils.roundAndFormat(skillInfo.getProgressLevel()))
				)
					.setHoverEvent(
						Utils.capitalizeString(className),
						Utils.labelWithDesc(
							"XP progress",
							Utils.simplifyNumber(skillInfo.expCurrent) + " / " + Utils.simplifyNumber(skillInfo.expForNext)
						) +
						"\n" +
						Utils.labelWithDesc("Total XP", Utils.simplifyNumber(skillInfo.totalSkillExp)) +
						"\n" +
						Utils.labelWithDesc(
							"Progress",
							(skillInfo.skillLevel == skillInfo.maxSkillLevel ? "MAX" : Utils.roundProgress(skillInfo.progressToNext))
						)
					)
					.build()
			);
		}

		return Utils.wrapText(output);
	}

	public static String getDungeonsChat(String[] args) {
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

		return (
			player.getUsername() +
			" is catacombs " +
			Utils.roundAndFormat(player.getCatacombsSkill().getProgressLevel()) +
			" with " +
			Utils.formatNumber(player.getDungeonSecrets()) +
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
		return "/" + getCommandName() + " <player> [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getDungeonsString(args)));
	}
}
