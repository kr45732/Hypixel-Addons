/*
 * Hypixel Addons - A quality of life mod for Hypixel
 * Copyright (c) 2021-2021 kr45732
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

import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Mouse;

public class TodoListGui extends GuiScreen {

	// Components
	private final List<GuiTextField> textFields = new ArrayList<>();
	private final List<GuiButton> miscButtons = new ArrayList<>();
	// Scrollbar
	private final int scrollbarWidth = 10;
	private final int scrollbarHeight = 30;
	private int scrollbarClickOffset = 0;
	private int scrollbarY = 36;
	private boolean isDragging = false;
	private int newScrollIfInside;
	// Gui state
	private int guiWidth;
	private int guiHeight;
	private int guiX;
	private int guiY;

	@Override
	public void initGui() {
		updateGuiSize();
		scrollbarY = 36;
		isDragging = false;

		initializeComponents(true);

		miscButtons.clear();
		miscButtons.add(new GuiButton(100, (guiX + guiWidth) / 2 - 60, (guiY + guiHeight) - 25, 80, 20, "+"));
		miscButtons.add(new GuiButton(101, (guiX + guiWidth) / 2 + 40, (guiY + guiHeight) - 25, 80, 20, "x"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// Scrollbar wheel
		if (newScrollIfInside >= 36 && GuiUtils.isPointInRegion(guiX, guiY, guiWidth, guiHeight, mouseX, mouseY)) {
			scrollbarY = newScrollIfInside;
			newScrollIfInside = 35;
			initializeComponents(true);
		}

		// Background and title
		drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xF2181c25);
		GuiUtils.applyGl(
			() -> {
				GlStateManager.scale(2, 2, 1);
				fontRendererObj.drawStringWithShadow("Hypixel Addons", guiX / 2F + 5, guiY / 2F + 5, 0xFF28709e);
				GuiUtils.drawHorizontalLine(guiX / 2 + 5, (guiX + guiWidth) / 2 - 5, guiY / 2 + 16, 0xFF28709e);
				GuiUtils.drawHorizontalLine(guiX / 2 + 5, (guiX + guiWidth) / 2 - 5, (guiY + guiHeight) / 2 - 16, 0xFF28709e);
			}
		);

		// Apply GL scissors for the scrolling effect
		GuiUtils.enableGlScissors();
		GuiUtils.applyGLScissors(guiX, guiY + 36, guiWidth, guiHeight - 72);

		// To-do list items
		for (int i = 0; i < ConfigUtils.todoList.size(); i++) {
			int translateValue = i * 24 + calculateScrollTranslate();
			drawRect(guiX + 10, guiY + 36 + translateValue, (guiX + guiWidth) - 10, guiY + 56 + translateValue, 0xFF303b52);
			textFields.get(i).drawTextBox();
		}

		// Check boxes
		buttonList.forEach(guiButton -> guiButton.drawButton(mc, mouseX, mouseY));

		GuiUtils.disableGlScissors();

		// Add and clear list buttons
		miscButtons.forEach(button -> button.drawButton(mc, mouseX, mouseY));

		// Render scrollbar
		if (needsScrollbar()) {
			int scrollbarX = guiX + (guiWidth - scrollbarWidth);
			GuiUtils.drawScrollbar(scrollbarX, guiY + scrollbarY, scrollbarX + scrollbarWidth, guiY + scrollbarY + scrollbarHeight);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		textFields.forEach(textBox -> textBox.mouseClicked(mouseX, mouseY, mouseButton));

		if (mouseButton == 0) {
			if (needsScrollbar()) {
				if (
					GuiUtils.isPointInRegion(
						guiX + (guiWidth - scrollbarWidth),
						guiY + scrollbarY,
						scrollbarWidth,
						scrollbarHeight,
						mouseX,
						mouseY
					)
				) {
					isDragging = true;
					scrollbarClickOffset = mouseY - (guiY + scrollbarY);
				}
			}

			for (GuiButton button : this.miscButtons) {
				if (button.mousePressed(this.mc, mouseX, mouseY)) {
					button.playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(button);
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		miscButtons.forEach(button -> button.mouseReleased(mouseX, mouseY));

		if (state == 0) {
			isDragging = false;
		}

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (isDragging) {
			scrollbarY = MathHelper.clamp_int(mouseY - guiY - scrollbarClickOffset, 36, guiHeight - scrollbarHeight - 36);
			initializeComponents(true);
		}
	}

	@Override
	public void updateScreen() {
		textFields.forEach(GuiTextField::updateCursorCounter);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		textFields.forEach(textBox -> textBox.textboxKeyTyped(typedChar, keyCode));
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof GuiCheckBox) {
			if (((GuiCheckBox) button).isChecked() && button.id < ConfigUtils.todoList.size()) {
				ConfigUtils.todoList.remove(button.id);
				initializeComponents(false);
			}
		} else {
			if (button.id == 100) {
				onGuiClosed();
				ConfigUtils.todoList.add("");
				initializeComponents(false);
			} else if (button.id == 101) {
				ConfigUtils.todoList.clear();
				initializeComponents(false);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int dWheel = Mouse.getEventDWheel();
		if (needsScrollbar() && dWheel != 0) {
			dWheel = -Integer.signum(dWheel) * 10;
			newScrollIfInside = MathHelper.clamp_int(scrollbarY + dWheel, 36, guiHeight - scrollbarHeight - 36);
		}
	}

	@Override
	public void onGuiClosed() {
		for (int i = 0; i < textFields.size(); i++) {
			ConfigUtils.todoList.set(i, textFields.get(i).getText());
		}

		ConfigUtils.writeTodoList();
	}

	private void initializeComponents(boolean onGuiClosed) {
		if (onGuiClosed) {
			onGuiClosed();
		}

		buttonList.clear();
		textFields.clear();
		for (int i = 0; i < ConfigUtils.todoList.size(); i++) {
			int translateValue = i * 24 + calculateScrollTranslate();
			buttonList.add(new GuiCheckBox(i, guiX + 15, guiY + 41 + translateValue, "", false));
			GuiTextField guiTextField = new GuiTextField(i, fontRendererObj, guiX + 32, guiY + 42 + translateValue, guiWidth - 42, 16);
			guiTextField.setMaxStringLength(100);
			guiTextField.setText(ConfigUtils.todoList.get(i));
			guiTextField.setEnableBackgroundDrawing(false);
			textFields.add(guiTextField);
		}
	}

	private void updateGuiSize() {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		guiWidth = Math.min(scaledResolution.getScaledWidth() - 100 / scaledResolution.getScaleFactor(), 600);
		guiHeight = Math.min(scaledResolution.getScaledHeight() - 100 / scaledResolution.getScaleFactor(), 400);
		guiX = (scaledResolution.getScaledWidth() - guiWidth) / 2;
		guiY = (scaledResolution.getScaledHeight() - guiHeight) / 2;
	}

	private boolean needsScrollbar() {
		double totalContent = (ConfigUtils.todoList.size() - 1) * 48 + 40;
		double visibleHeight = guiHeight - 36 - 36;
		return totalContent > visibleHeight * 2;
	}

	private int calculateScrollTranslate() {
		if (!needsScrollbar()) {
			return 0;
		}

		double percentScroll = (scrollbarY - 36.0) / (guiHeight - scrollbarHeight - 72); // (Should / please) range from 0 to 1
		double totalContent = (ConfigUtils.todoList.size() - 1) * 48 + 40; // Total content height
		double visibleHeight = guiHeight - 36 - 36; // Gui height - top offset - bottom offset

		// (Total content height - visible height) * % scroll
		return (int) ((totalContent - 2 * visibleHeight) * percentScroll / -2);
	}
}
