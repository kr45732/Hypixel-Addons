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

import static com.kr45732.hypixeladdons.utils.Constants.HARP_SONG_ID_TO_NAME;
import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.Map;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class HarpCommand extends CommandBase {

	public static final HarpCommand INSTANCE = new HarpCommand();

	public IChatComponent getHarpString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		JsonElement harpJson = higherDepth(player.profileJson(), "harp_quest");
		if (harpJson == null) {
			return wrapText("Player has not used the harp");
		}

		IChatComponent output = player.defaultComponent();
		output.appendText(
			labelWithDesc("Last played song", HARP_SONG_ID_TO_NAME.get(higherDepth(harpJson, "selected_song", "None"))) +
			"\n" +
			labelWithDesc("Claimed melody's hair", "" + higherDepth(harpJson, "claimed_talisman", false))
		);

		for (Map.Entry<String, JsonElement> song : harpJson.getAsJsonObject().entrySet()) {
			if (song.getKey().startsWith("song_") && song.getKey().endsWith("_completions")) {
				String songId = song.getKey().split("song_")[1].split("_completions")[0];
				if (HARP_SONG_ID_TO_NAME.containsKey(songId)) {
					output.appendSibling(
						new ChatText("\n" + arrow() + HARP_SONG_ID_TO_NAME.get(songId))
							.setHoverEvent(
								HARP_SONG_ID_TO_NAME.get(songId),
								arrow() +
								labelWithDesc("Completions", "" + song.getValue().getAsInt()) +
								"\n" +
								arrow() +
								labelWithDesc(
									"Best completion score",
									roundAndFormat(higherDepth(harpJson, "song_" + songId + "_best_completion", 0.0) * 100) + "%"
								) +
								"\n" +
								arrow() +
								labelWithDesc(
									"Perfect completions",
									"" + higherDepth(harpJson, "song_" + songId + "_perfect_completions", 0)
								)
							)
							.build()
					);
				}
			}
		}

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:harp";
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
		executor.submit(() -> sender.addChatMessage(getHarpString(args)));
	}
}
