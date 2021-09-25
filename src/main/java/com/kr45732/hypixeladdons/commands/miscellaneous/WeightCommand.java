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

package com.kr45732.hypixeladdons.commands.miscellaneous;

import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.weight.Weight;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class WeightCommand extends CommandBase {

	public static WeightCommand INSTANCE = new WeightCommand();

	public static IChatComponent getWeightString(String[] args) {
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

		Weight weight = new Weight(player);
		StringBuilder slayerStr = new StringBuilder();
		for (String slayerName : Constants.SLAYER_NAMES_MAP.keySet()) {
			slayerStr
				.append("\n")
				.append(Utils.arrow())
				.append(
					Utils.labelWithDesc(Utils.capitalizeString(slayerName), weight.getSlayerWeight().getSlayerWeight(slayerName).get())
				);
		}
		StringBuilder skillsStr = new StringBuilder();
		for (String skillName : Constants.SKILL_NAMES) {
			skillsStr
				.append("\n")
				.append(Utils.arrow())
				.append(Utils.labelWithDesc(Utils.capitalizeString(skillName), weight.getSkillsWeight().getSkillsWeight(skillName).get()));
		}
		StringBuilder dungeonsStr = new StringBuilder();
		dungeonsStr
			.append("\n")
			.append(Utils.arrow())
			.append(Utils.labelWithDesc("Catacombs", weight.getDungeonsWeight().getDungeonWeight("catacombs").get()));
		for (String dungeonClassName : Constants.DUNGEON_CLASS_NAMES) {
			dungeonsStr
				.append("\n")
				.append(Utils.arrow())
				.append(
					Utils.labelWithDesc(
						Utils.capitalizeString(dungeonClassName),
						weight.getDungeonsWeight().getClassWeight(dungeonClassName).get()
					)
				);
		}

		IChatComponent output = Utils.empty().appendSibling(player.getLink());
		output.appendText(
			"\n\n" +
			Utils.label("Slayer | " + weight.getSlayerWeight().getWeightStruct().get()) +
			slayerStr +
			"\n\n" +
			Utils.label("Skills | " + weight.getSkillsWeight().getWeightStruct().get()) +
			skillsStr +
			"\n\n" +
			Utils.label("Dungeons | " + weight.getDungeonsWeight().getWeightStruct().get()) +
			dungeonsStr +
			"\n\n" +
			Utils.labelWithDesc("Total weight", weight.getTotalWeight(false).get())
		);

		return Utils.wrapText(output);
	}

	public static String getWeightChat(String[] args) {
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

		Weight weight = new Weight(player);

		return (
			player.getUsername() +
			" has " +
			Utils.roundAndFormat(weight.getTotalWeight(true).getRaw()) +
			" total weight (" +
			Utils.roundAndFormat(weight.getSlayerWeight().getWeightStruct().getRaw()) +
			" slayer & " +
			Utils.roundAndFormat(weight.getSkillsWeight().getWeightStruct().getRaw()) +
			" skill & " +
			Utils.roundAndFormat(weight.getDungeonsWeight().getWeightStruct().getRaw()) +
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
		return "/" + getCommandName() + " <player> [profile]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getWeightString(args)));
	}
}
