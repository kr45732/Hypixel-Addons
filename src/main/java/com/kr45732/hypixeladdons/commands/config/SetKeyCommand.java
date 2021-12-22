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

package com.kr45732.hypixeladdons.commands.config;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class SetKeyCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "hpa:setkey";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <key>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(
			() -> {
				EntityPlayer player = (EntityPlayer) sender;

				if (args.length != 1) {
					player.addChatMessage(getUsage(this));
					return;
				}

				JsonElement keyJson = getJson("https://api.hypixel.net/key?key=" + args[0]);
				if (higherDepth(keyJson, "cause") != null) {
					player.addChatMessage(getFailCause(higherDepth(keyJson, "cause").getAsString()));
					return;
				}

				ConfigUtils.setHypixelKey(args[0]);
				player.addChatMessage(wrapText(labelWithDesc("Set API key to", args[0])));
			}
		);
	}
}
