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

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class MOTDEditorGui extends GuiScreen {

	private int guiWidth;
	private int guiHeight;
	private int guiX;
	private int guiY;
	private GuiMultiLineTextField textField;

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		updateGuiSize();

		int chatWidth = (int) (mc.gameSettings.chatWidth * 280 + 40);
		textField = new GuiMultiLineTextField(0, guiX + 10, guiY + 38, chatWidth, 9);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiScreen.drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xF2181c25);
		GlStateManager.pushMatrix();
		GlStateManager.scale(2, 2, 1);
		fontRendererObj.drawStringWithShadow("Hypixel Addons", guiX / 2F + 5, guiY / 2F + 5, 0xFF28709e);
		drawHorizontalLine(guiX / 2 + 5, (guiX + guiWidth) / 2 - 5, guiY / 2 + 16, 0xFF28709e);
		GlStateManager.popMatrix();

		textField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		textField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		textField.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void updateScreen() {
		textField.incrementCursorCount();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	private void updateGuiSize() {
		int chatWidth = (int) ((mc.gameSettings.chatWidth * 160 + 20) * 2);
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		guiWidth = Math.min(scaledResolution.getScaledWidth() - 100 / scaledResolution.getScaleFactor(), chatWidth + 20);
		guiHeight = Math.min(scaledResolution.getScaledHeight() - 100 / scaledResolution.getScaleFactor(), 200);
		guiX = (scaledResolution.getScaledWidth() - guiWidth) / 2;
		guiY = (scaledResolution.getScaledHeight() - guiHeight) / 2;
	}
}
