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

package com.kr45732.hypixeladdons.commands.hypixel;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

public class HypixelCommand extends CommandBase {

	public static final HypixelCommand INSTANCE = new HypixelCommand();

	public static IChatComponent getHypixelString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = newHypixelPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player
			.defaultComponent()
			.appendText(
				"\n\n" +
				labelWithDesc("Hypixel level", roundAndFormat(player.getHypixelLevel())) +
				"\n" +
				labelWithDesc("Achievement points", formatNumber(player.getAchievementPoints())) +
				"\n" +
				labelWithDesc("Karma", formatNumber(player.getKarma())) +
				"\n" +
				labelWithDesc("Status", player.isOnline() ? "online" : "offline") +
				(player.isOnline() ? "" : "\n" + labelWithDesc("Last updated", player.getLastUpdatedFormatted())) +
				"\n" +
				labelWithDesc("First login", player.getFirstLoginFormatted()) +
				(player.getSocialMediaLinks().entrySet().size() > 0 ? "\n\n" + label("Social Media") : "")
			);

		for (Map.Entry<String, JsonElement> socialMedia : player.getSocialMediaLinks().entrySet()) {
			String curVal = socialMedia.getValue().getAsString();
			if (curVal.contains("http")) {
				output.appendSibling(
					new ChatText(
						"\n" +
						arrow() +
						labelWithDesc(
							socialMedia.getKey().equals("HYPIXEL") ? "Hypixel Forums" : capitalizeString(socialMedia.getKey()),
							"link"
						)
					)
						.setClickEvent(ClickEvent.Action.OPEN_URL, curVal)
						.build()
				);
			} else {
				output.appendText(
					"\n" +
					arrow() +
					labelWithDesc(
						socialMedia.getKey().equals("HYPIXEL") ? "Hypixel Forums" : capitalizeString(socialMedia.getKey()),
						curVal
					)
				);
			}
		}

		return wrapText(output);
	}

	public static String getHypixelChat(String[] args) {
		if (args.length != 1) {
			return getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKeyChat();
		}

		HypixelPlayer player = new HypixelPlayer(args[0]);
		if (!player.isValid()) {
			return getFailCauseChat(player);
		}

		return (
			player.getStrippedFormattedUsername() +
			", Hypixel level: " +
			roundAndFormat(player.getHypixelLevel()) +
			", Status: " +
			(player.isOnline() ? "online" : "last online at " + player.getLastUpdatedFormattedShort()) +
			", Discord: " +
			(player.getDiscordTag() != null ? player.getDiscordTag() : "N/A")
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:hypixel";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [player]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getHypixelString(args)));
	}
}
