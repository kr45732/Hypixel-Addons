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

package com.kr45732.hypixeladdons.utils.structs;

import com.kr45732.hypixeladdons.HypixelAddons;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.kr45732.hypixeladdons.gui.NoteGui.getNotesDir;
import static com.kr45732.hypixeladdons.utils.Utils.gson;

public class Note {

	private String uuid;
	private String title;
	private String description;
	private boolean isPinned;
	private long lastModified;

	public Note() {
		this(null, "", "", false, Instant.now().toEpochMilli());
	}

	public Note(String uuid, String title, String description, boolean isPinned, long lastModified) {
		this.uuid = uuid;
		this.title = title;
		this.description = description;
		this.isPinned = isPinned;
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean pinned) {
		isPinned = pinned;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void save() {
		if (uuid == null) {
			List<String> noteUuids = Arrays.stream(getNotesDir().listFiles()).filter(file -> file.getName().endsWith(".json")).map(file -> file.getName().replace(".json", "")).collect(Collectors.toList());
			do {
				uuid = UUID.randomUUID().toString();
			} while (noteUuids.contains(uuid));
		}

		lastModified = Instant.now().toEpochMilli();

		try (FileWriter writer =  new FileWriter(getNotesDir().getAbsolutePath() + "/" + uuid + ".json")){
			gson.toJson(this, writer);
			writer.flush();
		} catch (IOException e) {
			HypixelAddons.INSTANCE.getLogger().error("Error when saving note with UUID: " + uuid, e);
		}
	}

	public void delete() {
		if (uuid != null) {
			Arrays.stream(getNotesDir().listFiles()).filter(file -> file.getName().equals(uuid + ".json")).findFirst().ifPresent(File::delete);
		}
	}
}