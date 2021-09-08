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

package com.kr45732.hypixeladdons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiUtils {

	public static void applyGLScissors(double x, double y, double width, double height) {
		Minecraft mc = Minecraft.getMinecraft();
		double scaleFactor = new ScaledResolution(mc).getScaleFactor();
		GL11.glScissor(
			(int) (x * scaleFactor),
			(int) (mc.displayHeight - (y + height) * scaleFactor),
			(int) (width * scaleFactor),
			(int) (height * scaleFactor)
		);
	}

	public static void drawScrollbar(int x, int y, int x2, int y2) {
		Gui.drawRect(x, y, x2, y2, 0xff000000);
		Gui.drawRect(x, y, x + 1, y2, 0xff444444);
		Gui.drawRect(x2 - 1, y, x2, y2, 0xff444444);
		Gui.drawRect(x, y, x2, y + 1, 0xff444444);
		Gui.drawRect(x, y2 - 1, x2, y2, 0xff444444);
	}

	public static void enableGlScissors() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}

	public static void disableGlScissors() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static void applyGl(Runnable insideGl) {
		GlStateManager.pushMatrix();
		insideGl.run();
		GlStateManager.popMatrix();
	}

	public static boolean isPointInRegion(int x, int y, int width, int height, int pointX, int pointY) {
		return pointX >= x && pointX < x + width && pointY >= y && pointY < y + height;
	}
}
