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

import static com.kr45732.hypixeladdons.utils.Constants.HOTM_PERK_ID_TO_NAME;
import static com.kr45732.hypixeladdons.utils.Constants.HOTM_PERK_MAX_LEVEL;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class HotmCommand extends CommandBase {

	public static final HotmCommand INSTANCE = new HotmCommand();

	public IChatComponent getHotmString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player.defaultComponent();
		SkillsStruct skillInfo = player.getHOTM();
		JsonElement miningJson = higherDepth(player.profileJson(), "mining_core");

		output.appendText(
			"\n\n" +
			label("Statistics") +
			arrow() +
			labelWithDesc("HOTM level", "" + skillInfo.getCurrentLevel()) +
			" (" +
			labelWithDesc("Progress", roundProgress(skillInfo.getProgressToNext())) +
			")\n" +
			arrow() +
			labelWithDesc("Tokens", "" + higherDepth(miningJson, "tokens", 0)) +
			" (" +
			labelWithDesc("Spent", "" + higherDepth(miningJson, "tokens_spent", 0)) +
			")\n" +
			arrow() +
			labelWithDesc("Mithril Powder", formatNumber(higherDepth(miningJson, "powder_mithril", 0))) +
			" (" +
			labelWithDesc("Spent", formatNumber(higherDepth(miningJson, "powder_spent_mithril", 0))) +
			")\n" +
			arrow() +
			labelWithDesc("Gemstone Powder", formatNumber(higherDepth(miningJson, "powder_gemstone", 0))) +
			" (" +
			labelWithDesc("Spent", formatNumber(higherDepth(miningJson, "powder_spent_gemstone", 0))) +
			")\n" +
			arrow() +
			labelWithDesc(
				"Selected ability",
				capitalizeString(higherDepth(miningJson, "selected_pickaxe_ability", "none").replace("_", " "))
			) +
			"\n\n" +
			label("Perks")
		);

		for (Map.Entry<String, JsonElement> perk : higherDepth(miningJson, "nodes").getAsJsonObject().entrySet()) {
			if (perk.getValue().getAsJsonPrimitive().isNumber()) {
				output.appendText(
					"\n" +
					arrow() +
					labelWithDesc(
						capitalizeString(HOTM_PERK_ID_TO_NAME.getOrDefault(perk.getKey(), perk.getKey().replace("_", " "))),
						perk.getValue().getAsInt() + "/" + HOTM_PERK_MAX_LEVEL.getOrDefault(perk.getKey(), 50)
					)
				);
			}
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:hotm";
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
		executor.submit(() -> sender.addChatMessage(getHotmString(args)));
	}
}
