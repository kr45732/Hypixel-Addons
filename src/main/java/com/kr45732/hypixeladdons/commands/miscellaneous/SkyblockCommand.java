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

import com.kr45732.hypixeladdons.utils.*;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.nullicorn.nedit.type.NBTCompound;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SkyblockCommand extends CommandBase {

	public static SkyblockCommand INSTANCE = new SkyblockCommand();

	public static IChatComponent getSkyblockString(String[] args) {
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

		IChatComponent output = Utils
			.empty()
			.appendSibling(
				new ChatText(Utils.labelWithDesc("Player", C.UNDERLINE + player.getFormattedUsername()))
					.setClickEvent(ClickEvent.Action.OPEN_URL, player.skyblockStatsLink())
					.build()
			)
			.appendText("\n\n");

		List<IChatComponent> skillComponents = new ArrayList<>();
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
					progressSA += skillInfo.getProgressLevel();
				}
			} else {
				skillComponents.add(new ChatComponentText("\n" + Utils.arrow() + Utils.labelWithDesc(Utils.capitalizeString(skill), "??")));
			}
		}
		progressSA /= Constants.SKILL_NAMES.size();
		output.appendText(Utils.labelWithDesc("Progress skill average", Utils.roundAndFormat(progressSA)));
		skillComponents.forEach(output::appendSibling);

		int svenOneKills = player.getSlayerBossKills("wolf", 0);
		int svenTwoKills = player.getSlayerBossKills("wolf", 1);
		int svenThreeKills = player.getSlayerBossKills("wolf", 2);
		int svenFourKills = player.getSlayerBossKills("wolf", 3);

		int revOneKills = player.getSlayerBossKills("zombie", 0);
		int revTwoKills = player.getSlayerBossKills("zombie", 1);
		int revThreeKills = player.getSlayerBossKills("zombie", 2);
		int revFourKills = player.getSlayerBossKills("zombie", 3);
		int revFiveKills = player.getSlayerBossKills("zombie", 4);

		int taraOneKills = player.getSlayerBossKills("spider", 0);
		int taraTwoKills = player.getSlayerBossKills("spider", 1);
		int taraThreeKills = player.getSlayerBossKills("spider", 2);
		int taraFourKills = player.getSlayerBossKills("spider", 3);

		int endermanOneKills = player.getSlayerBossKills("enderman", 0);
		int endermanTwoKills = player.getSlayerBossKills("enderman", 1);
		int endermanThreeKills = player.getSlayerBossKills("enderman", 2);
		int endermanFourKills = player.getSlayerBossKills("enderman", 3);

		long coinsSpentOnSlayers =
			100L *
			(svenOneKills + revOneKills + taraOneKills) +
			2000L *
			(svenTwoKills + revTwoKills + taraTwoKills) +
			10000L *
			(svenThreeKills + revThreeKills + taraThreeKills) +
			50000L *
			(svenFourKills + revFourKills + taraFourKills) +
			100000L *
			revFiveKills +
			2000L *
			endermanOneKills +
			7500L *
			endermanTwoKills +
			20000L *
			endermanThreeKills +
			50000L *
			endermanFourKills;

		output.appendText(
			"\n\n" +
			Utils.labelWithDesc("Total slayer", Utils.formatNumber(player.getTotalSlayer()) + " XP") +
			"\n" +
			Utils.labelWithDesc("Total coins spent", Utils.simplifyNumber(coinsSpentOnSlayers))
		);

		for (Map.Entry<String, String> slayerName : Constants.SLAYER_NAMES_MAP.entrySet()) {
			StringBuilder curSlayerKills = new StringBuilder();
			int maxTier = slayerName.getValue().equals("zombie") ? 5 : 4;
			for (int i = 1; i <= maxTier; i++) {
				curSlayerKills
					.append(Utils.labelWithDesc("Tier " + i, "" + player.getSlayerBossKills(slayerName.getValue(), i - 1)))
					.append(i != maxTier ? "\n" : "");
			}

			output.appendSibling(
				new ChatText(
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						Utils.capitalizeString(slayerName.getValue()) + " (" + player.getSlayerLevel(slayerName.getKey()) + ")",
						Utils.simplifyNumber(player.getSlayer(slayerName.getKey())) + " XP"
					)
				)
					.setHoverEvent(Utils.capitalizeString(slayerName.getValue()) + " - Boss Kills", curSlayerKills.toString())
					.build()
			);
		}

		SkillsStruct skillInfo = player.getCatacombsSkill();
		output
			.appendText(
				"\n\n" +
				Utils.labelWithDesc("True catacombs level", "" + skillInfo.skillLevel) +
				"\n" +
				Utils.labelWithDesc("Secrets", Utils.formatNumber(player.getDungeonSecrets()))
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
					Utils.labelWithDesc(Utils.capitalizeString(className), Utils.roundAndFormat(skillInfo.getProgressLevel())) +
					(player.getSelectedDungeonClass().equalsIgnoreCase(className) ? "" + C.DARK_GREEN + C.BOLD + " - Selected Class" : "")
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

		double bankBal = player.getBankBalance();
		output.appendText(
			"\n\n" +
			Utils.label("Miscellaneous") +
			"\n" +
			Utils.arrow() +
			Utils.labelWithDesc("Weight", Utils.roundAndFormat(player.getWeight())) +
			"\n" +
			Utils.arrow() +
			Utils.labelWithDesc("Bank balance", bankBal == -1 ? "Banking API disabled" : Utils.simplifyNumber(bankBal) + " coins") +
			"\n" +
			Utils.arrow() +
			Utils.labelWithDesc("Purse coins", Utils.simplifyNumber(player.getPurseCoins()) + " coins") +
			"\n" +
			Utils.arrow() +
			Utils.labelWithDesc("Fairy souls", Utils.simplifyNumber(player.getFairySouls()) + "/227") +
			"\n\n" +
			Utils.label("Equipped armor")
		);

		List<NBTCompound> armorList = player.getInventoryArmorNBT();
		for (int i = 0; i < armorList.size(); i++) {
			NBTCompound cur = armorList.get(i);
			String itemType;
			switch (i) {
				case 0:
					itemType = "Helmet";
					break;
				case 1:
					itemType = "Chestplate";
					break;
				case 2:
					itemType = "Leggings";
					break;
				default:
					itemType = "Boots";
					break;
			}

			output.appendSibling(
				new ChatText("\n" + Utils.arrow() + Utils.label(itemType) + ": " + cur.getString("tag.display.Name", "Error"))
					.setHoverEvent(Utils.nbtToTooltip(cur))
					.build()
			);
		}

		return Utils.wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:skyblock";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:sb");
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
		Utils.executor.submit(() -> sender.addChatMessage(getSkyblockString(args)));
	}
}
