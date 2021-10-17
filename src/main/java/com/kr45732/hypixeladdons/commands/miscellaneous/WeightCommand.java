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

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.weight.senither.Weight;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;

import static com.kr45732.hypixeladdons.utils.Utils.*;

public class WeightCommand extends CommandBase {

	public static final WeightCommand INSTANCE = new WeightCommand();

	public static IChatComponent getWeightString(String[] args) {
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
				.append(
					labelWithDesc(capitalizeString(slayerName), weight.getSlayerWeight().getSlayerWeight(slayerName).get())
				);
		}
		StringBuilder skillsStr = new StringBuilder();
		for (String skillName : Constants.SKILL_NAMES) {
			skillsStr
				.append("\n")
				.append(arrow())
				.append(labelWithDesc(capitalizeString(skillName), weight.getSkillsWeight().getSkillsWeight(skillName).get()));
		}
		StringBuilder dungeonsStr = new StringBuilder();
		dungeonsStr
			.append("\n")
			.append(arrow())
			.append(labelWithDesc("Catacombs", weight.getDungeonsWeight().getDungeonWeight("catacombs").get()));
		for (String dungeonClassName : Constants.DUNGEON_CLASS_NAMES) {
			dungeonsStr
				.append("\n")
				.append(arrow())
				.append(
					labelWithDesc(
						capitalizeString(dungeonClassName),
						weight.getDungeonsWeight().getClassWeight(dungeonClassName).get()
					)
				);
		}

		IChatComponent output =player.defaultComponent().appendText(
			"\n\n" +
			label("Slayer | " + weight.getSlayerWeight().getWeightStruct().get()) +
			slayerStr +
			"\n\n" +
			label("Skills | " + weight.getSkillsWeight().getWeightStruct().get()) +
			skillsStr +
			"\n\n" +
			label("Dungeons | " + weight.getDungeonsWeight().getWeightStruct().get()) +
			dungeonsStr +
			"\n\n" +
			labelWithDesc("Total weight", weight.getTotalWeight(false).get())
		);

		return wrapText(output);
	}

	public static String getWeightChat(String[] args) {
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

		Weight weight = new Weight(player);

		return (
			player.getUsername() +
			" has " +
			roundAndFormat(weight.getTotalWeight(true).getRaw()) +
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
