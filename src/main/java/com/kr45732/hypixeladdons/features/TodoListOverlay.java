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

package com.kr45732.hypixeladdons.features;

import com.kr45732.hypixeladdons.gui.TodoListGui;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;

public class TodoListOverlay {

	public static void drawOverlay() {
		if (!ConfigUtils.enableTodolist || ConfigUtils.todoList.size() == 0) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		if (mc.isGamePaused()) {
			return;
		}

		if (mc.currentScreen != null) {
			if (!(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof TodoListGui)) {
				return;
			}
		}

		FontRenderer fr = mc.fontRendererObj;

		int todoListHeight = ConfigUtils.todoListY + fr.FONT_HEIGHT + 10;
		for (int i = 0; i < Math.min(ConfigUtils.todoListMaxDisplayItems, ConfigUtils.todoList.size()); i++) {
			todoListHeight +=
				fr.splitStringWidth(C.DARK_GREEN + "✔ " + C.DARK_AQUA + ConfigUtils.todoList.get(i), ConfigUtils.todoListWidth) + 5;
		}

		GuiScreen.drawRect(
			ConfigUtils.todoListX,
			ConfigUtils.todoListY,
			ConfigUtils.todoListX + ConfigUtils.todoListWidth,
			ConfigUtils.todoListY + todoListHeight,
			0xA6000000
		);
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.3, 1.3, 1.3);
		fr.drawStringWithShadow("Todo List", ConfigUtils.todoListX / 1.3F + 5, ConfigUtils.todoListY / 1.3F + 4, 0xFF28709e);
		GlStateManager.popMatrix();
		TodoListOverlay.drawHorizontalLine(
			ConfigUtils.todoListX + 5,
			ConfigUtils.todoListX + ConfigUtils.todoListWidth - 5,
			ConfigUtils.todoListY + fr.FONT_HEIGHT + 9
		);

		int yPos = ConfigUtils.todoListY + fr.FONT_HEIGHT + 15;
		for (int i = 0; i < Math.min(ConfigUtils.todoListMaxDisplayItems, ConfigUtils.todoList.size()); i++) {
			String curTodo = ConfigUtils.todoList.get(i);
			fr.drawSplitString(
				C.DARK_GREEN + "✔ " + C.DARK_AQUA + curTodo,
				ConfigUtils.todoListX + 5,
				yPos,
				ConfigUtils.todoListWidth - 5,
				0xFFFFFFFF
			);

			yPos += fr.splitStringWidth(C.DARK_GREEN + "✔ " + C.DARK_AQUA + curTodo, ConfigUtils.todoListWidth) + 3;
		}
	}

	private static void drawHorizontalLine(int startX, int endX, int y) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		GuiScreen.drawRect(startX, y, endX, y + 1, -14126946);
	}
}
