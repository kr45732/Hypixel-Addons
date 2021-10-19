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

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.api.Player;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.Utils.instantToDHM;

public class CakesCommand extends CommandBase {

	public static final CakesCommand INSTANCE = new CakesCommand();

	public IChatComponent getCakesString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		Player player = newPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player.defaultComponent();

		List<String> missingCakes = new ArrayList<>(
				Arrays.asList(
						"cake_strength",
						"cake_pet_luck",
						"cake_health",
						"cake_walk_speed",
						"cake_magic_find",
						"cake_ferocity",
						"cake_defense",
						"cake_sea_creature_chance",
						"cake_intelligence"
				)
		);

		StringBuilder activeCakes = new StringBuilder();
		if (higherDepth(player.profileJson(), "temp_stat_buffs") != null) {
			for (JsonElement cake : higherDepth(player.profileJson(), "temp_stat_buffs").getAsJsonArray()) {
				Instant expires = Instant.ofEpochMilli(higherDepth(cake, "expire_at").getAsLong());
				if (expires.isAfter(Instant.now())) {
					String cakeName = higherDepth(cake, "key").getAsString();
					activeCakes
							.append("\n")
							.append(arrow())
							.append(labelWithDesc(capitalizeString(cakeName.split("cake_")[1].replace("_", " ")) + " Cake", "expires in " + instantToDHM(Duration.between(Instant.now(), expires))));
					missingCakes.remove(cakeName);
				}
			}
		}
		output.appendText("\n\n" + label("Active cakes") + ( activeCakes.length() > 0 ? activeCakes.toString() : "None"));

		StringBuilder missingCakesStr = new StringBuilder();
		for (String missingCake : missingCakes) {
			missingCakesStr
					.append("\n")
					.append(arrow())
					.append(label(capitalizeString(missingCake.split("cake_")[1].replace("_", " ")) + " Cake"));
		}
		output.appendText("\n\n" + label("Inactive cakes") + ( missingCakesStr.length() > 0 ? missingCakesStr.toString() : "None"));

		return wrapText(output);
	}

	@Override
	public String getCommandName() {
		return "hpa:cakes";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:cake");
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
		executor.submit(() -> sender.addChatMessage(getCakesString(args)));
	}
}
