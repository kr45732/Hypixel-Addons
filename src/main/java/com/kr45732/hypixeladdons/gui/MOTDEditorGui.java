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

package com.kr45732.hypixeladdons.gui;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.kr45732.hypixeladdons.gui.component.MOTDTextField;
import com.kr45732.hypixeladdons.utils.GuiUtils;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MOTDEditorGui extends GuiScreen {

	private int guiWidth;
	private int guiHeight;
	private int guiX;
	private int guiY;
	private MOTDTextField textField;

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		updateGuiSize();
		if (textField != null) {
			ConfigUtils.setMotdText(textField.getText());
		}

		int chatWidth = (int) (mc.gameSettings.chatWidth * 280 + 40);
		textField = new MOTDTextField( guiX + 10, guiY + 38, chatWidth, (int) (fontRendererObj.FONT_HEIGHT * 9.5), 5);
		textField.setMaxLines(9);
		textField.setMaxLineLength(100);
		textField.setText(ConfigUtils.motdText);
		buttonList.add(new GuiButton(0, guiX + 10, guiY + guiHeight - 30, 150, 20, "Submit"));
		buttonList.add(new GuiButton(1, guiX + guiWidth - 10 - 150, guiY + guiHeight - 30, 150, 20, "Clear"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiScreen.drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xF2181c25);
		GlStateManager.pushMatrix();
		GlStateManager.scale(2, 2, 1);
		fontRendererObj.drawStringWithShadow("Hypixel Addons", guiX / 2F + 5, guiY / 2F + 5, 0xFF28709e);
		GuiUtils.drawHorizontalLine(guiX / 2 + 5, (guiX + guiWidth) / 2 - 5, guiY / 2 + 16, 0xFF28709e);
		GlStateManager.popMatrix();

		textField.drawTextBox();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		textField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		textField.mouseReleased(mouseX, mouseY, state);
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void handleMouseInput() throws IOException {
		textField.mouseScrolled(Mouse.getEventDWheel());
		super.handleMouseInput();
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
		ConfigUtils.setMotdText(textField.getText());
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			ConfigUtils.setMotdText(textField.getText());
			mc.displayGuiScreen(null);
			IChatComponent chatComponent = empty();
			String[] lines = textField.getText().split("\n");
			for (int i = 0; i < lines.length; i++) {
				if (i != 0) {
					chatComponent.appendText("\n");
				}

				String line = lines[i];
				chatComponent.appendSibling(
					new ChatText(labelWithDesc("Click here to set line", "" + (i + 1)))
						.setClickEvent(ClickEvent.Action.RUN_COMMAND, "/g motd set " + (i + 1) + " " + line)
						.build()
				);
			}

			mc.thePlayer.addChatMessage(wrapText(chatComponent));
		} else if (button.id == 1) {
			textField.setText("");
		}
	}

	private void updateGuiSize() {
		int chatWidth = (int) (mc.gameSettings.chatWidth * 280 + 40);
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		guiWidth = Math.min(scaledResolution.getScaledWidth() - 100 / scaledResolution.getScaleFactor(), chatWidth + 20);
		guiHeight = Math.min(scaledResolution.getScaledHeight() - 100 / scaledResolution.getScaleFactor(), 200);
		guiX = (scaledResolution.getScaledWidth() - guiWidth) / 2;
		guiY = (scaledResolution.getScaledHeight() - guiHeight) / 2;
	}
}
