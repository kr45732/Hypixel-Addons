/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021 kr45732
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kr45732.hypixeladdons.commands.guild;

import com.kr45732.hypixeladdons.HypixelAddons;
import com.kr45732.hypixeladdons.gui.MOTDEditorGui;
import com.kr45732.hypixeladdons.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class MOTDCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "hpa:motd";
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
		Utils.executor.submit(() -> HypixelAddons.INSTANCE.eventListener.setGuiToOpen(new MOTDEditorGui()));
	}
}
