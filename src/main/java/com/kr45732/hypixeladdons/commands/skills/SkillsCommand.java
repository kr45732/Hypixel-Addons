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

package com.kr45732.hypixeladdons.commands.skills;

import static com.kr45732.hypixeladdons.utils.Utils.*;

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

	public static final SkillsCommand INSTANCE = new SkillsCommand();

	public IChatComponent getSkillsString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		List<IChatComponent> skillComponents = new ArrayList<>();
		double trueSA = 0;
		double progressSA = 0;
		for (String skill : Constants.ALL_SKILL_NAMES) {
			SkillsStruct skillInfo = player.getSkill(skill);
			if (skillInfo != null) {
				skillComponents.add(
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

				if (!skill.equals("runecrafting") && !skill.equals("carpentry")) {
					trueSA += skillInfo.getCurrentLevel();
					progressSA += skillInfo.getProgressLevel();
				}
			} else {
				skillComponents.add(new ChatComponentText("\n" + arrow() + labelWithDesc(capitalizeString(skill), "??")));
			}
		}
		trueSA /= Constants.SKILL_NAMES.size();
		progressSA /= Constants.SKILL_NAMES.size();

		IChatComponent output = player
			.defaultComponent()
			.appendText(
				"\n\n" +
				labelWithDesc("True skill average", roundAndFormat(trueSA)) +
				"\n" +
				labelWithDesc("Progress skill average", roundAndFormat(progressSA) + "\n")
			);
		for (IChatComponent skillComponent : skillComponents) {
			output.appendSibling(skillComponent);
		}

		return wrapText(output);
	}

	public String getSkillsChat(String[] args) {
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

		StringBuilder output = new StringBuilder();
		double progressSA = 0;
		for (String skill : Constants.SKILL_NAMES) {
			SkillsStruct skillInfo = player.getSkill(skill);
			if (skillInfo != null) {
				progressSA += skillInfo.getProgressLevel();
			} else {
				output.append(", ").append(capitalizeString(skill)).append(": ??");
			}
		}
		progressSA /= Constants.SKILL_NAMES.size();

		output.insert(0, player.getUsername() + " has a progress skill average of " + roundAndFormat(progressSA));

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
		return "/" + getCommandName() + " [player] [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getSkillsString(args)));
	}
}
