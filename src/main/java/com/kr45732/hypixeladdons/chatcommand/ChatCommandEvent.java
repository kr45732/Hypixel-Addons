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

package com.kr45732.hypixeladdons.chatcommand;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ChatCommandEvent {

	private final ClientChatReceivedEvent event;
	private final String[] args;
	private final String sender;

	public ChatCommandEvent(ClientChatReceivedEvent event, String[] args, String sender) {
		this.event = event;
		this.args = args;
		this.sender = sender;
	}

	public ClientChatReceivedEvent getEvent() {
		return event;
	}

	public String[] getArgs() {
		return args;
	}

	public String getSender() {
		return sender;
	}

	public void reply(String message) {
		Minecraft.getMinecraft().thePlayer.sendChatMessage("/w " + sender + " " + message);
	}
}
