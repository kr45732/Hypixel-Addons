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

import static com.kr45732.hypixeladdons.utils.Utils.executor;

import com.kr45732.hypixeladdons.HypixelAddons;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandBase;

public class ChatCommand {

	private final String name;
	private final List<String> aliases;
	private final Function<ChatCommandEvent, String> execute;

	public ChatCommand(CommandBase command, Function<ChatCommandEvent, String> execute) {
		this.name = command.getCommandName();
		this.aliases = command.getCommandAliases();
		this.execute = execute;
	}

	public void execute(ChatCommandEvent event) {
		executor.submit(() -> {
			int remainingCooldown = getRemainingCooldown(event.getSender());
			if (remainingCooldown > 0) {
				if (ConfigUtils.toggleGuildChatCooldownMessage) {
					event.reply("This command is on cooldown for " + remainingCooldown + " more seconds");
				}
			} else {
				event.reply(execute.apply(event));
			}
		});
	}

	public boolean isForCommand(String command) {
		return name.equalsIgnoreCase(command) || aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(command));
	}

	private int getRemainingCooldown(String senderUsername) {
		String key = name + "|" + senderUsername;
		int remaining = HypixelAddons.INSTANCE.getChatCommandListener().getRemainingCooldown(key);
		if (remaining > 0) {
			return remaining;
		} else {
			HypixelAddons.INSTANCE.getChatCommandListener().setCooldown(key, ConfigUtils.guildChatCooldown);
		}

		return 0;
	}
}
