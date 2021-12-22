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
import com.kr45732.hypixeladdons.gui.component.NoteButton;
import com.kr45732.hypixeladdons.utils.structs.Note;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.kr45732.hypixeladdons.utils.Utils.gson;
import static com.kr45732.hypixeladdons.utils.config.Configuration.baseConfigPath;

public class NoteGui extends GuiScreen {

	private final GuiScreen previousScreen;
	private final List<Note> notes = new ArrayList<>();
	private int index = 0;

	public NoteGui(GuiScreen previousScreen) {
		this.previousScreen = previousScreen;

		File notesDir = getNotesDir();
		if(!notesDir.exists()){
			if(!notesDir.mkdirs()){
				HypixelAddons.INSTANCE.getLogger().error("Unable to create notes directory");
				HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen);
				return;
			}
		}

		if( notesDir.listFiles() == null){
			HypixelAddons.INSTANCE.getLogger().error("Unable to read notes directory");
			HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen);
			return;
		}

		loadNotes();
	}

	@Override
	public void initGui() {
		buttonList.addAll(
				Arrays.asList(
						new NoteButton(0, 25, 38, "New", () -> HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(new EditNoteGui(this, new Note()))),
						new NoteButton(
								1,
								25,
								68,
								"Edit",
								() -> {
									if (notes.size() > index) {
										HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(new EditNoteGui(this, notes.get(index)));
									}
								}
						),
						new NoteButton(
								2,
								25,
								98,
								"Delete",
								() -> {
									if (notes.size() > index) {
										notes.get(index).delete();
										index = Math.max(index - 1, 0);
										loadNotes();
									}
								}
						),
						new NoteButton(
								3,
								25,
								128,
								"Export Notes",
								() -> {
									List<File> notesArr = Arrays.stream(getNotesDir().listFiles()).filter(noteFile -> {
										Note note = null;
										try {
											note = gson.fromJson(new FileReader(noteFile), Note.class);
										}catch (Exception ignored){}
										return note != null;
									}).collect(Collectors.toList());

									File notesDir = new File(baseConfigPath + "/notes_export");
									if(!notesDir.exists()){
										notesDir.mkdirs();
									}
									try(FileOutputStream fos = new FileOutputStream(notesDir.getAbsolutePath() + "/notes_" + Instant.now() + ".zip")) {
										try(ZipOutputStream zipOut = new ZipOutputStream(fos)) {
											for (File noteFile : notesArr) {
												try(FileInputStream fis = new FileInputStream(noteFile)) {
													ZipEntry zipEntry = new ZipEntry(noteFile.getName());
													zipOut.putNextEntry(zipEntry);

													byte[] bytes = new byte[1024];
													int length;
													while ((length = fis.read(bytes)) >= 0) {
														zipOut.write(bytes, 0, length);
													}
												}
											}
										}
									}catch (Exception e){
										HypixelAddons.INSTANCE.getLogger().error("Exception while compressing notes to a ZIP", e);
										return;
									}

									try {
										Desktop.getDesktop().open(notesDir);
									} catch (Exception e) {
										HypixelAddons.INSTANCE.getLogger().error("Exception while opening notes ZIP", e);
									}
								}
						),
						new NoteButton(4, 25, height - 30, "Cancel", () -> {
							if (previousScreen != null) {
								HypixelAddons.INSTANCE.getEventListener().setGuiToOpen(previousScreen);
							} else {
								mc.displayGuiScreen(null);
							}
						})
				)
		);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(fontRendererObj, "Notes", width / 2 + 60, 15, 0xFF28709e);
		if(notes.size() == 0){
			int maxWidth = width - 10;
			drawRect(135, 38, maxWidth, 76, 0xFF303b52);
			drawRect(135, 38, maxWidth, 56, 0xFF212939);
			fontRendererObj.drawString("No notes", 140, 43, 0xFFffffff);
			fontRendererObj.drawString("Click the 'new' button to create a note!", 140, 62, 0xFF808080);
		}else {
			for (int i = 0; i < notes.size(); i++) {
				Note note = notes.get(i);
				int translateValue = i * 45;
				int maxWidth = width - 10;

				if (i == index) {
					drawRect(133, 36 + translateValue, maxWidth + 2, 78 + translateValue, 0xFFffffff);
				}
				drawRect(135, 38 + translateValue, maxWidth, 76 + translateValue, 0xFF303b52);
				drawRect(135, 38 + translateValue, maxWidth, 56 + translateValue, 0xFF212939);

				String title = note.getTitle();
				String titleTrimmed = fontRendererObj.trimStringToWidth(title, maxWidth - 130);
				if (!title.equals(titleTrimmed)) {
					title = titleTrimmed.substring(0, titleTrimmed.length() - 5) + "...";
				}
				String desc = note.getDescription();
				String descTrimmed = fontRendererObj.trimStringToWidth(desc, maxWidth - 130);
				if (!desc.equals(descTrimmed)) {
					desc = descTrimmed.substring(0, descTrimmed.length() - 5) + "...";
				}
				fontRendererObj.drawString(title, 140, 43 + translateValue, 0xFFffffff);
				fontRendererObj.drawString(desc, 140, 62 + translateValue, 0xFF808080);
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
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
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		switch (keyCode) {
			case 200: // Up arrow
				index = Math.max(index - 1, 0);
				break;
			case 208: // Down arrow
				index = Math.max(Math.min(index + 1, notes.size() - 1), 0);
				break;
		}

		super.keyTyped(typedChar, keyCode);
	}

	public void loadNotes(){
		notes.clear();
		for (File noteFile : getNotesDir().listFiles()) {
			try {
				notes.add(gson.fromJson(new FileReader(noteFile), Note.class));
			} catch (FileNotFoundException e) {
				HypixelAddons.INSTANCE.getLogger().error("Unable to read note: " + noteFile.getPath(), e);
			}
		}

		notes.sort(Comparator.comparingLong(Note::getLastModified).reversed());
	}
	
	public static File getNotesDir(){
		return new File(baseConfigPath + "notes/");
	}
}
