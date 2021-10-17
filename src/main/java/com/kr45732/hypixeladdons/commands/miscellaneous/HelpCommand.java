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

import com.kr45732.hypixeladdons.HypixelAddons;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.kr45732.hypixeladdons.utils.Utils.*;

public class HelpCommand extends CommandBase {

	private static final String commandHelpList;
	static {
		StringBuilder out = new StringBuilder();
		for (CommandBase command : HypixelAddons.INSTANCE.getCommands()) {
			String usage = command.getCommandUsage(null);
			if(usage.contains("\n")){
				for (String s : usage.split("\n")) {
					out.append(arrow()).append(label(s)).append("\n");
				}
			}else {
				out.append(arrow()).append(label(command.getCommandUsage(null))).append("\n");
			}
		}
		commandHelpList = out.toString().trim();
	}

	@Override
	public String getCommandName() {
		return "hpa:help";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:commands");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		sender.addChatMessage(wrapText(commandHelpList));
	}
}
