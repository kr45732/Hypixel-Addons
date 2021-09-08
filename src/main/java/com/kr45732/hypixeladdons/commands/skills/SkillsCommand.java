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

package com.kr45732.hypixeladdons.commands.skills;

import com.kr45732.hypixeladdons.utils.*;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SkillsCommand extends CommandBase {

	public static SkillsCommand INSTANCE = new SkillsCommand();

	public static IChatComponent getSkillsString(String[] args) {
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

		List<IChatComponent> skillComponents = new ArrayList<>();
		double trueSA = 0;
		double progressSA = 0;
		for (String skill : Constants.ALL_SKILL_NAMES) {
			SkillsStruct skillInfo = player.getSkill(skill);
			if (skillInfo != null) {
				skillComponents.add(
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

				if (!skill.equals("runecrafting") && !skill.equals("carpentry")) {
					trueSA += skillInfo.skillLevel;
					progressSA += skillInfo.getProgressLevel();
				}
			} else {
				skillComponents.add(new ChatComponentText("\n" + Utils.arrow() + Utils.labelWithDesc(Utils.capitalizeString(skill), "??")));
			}
		}
		trueSA /= Constants.SKILL_NAMES.size();
		progressSA /= Constants.SKILL_NAMES.size();

		IChatComponent output = Utils.empty().appendSibling(player.getLink());
		output.appendText(
			"\n\n" +
			Utils.labelWithDesc("True skill average", Utils.roundAndFormat(trueSA)) +
			"\n" +
			Utils.labelWithDesc("Progress skill average", Utils.roundAndFormat(progressSA) + "\n")
		);
		for (IChatComponent skillComponent : skillComponents) {
			output.appendSibling(skillComponent);
		}

		return Utils.wrapText(output);
	}

	public static String getSkillsChat(String[] args) {
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

		StringBuilder output = new StringBuilder();
		double progressSA = 0;
		for (String skill : Constants.SKILL_NAMES) {
			SkillsStruct skillInfo = player.getSkill(skill);
			if (skillInfo != null) {
				progressSA += skillInfo.getProgressLevel();
			} else {
				output.append(", ").append(Utils.capitalizeString(skill)).append(": ??");
			}
		}
		progressSA /= Constants.SKILL_NAMES.size();

		output.insert(0, player.getUsername() + " has a progress skill average of " + Utils.roundAndFormat(progressSA));

		return output.toString();
	}

	@Override
	public String getCommandName() {
		return "hpa:skills";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:skill");
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
		Utils.executor.submit(() -> sender.addChatMessage(getSkillsString(args)));
	}
}
