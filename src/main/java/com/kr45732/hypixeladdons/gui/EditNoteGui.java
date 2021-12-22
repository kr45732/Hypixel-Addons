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

import com.kr45732.hypixeladdons.HypixelAddons;
import com.kr45732.hypixeladdons.gui.component.CustomTextField;
import com.kr45732.hypixeladdons.gui.component.NoteButton;
import com.kr45732.hypixeladdons.utils.structs.Note;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;

public class EditNoteGui extends GuiScreen {

	private final Note note;
	private final NoteGui previousScreen;
	private GuiTextField titleField;
	private CustomTextField descriptionField;

	public EditNoteGui(NoteGui previousScreen, Note note) {
		this.previousScreen = previousScreen;
		this.note = note;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);

		buttonList.addAll(
				Arrays.asList(
						new NoteButton(0, 25, 38, "Pin", () -> note.setPinned(!note.isPinned())),
						new NoteButton(1, 25, 68, "Cancel", () -> HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen)),
						new NoteButton(2, 25, 98, "Delete", () -> {
							note.delete();
							previousScreen.loadNotes();
							HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen);
						}),
						new NoteButton(3, 25, height - 30, "Save & Back", () -> {
							save();
							note.save();
							previousScreen.loadNotes();
							HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen);
						})
				)
		);

		save();

		titleField = new GuiTextField(0, fontRendererObj, 130, 40, width - 140, 20);
		titleField.setMaxStringLength(75);
		titleField.setText(note.getTitle());
		titleField.setFocused(true);
		descriptionField = new CustomTextField(130, 85, width - 140, height - 95, 5);
		descriptionField.setText(note.getDescription());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		titleField.drawTextBox();
		descriptionField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void updateScreen() {
		titleField.updateCursorCounter();
		descriptionField.incrementCursorCount();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		titleField.mouseClicked(mouseX, mouseY, mouseButton);
		descriptionField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		descriptionField.mouseReleased(mouseX, mouseY, state);
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		titleField.textboxKeyTyped(typedChar, keyCode);
		descriptionField.textboxKeyTyped(typedChar, keyCode);
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void actionPerformed(GuiButton clickedButton) {
		for (GuiButton button : buttonList) {
			if (button.id == clickedButton.id) {
				((NoteButton) button).onPress();
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		descriptionField.mouseScrolled(Mouse.getEventDWheel());
		super.handleMouseInput();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	private void save() {
		if(titleField != null){
			note.setTitle(titleField.getText());
		}
		if(descriptionField != null){
			note.setDescription(descriptionField.getText());
		}
	}
}
