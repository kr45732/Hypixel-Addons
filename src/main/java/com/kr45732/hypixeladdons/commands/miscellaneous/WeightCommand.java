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

package com.kr45732.hypixeladdons.commands.miscellaneous;

import static com.kr45732.hypixeladdons.utils.Constants.SKILL_NAMES;
import static com.kr45732.hypixeladdons.utils.Constants.SLAYER_NAMES;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.weight.senither.Weight;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class WeightCommand extends CommandBase {

	public static final WeightCommand INSTANCE = new WeightCommand();

	public IChatComponent getWeightString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		Weight weight = new Weight(player);
		StringBuilder slayerStr = new StringBuilder();
		for (String slayerName : Constants.SLAYER_NAMES_MAP.keySet()) {
			slayerStr
				.append("\n")
				.append(arrow())
				.append(labelWithDesc(capitalizeString(slayerName), weight.getSlayerWeight().getSlayerWeight(slayerName).getFormatted()));
		}
		StringBuilder skillsStr = new StringBuilder();
		for (String skillName : SKILL_NAMES) {
			skillsStr
				.append("\n")
				.append(arrow())
				.append(labelWithDesc(capitalizeString(skillName), weight.getSkillsWeight().getSkillsWeight(skillName).getFormatted()));
		}
		StringBuilder dungeonsStr = new StringBuilder();
		dungeonsStr
			.append("\n")
			.append(arrow())
			.append(labelWithDesc("Catacombs", weight.getDungeonsWeight().getDungeonWeight("catacombs").getFormatted()));
		for (String dungeonClassName : Constants.DUNGEON_CLASS_NAMES) {
			dungeonsStr
				.append("\n")
				.append(arrow())
				.append(
					labelWithDesc(
						capitalizeString(dungeonClassName),
						weight.getDungeonsWeight().getClassWeight(dungeonClassName).getFormatted()
					)
				);
		}

		com.kr45732.hypixeladdons.utils.weight.lily.Weight lilyWeight = new com.kr45732.hypixeladdons.utils.weight.lily.Weight(player);
		StringBuilder lilySlayerStr = new StringBuilder();
		for (String slayerName : SLAYER_NAMES) {
			lilySlayerStr
				.append(capitalizeString(slayerName))
				.append(": ")
				.append(lilyWeight.getSlayerWeight().getSlayerWeight(slayerName).getFormatted())
				.append("\n");
		}
		StringBuilder lilySkillsStr = new StringBuilder();
		for (String skillName : SKILL_NAMES) {
			lilySkillsStr
				.append(capitalizeString(skillName))
				.append(": ")
				.append(lilyWeight.getSkillsWeight().getSkillsWeight(skillName).getFormatted())
				.append("\n");
		}
		String lilyDungeonsStr =
			"Catacombs: " +
			lilyWeight.getDungeonsWeight().getDungeonWeight().getFormatted() +
			"\n" +
			"Normal floor completions: " +
			lilyWeight.getDungeonsWeight().getDungeonCompletionWeight("normal").getFormatted() +
			"\n" +
			"Master floor completions: " +
			lilyWeight.getDungeonsWeight().getDungeonCompletionWeight("master").getFormatted() +
			"\n";

		IChatComponent output = player
			.defaultComponent()
			.appendText("\n\n" + label(labelWithDesc("Senither Weight", weight.getTotalWeight().getFormatted())))
			.appendSibling(
				new ChatText("\n" + arrow() + label("Slayer | " + weight.getSlayerWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Slayer Weight"), slayerStr.toString())
					.build()
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("Skills | " + weight.getSkillsWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Skills Weight"), skillsStr.toString())
					.build()
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("Dungeons | " + weight.getDungeonsWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Dungeons Weight"), dungeonsStr.toString())
					.build()
			)
			.appendText("\n\n" + label(labelWithDesc("Lily Weight", lilyWeight.getTotalWeight().getFormatted())))
			.appendSibling(
				new ChatText("\n" + arrow() + label("Slayer | " + lilyWeight.getSlayerWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Slayer Weight"), lilySlayerStr.toString())
					.build()
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("Skills | " + lilyWeight.getSkillsWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Skills Weight"), lilySkillsStr.toString())
					.build()
			)
			.appendSibling(
				new ChatText("\n" + arrow() + label("Dungeons | " + lilyWeight.getDungeonsWeight().getWeightStruct().getFormatted()))
					.setHoverEvent(label("Dungeons Weight"), lilyDungeonsStr)
					.build()
			);

		return wrapText(output);
	}

	public String getWeightChat(String[] args) {
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

		Weight weight = new Weight(player, true);

		return (
			player.getUsername() +
			" has " +
			roundAndFormat(weight.getTotalWeight().getRaw()) +
			" total weight (" +
			roundAndFormat(weight.getSlayerWeight().getWeightStruct().getRaw()) +
			" slayer & " +
			roundAndFormat(weight.getSkillsWeight().getWeightStruct().getRaw()) +
			" skill & " +
			roundAndFormat(weight.getDungeonsWeight().getWeightStruct().getRaw()) +
			" dungeon)"
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:weight";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:we");
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
		executor.submit(() -> sender.addChatMessage(getWeightString(args)));
	}
}
