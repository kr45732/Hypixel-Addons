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

package com.kr45732.hypixeladdons.utils.chat;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

public class ChatText {

	private final String text;
	private final ChatStyle style;

	public ChatText(String text) {
		this.text = text;
		this.style = new ChatStyle();
	}

	public ChatText setClickEvent(ClickEvent.Action action, String value) {
		style.setChatClickEvent(new ClickEvent(action, value));
		return this;
	}

	public ChatText setHoverEvent(String value) {
		return this.setHoverEvent(new ChatComponentText(value));
	}

	public ChatText setHoverEvent(String title, String value) {
		return this.setHoverEvent(new ChatComponentText("" + C.DARK_GREEN + C.BOLD + title + "\n" + value));
	}

	public ChatText setHoverEvent(IChatComponent value) {
		return this.setHoverEvent(HoverEvent.Action.SHOW_TEXT, value);
	}

	public ChatText setHoverEvent(HoverEvent.Action action, IChatComponent value) {
		style.setChatHoverEvent(new HoverEvent(action, value));
		return this;
	}

	public IChatComponent build() {
		return new ChatComponentText(text).setChatStyle(style);
	}
}
