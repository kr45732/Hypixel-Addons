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

package com.kr45732.hypixeladdons.chatcommand;

import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatCommandListener {

	private final List<ChatCommand> chatCommands;
	private final HashMap<String, OffsetDateTime> cooldowns;
	private final Pattern guildChatRegex = Pattern.compile("Guild > (?:\\[.*?] )?(\\w++).*?: (.*)");

	public ChatCommandListener() {
		this.chatCommands = new ArrayList<>();
		this.cooldowns = new HashMap<>();
	}

	public ChatCommandListener addChatCommands(ChatCommand... commands) {
		chatCommands.addAll(Arrays.asList(commands));
		return this;
	}

	@SubscribeEvent
	public void onClientChatReceivedEvent(ClientChatReceivedEvent event) {
		// TODO: Cancel reply event message?
		if (ConfigUtils.toggleGuildChatResponder && Utils.isOnHypixel()) {
			String formattedMessage = event.message.getFormattedText();
			Matcher guildChatMatcher = guildChatRegex.matcher(StringUtils.stripControlCodes(event.message.getUnformattedText()));
			if (formattedMessage.startsWith("" + C.RESET + C.DARK_GREEN + "Guild > ") && guildChatMatcher.find()) {
				String sender = guildChatMatcher.group(1);
				String message = guildChatMatcher.group(2);

				if (message.startsWith("/hpa:")) {
					String[] args = message.substring(1).split(" ", 2);
					if (args.length > 0) {
						chatCommands
							.stream()
							.filter(command -> command.isForCommand(args[0]))
							.findFirst()
							.ifPresent(
								matchedCommand ->
									matchedCommand.execute(
										new ChatCommandEvent(event, args.length == 2 ? args[1].split(" ") : new String[] { sender }, sender)
									)
							);
					}
				}
			}
		}
		/*
		 String message = event.message.getUnformattedText();
		 String sender = "CrypticPlasma";
		 System.out.println(message);
		 if (SettingsStorage.toggleGuildChatResponder && message.startsWith("<CrypticPlasma> _")) {
		 	String[] args = message.split("<CrypticPlasma> _")[1].split(" ", 2);
		 	System.out.println(Arrays.toString(args));
		 	if (args.length > 0) {
		 		chatCommands
		 				.stream()
		 				.filter(command -> command.isForCommand(args[0]))
		 				.findFirst()
		 				.ifPresent(
		 						matchedCommand ->
		 								matchedCommand.execute(
		 										new ChatCommandEvent(event, args.length == 2 ? args[1].split(" ") : new String[]{sender}, sender)
		 								)
		 				);
		 	}
		 }
		*/
	}

	public int getRemainingCooldown(String name) {
		if (cooldowns.containsKey(name)) {
			int time = (int) Math.ceil(OffsetDateTime.now().until(cooldowns.get(name), ChronoUnit.MILLIS) / 1000D);
			if (time <= 0) {
				cooldowns.remove(name);
				return 0;
			}
			return time;
		}
		return 0;
	}

	public void putCooldown(String name, int seconds) {
		cooldowns.put(name, OffsetDateTime.now().plusSeconds(seconds));
	}
}
