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

import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public class SettingsGui extends GuiScreen {

	// Components
	private final Map<Integer, GuiPage> pageMap = new HashMap<>();
	private final List<GuiTextField> textFields = new ArrayList<>();
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
	private int pageNum = 0;

	public SettingsGui() {
		this.pageMap.put(
				0,
				new GuiPage()
					.addButtons(
						() -> addButton(0, 0, ConfigUtils.toggleGuildRequestHelper),
						() -> addButton(1, 2, ConfigUtils.toggleGuildJoinMessage),
						() -> addButton(2, 4, ConfigUtils.toggleGuildLeaveMessage)
					)
					.addTextFields(
						() -> addTextField(0, 1, ConfigUtils.guildRequestType),
						() -> addTextField(1, 3, ConfigUtils.guildJoinMessage),
						() -> addTextField(2, 5, ConfigUtils.guildLeaveMessage)
					)
					.addSettings(
						() ->
							addSetting(
								"Guild join request helper (toggle)",
								"When someone requests to join your guild, the mod will automatically send their stats to you.",
								0
							),
						() ->
							addSetting(
								"Guild join request helper type",
								"What type of statistics should be sent ('skyblock', 'bedwars', or 'skywars')",
								1
							),
						() ->
							addSetting(
								"Guild join message (toggle)",
								"Toggle whether a message should be sent when a player joins your guild",
								2
							),
						() -> addSetting("Guild join message", "The message that should be sent when a player joins your guild", 3),
						() ->
							addSetting(
								"Guild leave message (toggle)",
								"Toggle whether a message should be sent when a player leaves your guild",
								4
							),
						() -> addSetting("Guild leave message", "The message that should be sent when a player leaves your guild", 5)
					)
			);

		this.pageMap.put(
				1,
				new GuiPage()
					.addButtons(
						() -> addButton(0, 0, ConfigUtils.toggleGuildChatResponder),
						() -> addButton(1, 2, ConfigUtils.toggleGuildChatCooldownMessage)
					)
					.addTextFields(() -> addTextField(0, 1, "" + ConfigUtils.guildChatCooldown))
					.addSettings(
						() -> addSetting("Guild chat commands (toggle)", "Toggle the guild chat auto-responder/bot", 0),
						() ->
							addSetting(
								"Guild chat command cooldown",
								"Set the cooldown for guild chat commands. Applies per player per command",
								1
							),
						() ->
							addSetting(
								"Guild chat cooldown message (toggle)",
								"Toggle if a message should be sent when a command is on cooldown",
								2
							)
					)
			);

		this.pageMap.put(
				2,
				new GuiPage()
					.addButtons(() -> addButton(0, 0, ConfigUtils.toggleMysteryBoxSorter))
					.addTextFields(() -> addTextField(0, 1, ConfigUtils.mysteryBoxSortType))
					.addSettings(
						() -> addSetting("Mystery box sorting (toggle)", "Toggle if the mystery box GUI should be sorted", 0),
						() -> addSetting("Mystery box sort type", "How the mystery boxes should be sorted ('expiry' or 'stars')", 1)
					)
			);

		this.pageMap.put(
				3,
				new GuiPage()
					.addButtons(
						() -> addButton(0, 0, ConfigUtils.enableCustomSidebar),
						() -> addButton(1, 4, ConfigUtils.hideSidebarRedNumbers),
						() -> addButton(2, 7, ConfigUtils.sidebarChromaBackground)
					)
					.addTextFields(
						() -> addTextField(0, 1, "" + ConfigUtils.sidebarXOffset),
						() -> addTextField(1, 2, "" + ConfigUtils.sidebarYOffset),
						() -> addTextField(2, 3, "" + ConfigUtils.sidebarScale),
						() -> addTextField(3, 5, "" + ConfigUtils.getSidebarBackgroundColorFormatted()),
						() -> addTextField(4, 6, "" + ConfigUtils.sidebarAlpha),
						() -> addTextField(5, 8, "" + ConfigUtils.sidebarChromaSpeed)
					)
					.addSettings(
						() -> addSetting("Custom sidebar (toggle)", "Toggle the custom sidebar", 0),
						() -> addSetting("X offset", "The X offset from the original X position", 1),
						() -> addSetting("Y offset", "The Y offset from the original Y position", 2),
						() -> addSetting("Scale", "The scale factor to change the size of the sidebar", 3),
						() -> addSetting("Hide red numbers (toggle)", "Toggle the red numbers shown on the right of the sidebar", 4),
						() -> addSetting("Background color", "The background color of the sidebar", 5),
						() -> addSetting("Transparency value", "The transparency or alpha value of the background", 6),
						() -> addSetting("Chroma background (toggle)", "Toggle if the background color should be a chroma background", 7),
						() -> addSetting("Chroma speed", "The speed of the chroma background", 8)
					)
			);

		this.pageMap.put(
				4,
				new GuiPage()
					.addButtons(() -> addButton(0, 0, ConfigUtils.enableTodolist))
					.addTextFields(
						() -> addTextField(0, 1, "" + ConfigUtils.todoListX),
						() -> addTextField(1, 2, "" + ConfigUtils.todoListY),
						() -> addTextField(2, 3, "" + ConfigUtils.todoListWidth),
						() -> addTextField(3, 4, "" + ConfigUtils.todoListMaxDisplayItems)
					)
					.addSettings(
						() -> addSetting("Todo list (toggle)", "Toggle the todo list overlay", 0),
						() -> addSetting("X position", "The X position of the todo list", 1),
						() -> addSetting("Y offset", "The Y position of the todo list", 2),
						() -> addSetting("Width", "The width of the todo list", 3),
						() -> addSetting("Max display items", "The max number of items to be displayed on the todo list", 4)
					)
			);
	}

	@Override
	public void initGui() {
		initGui(true);
	}

	public void initGui(boolean save) {
		updateGuiSize();
		scrollbarY = 36;
		isDragging = false;

		initializeComponents(save);

		addCategory(100, 0, "Guild Events");
		addCategory(101, 1, "Chat Commands");
		addCategory(102, 2, "Mystery Box");
		addCategory(103, 3, "Sidebar");
		addCategory(104, 4, "Todo List");
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// Scrollbar wheel
		if (newScrollIfInside >= 36 && GuiUtils.isPointInRegion(guiX, guiY, guiWidth, guiHeight, mouseX, mouseY)) {
			scrollbarY = newScrollIfInside;
			newScrollIfInside = 35;
			initializeComponents(true);
		}

		// Background, title, and the 2 lines
		drawRect(guiX, guiY, guiX + guiWidth, guiY + guiHeight, 0xF2181c25);
		GuiUtils.applyGl(
			() -> {
				GlStateManager.scale(2, 2, 1);
				fontRendererObj.drawStringWithShadow("Hypixel Addons", guiX / 2F + 5, guiY / 2F + 5, 0xFF28709e);
				drawHorizontalLine(guiX / 2 + 5, (guiX + guiWidth) / 2 - 5, guiY / 2 + 16, 0xFF28709e);
				drawVerticalLine(guiX / 2 + 45, guiY / 2 + 16, (guiY + guiHeight) / 2 - 5, 0xFF28709e);
			}
		);

		// Apply GL scissors for the scrolling effect
		GuiUtils.enableGlScissors();
		GuiUtils.applyGLScissors(guiX, guiY + 36, guiWidth, guiHeight - 47);

		// Settings
		pageMap.get(pageNum).getSettings().forEach(Runnable::run);

		// Text fields
		textFields.forEach(GuiTextField::drawTextBox);

		// Buttons
		buttonList.forEach(guiButton -> guiButton.drawButton(mc, mouseX, mouseY));

		GuiUtils.disableGlScissors();

		// Render scrollbar
		if (needsScrollbar()) {
			int scrollbarX = guiX + (guiWidth - scrollbarWidth);
			GuiUtils.drawScrollbar(scrollbarX, guiY + scrollbarY, scrollbarX + scrollbarWidth, guiY + scrollbarY + scrollbarHeight);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (pageNum) {
			case 0:
				switch (button.id) {
					case 0:
						ConfigUtils.setToggleGuildRequestHelper(!ConfigUtils.toggleGuildRequestHelper);
						button.displayString = ConfigUtils.toggleGuildRequestHelper ? "On" : "Off";
						break;
					case 1:
						ConfigUtils.setToggleGuildJoinMessage(!ConfigUtils.toggleGuildJoinMessage);
						button.displayString = ConfigUtils.toggleGuildJoinMessage ? "On" : "Off";
						break;
					case 2:
						ConfigUtils.setToggleGuildLeaveMessage(ConfigUtils.toggleGuildLeaveMessage);
						button.displayString = ConfigUtils.toggleGuildLeaveMessage ? "On" : "Off";
						break;
				}
				break;
			case 1:
				switch (button.id) {
					case 0:
						ConfigUtils.setToggleGuildChatResponder(!ConfigUtils.toggleGuildChatResponder);
						button.displayString = ConfigUtils.toggleGuildChatResponder ? "On" : "Off";
						break;
					case 1:
						ConfigUtils.setToggleGuildChatCooldownMessage(!ConfigUtils.toggleGuildChatCooldownMessage);
						button.displayString = ConfigUtils.toggleGuildChatCooldownMessage ? "On" : "Off";
						break;
				}
				break;
			case 2:
				if (button.id == 0) {
					ConfigUtils.setToggleMysteryBoxSorter(!ConfigUtils.toggleMysteryBoxSorter);
					button.displayString = ConfigUtils.toggleMysteryBoxSorter ? "On" : "Off";
				}
				break;
			case 3:
				switch (button.id) {
					case 0:
						ConfigUtils.setEnableCustomSidebar(!ConfigUtils.enableCustomSidebar);
						button.displayString = ConfigUtils.enableCustomSidebar ? "On" : "Off";
						break;
					case 1:
						ConfigUtils.setHideSidebarRedNumbers(!ConfigUtils.hideSidebarRedNumbers);
						button.displayString = ConfigUtils.hideSidebarRedNumbers ? "On" : "Off";
						break;
					case 2:
						ConfigUtils.setSidebarChromaBackground(!ConfigUtils.sidebarChromaBackground);
						button.displayString = ConfigUtils.sidebarChromaBackground ? "On" : "Off";
						break;
				}
				break;
			case 4:
				if (button.id == 0) {
					ConfigUtils.setEnableTodolist(!ConfigUtils.enableTodolist);
					button.displayString = ConfigUtils.enableTodolist ? "On" : "Off";
					break;
				}
		}

		switch (button.id) {
			case 100:
				if (pageNum != 0) {
					onGuiClosed();
					pageNum = 0;
					initGui(false);
				}
				break;
			case 101:
				if (pageNum != 1) {
					onGuiClosed();
					pageNum = 1;
					initGui(false);
				}
				break;
			case 102:
				if (pageNum != 2) {
					pageNum = 2;
					initGui(false);
				}
				break;
			case 103:
				if (pageNum != 3) {
					onGuiClosed();
					pageNum = 3;
					initGui(false);
				}
				break;
			case 104:
				if (pageNum != 4) {
					onGuiClosed();
					pageNum = 4;
					initGui(false);
				}
				break;
		}
	}

	@Override
	public void onGuiClosed() {
		for (GuiTextField textField : textFields) {
			switch (pageNum) {
				case 0:
					switch (textField.getId()) {
						case 0:
							String guildRequestType = textField.getText().toLowerCase();
							if (
								guildRequestType.equals("skyblock") ||
								guildRequestType.equals("bedwars") ||
								guildRequestType.equals("skywars")
							) {
								ConfigUtils.setGuildRequestType(guildRequestType);
							}
							break;
						case 1:
							ConfigUtils.setGuildJoinMessage(textField.getText());
							break;
						case 2:
							ConfigUtils.setGuildLeaveMessage(textField.getText());
							break;
					}
					break;
				case 1:
					if (textField.getId() == 0) {
						try {
							ConfigUtils.setGuildChatCooldown(Integer.parseInt(textField.getText()));
						} catch (Exception ignored) {}
					}
					break;
				case 2:
					if (textField.getId() == 0) {
						String mysteryBoxSortType = textField.getText().toLowerCase();
						if (mysteryBoxSortType.equals("expiry") || mysteryBoxSortType.equals("stars")) {
							ConfigUtils.setMysteryBoxSortType(mysteryBoxSortType);
						}
					}
					break;
				case 3:
					switch (textField.getId()) {
						case 0:
							try {
								ConfigUtils.setSidebarXOffset(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 1:
							try {
								ConfigUtils.setSidebarYOffset(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 2:
							try {
								ConfigUtils.setSidebarScale(Double.parseDouble(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 3:
							try {
								ConfigUtils.setSidebarBackgroundColor(textField.getText());
							} catch (Exception ignored) {}
							break;
						case 4:
							try {
								ConfigUtils.setSidebarAlpha(MathHelper.clamp_double(Double.parseDouble(textField.getText()), 0, 1));
							} catch (Exception ignored) {}
							break;
						case 5:
							try {
								ConfigUtils.setSidebarChromaSpeed(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
					}
					break;
				case 4:
					switch (textField.getId()) {
						case 0:
							try {
								ConfigUtils.setTodoListX(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 1:
							try {
								ConfigUtils.setTodoListY(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 2:
							try {
								ConfigUtils.setTodoListWidth(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
						case 3:
							try {
								ConfigUtils.setTodoListMaxDisplayItems(Integer.parseInt(textField.getText()));
							} catch (Exception ignored) {}
							break;
					}
					break;
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		textFields.forEach(textBox -> textBox.mouseClicked(mouseX, mouseY, mouseButton));

		if (mouseButton == 0 && needsScrollbar()) {
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

		super.mouseClicked(mouseX, mouseY, mouseButton);
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
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			isDragging = false;
		}
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (isDragging) {
			scrollbarY = MathHelper.clamp_int(mouseY - guiY - scrollbarClickOffset, 36, guiHeight - scrollbarHeight - 11);
			initializeComponents(true);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int dWheel = Mouse.getEventDWheel();
		if (needsScrollbar() && dWheel != 0) {
			dWheel = -Integer.signum(dWheel) * 10;
			newScrollIfInside = MathHelper.clamp_int(scrollbarY + dWheel, 36, guiHeight - scrollbarHeight - 11);
		}
	}

	/* Add gui components */
	private void addButton(int id, int count, boolean curVal) {
		int translateValue = count * 45 + calculateScrollTranslate();

		this.buttonList.add(new GuiToggleButton(id, guiX + guiWidth - 35, guiY + 37 + translateValue, curVal ? "On" : "Off"));
	}

	private void addTextField(int id, int count, String curVal) {
		int translateValue = count * 45 + calculateScrollTranslate();

		GuiTextField textBox = new GuiTextField(id, this.fontRendererObj, guiX + guiWidth - 101, guiY + 38 + translateValue, 90, 16);
		textBox.setText(curVal);
		this.textFields.add(textBox);
	}

	private void addSetting(String title, String description, int count) {
		int translateValue = count * 45 + calculateScrollTranslate();

		drawRect(guiX + 95, guiY + 36 + translateValue, (guiX + guiWidth) - 10, guiY + 76 + translateValue, 0xFF303b52);
		drawRect(guiX + 95, guiY + 36 + translateValue, (guiX + guiWidth) - 10, guiY + 56 + translateValue, 0xFF212939);

		fontRendererObj.drawString(title, guiX + 100, guiY + 41 + translateValue, 0xFFffffff);
		fontRendererObj.drawString(description, guiX + 100, guiY + 62 + translateValue, 0xFF808080);
	}

	private void addCategory(int id, int count, String name) {
		this.buttonList.add(new GuiToggleButton(id, guiX + 15, guiY + 40 + count * 18, name));
	}

	/* Helper methods */
	private void updateGuiSize() {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
		guiWidth = Math.min(scaledResolution.getScaledWidth() - 100 / scaledResolution.getScaleFactor(), 600);
		guiHeight = Math.min(scaledResolution.getScaledHeight() - 100 / scaledResolution.getScaleFactor(), 400);
		guiX = (scaledResolution.getScaledWidth() - guiWidth) / 2;
		guiY = (scaledResolution.getScaledHeight() - guiHeight) / 2;
	}

	private boolean needsScrollbar() {
		double totalContent = (pageMap.get(pageNum).getSettings().size() - 1) * 90 + 80;
		double visibleHeight = guiHeight - 47;
		return totalContent > visibleHeight * 2;
	}

	private int calculateScrollTranslate() {
		if (!needsScrollbar()) {
			return 0;
		}

		double percentScroll = (scrollbarY - 36.0) / (guiHeight - scrollbarHeight - 47); // (Should / please) range from 0 to 1
		double totalContent = (pageMap.get(pageNum).getSettings().size() - 1) * 90 + 80; // Total content height
		double visibleHeight = guiHeight - 47; // Gui height - top offset - bottom offset
		// (Total content height - visible height) * % scroll
		return (int) ((totalContent - 2 * visibleHeight) * percentScroll / -2);
	}

	private void initializeComponents(boolean save) {
		if (save) {
			onGuiClosed();
		}

		GuiPage curPage = pageMap.get(pageNum);

		this.buttonList.removeIf(button -> button.id < 100); // Don't remove category buttons (id of 100+)
		for (Runnable buttonRunnable : curPage.getButtons()) {
			buttonRunnable.run();
		}

		this.textFields.clear();
		for (Runnable textFieldRunnable : curPage.getTextFields()) {
			textFieldRunnable.run();
		}
	}
}
