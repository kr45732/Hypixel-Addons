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

package com.kr45732.hypixeladdons.listeners;

import static com.kr45732.hypixeladdons.utils.Constants.*;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.common.collect.Sets;
import com.kr45732.hypixeladdons.commands.hypixel.BedwarsCommand;
import com.kr45732.hypixeladdons.commands.hypixel.SkywarsCommand;
import com.kr45732.hypixeladdons.features.MysteryBoxOverlay;
import com.kr45732.hypixeladdons.features.TodoListOverlay;
import com.kr45732.hypixeladdons.gui.ExtendedGuiIngame;
import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.SkillsStruct;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventListener {

	private final Pattern guildJoinRequestPattern = Pattern.compile("(\\w+) has requested to join the Guild!");
	private final Set<String> skyblockInAllLanguages = Sets.newHashSet("SKYBLOCK", "\u7A7A\u5C9B\u751F\u5B58", "\u7A7A\u5CF6\u751F\u5B58");
	private GuiScreen guiToOpen;
	private long lastLongUpdate = 0;
	private final ExtendedGuiIngame extendedGuiIngame;

	public EventListener() {
		this.extendedGuiIngame = new ExtendedGuiIngame();
	}

	@SubscribeEvent
	public void onTick(TickEvent.RenderTickEvent event) {
		if (guiToOpen != null) {
			Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
			guiToOpen = null;
		}

		TodoListOverlay.drawOverlay();
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		String formattedText = event.message.getFormattedText();

		Matcher guildJoinMatcher = guildJoinRequestPattern.matcher(StringUtils.stripControlCodes(formattedText));
		if (formattedText.contains("§r§a") && guildJoinMatcher.find()) {
			String username = guildJoinMatcher.group(1);
			executor.submit(() -> Minecraft.getMinecraft().thePlayer.addChatMessage(onGuildRequest(username)));
		} else if (ConfigUtils.toggleGuildJoinMessage && formattedText.endsWith("" + C.RESET + C.YELLOW + " joined the guild!" + C.RESET)) {
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/gc " + ConfigUtils.guildJoinMessage);
		} else if (ConfigUtils.toggleGuildLeaveMessage && formattedText.endsWith("" + C.RESET + C.YELLOW + "left the guild!" + C.RESET)) {
			Minecraft.getMinecraft().thePlayer.sendChatMessage("/gc " + ConfigUtils.guildLeaveMessage);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		try {
			Minecraft mc = Minecraft.getMinecraft();
			if (event.phase != TickEvent.Phase.START || mc == null || mc.theWorld == null || mc.thePlayer == null) {
				return;
			}

			if (!(mc.ingameGUI instanceof ExtendedGuiIngame)) {
				mc.ingameGUI = this.extendedGuiIngame;
			}

			long currentTime = System.currentTimeMillis();
			if (currentTime - lastLongUpdate > 1000) {
				lastLongUpdate = currentTime;
			} else {
				return;
			}

			if (!mc.isSingleplayer() && mc.thePlayer.getClientBrand() != null && isOnHypixel()) {
				Scoreboard scoreboard = mc.theWorld.getScoreboard();
				ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
				if (sidebar != null) {
					String name = sidebar.getDisplayName().replaceAll("(?i)\\u00A7.", "");
					for (String skyblock : skyblockInAllLanguages) {
						if (name.startsWith(skyblock)) {
							onSkyblock = true;
							return;
						}
					}
				}
			}
			onSkyblock = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		if (MysteryBoxOverlay.isGuiOpen()) {
			MysteryBoxOverlay.INSTANCE = new MysteryBoxOverlay();
		}
	}

	private IChatComponent onGuildRequest(String username) {
		if (ConfigUtils.getHypixelKey() == null) {
			return getFailCause("Guild join request helper\n ⚠ API key not set. Use /hpa:setkey.");
		}

		switch (ConfigUtils.guildRequestType) {
			case "skyblock":
				return getSkyblockStats(username);
			case "bedwars":
				return getBedwarsStats(username);
			case "skywars":
				return getSkywarsStats(username);
			default:
				return getFailCause("Guild join request helper\n ⚠ Invalid stats type set.");
		}
	}

	private IChatComponent getSkyblockStats(String username) {
		Player player = new Player(username);
		if (!player.isValid()) {
			return getFailCause("Guild join request helper\n ⚠ " + player.getFailCause());
		}

		IChatComponent output = empty()
			.appendSibling(
				new ChatText(labelWithDesc("Player", C.UNDERLINE + player.getFormattedUsername()))
					.setClickEvent(ClickEvent.Action.OPEN_URL, player.skyblockStatsLink())
					.build()
			)
			.appendText("\n\n");

		StringBuilder skillBreakdown = new StringBuilder();
		double progressSA = 0;
		for (String skill : ALL_SKILL_NAMES) {
			SkillsStruct skillInfo = player.getSkill(skill);
			if (skillInfo != null) {
				skillBreakdown
					.append("\n")
					.append(arrow())
					.append(labelWithDesc(capitalizeString(skillInfo.skillName), roundAndFormat(skillInfo.getProgressLevel())));

				if (!skill.equals("runecrafting") && !skill.equals("carpentry")) {
					progressSA += skillInfo.getProgressLevel();
				}
			} else {
				skillBreakdown.append("\n").append(arrow()).append(labelWithDesc(capitalizeString(skill), "??"));
			}
		}
		progressSA /= SKILL_NAMES.size();

		output
			.appendText(label("Skills") + "\n" + arrow() + labelWithDesc("Progress skill average", roundAndFormat(progressSA)))
			.appendSibling(
				new ChatText("\n" + arrow() + labelWithDesc("Skill breakdown", "Hover here"))
					.setHoverEvent("Skill breakdown", skillBreakdown.toString().trim())
					.build()
			);

		output.appendText("\n\n" + labelWithDesc("Total slayer", formatNumber(player.getTotalSlayer()) + " XP"));

		for (Map.Entry<String, String> slayerName : SLAYER_NAMES_MAP.entrySet()) {
			StringBuilder curSlayerKills = new StringBuilder();
			int maxTier = slayerName.getValue().equals("zombie") ? 5 : 4;
			for (int i = 1; i <= maxTier; i++) {
				curSlayerKills
					.append(labelWithDesc("Tier " + i, "" + player.getSlayerBossKills(slayerName.getValue(), i - 1)))
					.append(i != maxTier ? "\n" : "");
			}

			output.appendSibling(
				new ChatText(
					"\n" +
					arrow() +
					labelWithDesc(
						capitalizeString(slayerName.getValue()) + " (" + player.getSlayerLevel(slayerName.getKey()) + ")",
						simplifyNumber(player.getSlayer(slayerName.getKey())) + " XP"
					)
				)
					.setHoverEvent(capitalizeString(slayerName.getValue()) + " - Boss Kills", curSlayerKills.toString())
					.build()
			);
		}

		SkillsStruct skillInfo = player.getCatacombsSkill();
		output
			.appendText("\n\n" + label("Dungeons") + "\n" + arrow() + labelWithDesc("Secrets", formatNumber(player.getDungeonSecrets())))
			.appendSibling(
				new ChatText(
					"\n" + arrow() + labelWithDesc(capitalizeString(skillInfo.skillName), roundAndFormat(skillInfo.getProgressLevel()))
				)
					.setHoverEvent(
						capitalizeString(skillInfo.skillName),
						labelWithDesc("XP progress", simplifyNumber(skillInfo.expCurrent) + " / " + simplifyNumber(skillInfo.expForNext)) +
						"\n" +
						labelWithDesc("Total XP", simplifyNumber(skillInfo.totalSkillExp)) +
						"\n" +
						labelWithDesc(
							"Progress",
							(skillInfo.skillLevel == skillInfo.maxSkillLevel ? "MAX" : roundProgress(skillInfo.progressToNext))
						)
					)
					.build()
			);

		double bankBal = player.getBankBalance();
		output.appendText(
			"\n\n" +
			label("Miscellaneous") +
			"\n" +
			arrow() +
			labelWithDesc("Weight", roundAndFormat(player.getWeight())) +
			"\n" +
			arrow() +
			labelWithDesc("Bank balance", bankBal == -1 ? "Banking API disabled" : simplifyNumber(bankBal) + " coins") +
			"\n" +
			arrow() +
			labelWithDesc("Purse coins", simplifyNumber(player.getPurseCoins()) + " coins") +
			"\n" +
			arrow() +
			labelWithDesc("Fairy souls", simplifyNumber(player.getFairySouls()) + "/227")
		);

		return wrapText(output);
	}

	private IChatComponent getBedwarsStats(String username) {
		HypixelPlayer player = new HypixelPlayer(username);
		if (!player.isValid()) {
			return getFailCause("Guild join request helper\n ⚠ " + player.getFailCause());
		}

		return BedwarsCommand.getBedwarsString(new String[] { username });
	}

	private IChatComponent getSkywarsStats(String username) {
		HypixelPlayer player = new HypixelPlayer(username);
		if (!player.isValid()) {
			return getFailCause("Guild join request helper\n ⚠ " + player.getFailCause());
		}

		return SkywarsCommand.getSkywarsString(new String[] { username });
	}

	public void setGuiToOpen(GuiScreen guiToOpen) {
		this.guiToOpen = guiToOpen;
	}
}
