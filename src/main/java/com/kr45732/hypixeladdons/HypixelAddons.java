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

package com.kr45732.hypixeladdons;

import com.kr45732.hypixeladdons.chatcommand.ChatCommand;
import com.kr45732.hypixeladdons.chatcommand.ChatCommandListener;
import com.kr45732.hypixeladdons.commands.config.SetKeyCommand;
import com.kr45732.hypixeladdons.commands.config.SettingsCommand;
import com.kr45732.hypixeladdons.commands.dungeons.DungeonsCommand;
import com.kr45732.hypixeladdons.commands.dungeons.EssenceCommand;
import com.kr45732.hypixeladdons.commands.dungeons.PartyFinderCommand;
import com.kr45732.hypixeladdons.commands.guild.GuildCommand;
import com.kr45732.hypixeladdons.commands.guild.MOTDCommand;
import com.kr45732.hypixeladdons.commands.hypixel.*;
import com.kr45732.hypixeladdons.commands.miscellaneous.*;
import com.kr45732.hypixeladdons.commands.price.*;
import com.kr45732.hypixeladdons.commands.skills.HotmCommand;
import com.kr45732.hypixeladdons.commands.skills.SkillsCommand;
import com.kr45732.hypixeladdons.commands.slayer.SlayerCommand;
import com.kr45732.hypixeladdons.listeners.EventListener;
import com.kr45732.hypixeladdons.utils.Constants;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = HypixelAddons.MOD_ID, name = HypixelAddons.MOD_NAME, version = HypixelAddons.VERSION, clientSideOnly = true)
public class HypixelAddons {

	public static final String MOD_ID = "hypixeladdons";
	public static final String MOD_NAME = "Hypixel Addons";
	public static final String VERSION = "0.0.4";

	public static HypixelAddons INSTANCE;
	private final Logger logger;
	private final ChatCommandListener chatCommandListener;
	private final EventListener eventListener;

	public HypixelAddons() {
		HypixelAddons.INSTANCE = this;
		this.logger = LogManager.getLogger("HypixelAddons");
		this.chatCommandListener =
			new ChatCommandListener()
				.addChatCommands(
					new ChatCommand(SlayerCommand.INSTANCE, event -> SlayerCommand.INSTANCE.getSlayerChat(event.getArgs())),
					new ChatCommand(SkillsCommand.INSTANCE, event -> SkillsCommand.INSTANCE.getSkillsChat(event.getArgs())),
					new ChatCommand(AuctionsCommand.INSTANCE, event -> AuctionsCommand.INSTANCE.getAuctionsChat(event.getArgs())),
					new ChatCommand(BazaarCommand.INSTANCE, event -> BazaarCommand.INSTANCE.getBazaarChat(event.getArgs())),
					new ChatCommand(BinCommand.INSTANCE, event -> BinCommand.INSTANCE.getBinChat(event.getArgs())),
					new ChatCommand(BitsCommand.INSTANCE, event -> BitsCommand.INSTANCE.getBitsChat(event.getArgs())),
					new ChatCommand(BankCommand.INSTANCE, event -> BankCommand.INSTANCE.getBankChat(event.getArgs())),
					new ChatCommand(WeightCommand.INSTANCE, event -> WeightCommand.INSTANCE.getWeightChat(event.getArgs())),
					new ChatCommand(DungeonsCommand.INSTANCE, event -> DungeonsCommand.INSTANCE.getDungeonsChat(event.getArgs())),
					new ChatCommand(PartyFinderCommand.INSTANCE, event -> PartyFinderCommand.INSTANCE.getPartyFinderChat(event.getArgs())),
					new ChatCommand(GuildCommand.INSTANCE, event -> GuildCommand.INSTANCE.getGuildChat(event.getArgs())),
					new ChatCommand(SkywarsCommand.INSTANCE, event -> SkywarsCommand.INSTANCE.getSkywarsChat(event.getArgs())),
					new ChatCommand(BedwarsCommand.INSTANCE, event -> BedwarsCommand.INSTANCE.getBedwarsChat(event.getArgs())),
					new ChatCommand(HypixelCommand.INSTANCE, event -> HypixelCommand.INSTANCE.getHypixelChat(event.getArgs()))
				);
		this.eventListener = new EventListener();
	}

	@Mod.EventHandler
	public void onFMLPreInitializationEvent(FMLPreInitializationEvent event) {
		for (CommandBase command : getCommands()) {
			ClientCommandHandler.instance.registerCommand(command);
		}
		HelpCommand.initialize();
	}

	@Mod.EventHandler
	public void onFMLInitializationEvent(FMLInitializationEvent ev) {
		MinecraftForge.EVENT_BUS.register(chatCommandListener);
		MinecraftForge.EVENT_BUS.register(eventListener);

		Constants.initialize();
		ConfigUtils.initialize();
	}

	public CommandBase[] getCommands() {
		return new CommandBase[] {
			// Mod settings
			new SetKeyCommand(),
			new SettingsCommand(),
			// Slayer
			SlayerCommand.INSTANCE,
			// Skills
			SkillsCommand.INSTANCE,
			HotmCommand.INSTANCE,
			// Dungeons
			DungeonsCommand.INSTANCE,
			EssenceCommand.INSTANCE,
			PartyFinderCommand.INSTANCE,
			// Guild
			GuildCommand.INSTANCE,
			new MOTDCommand(),
			// Price
			AuctionsCommand.INSTANCE,
			BazaarCommand.INSTANCE,
			BinCommand.INSTANCE,
			BidsCommand.INSTANCE,
			BitsCommand.INSTANCE,
			// Hypixel
			HypixelCommand.INSTANCE,
			BedwarsCommand.INSTANCE,
			SkywarsCommand.INSTANCE,
			ArcadeCommand.INSTANCE,
			ArenaCommand.INSTANCE,
			DuelsCommand.INSTANCE,
			MurderMysteryCommand.INSTANCE,
			BuildBattleCommand.INSTANCE,
			// Miscellaneous
			BankCommand.INSTANCE,
			WeightCommand.INSTANCE,
			SkyblockCommand.INSTANCE,
			CakesCommand.INSTANCE,
			HarpCommand.INSTANCE,
			new NotesCommand(),
			new HelpCommand(),
			new JacobContestCommand(),
			// Don't run this command, ok?
			new DevCommand(),
		};
	}

	public Logger getLogger() {
		return logger;
	}

	public ChatCommandListener getChatCommandListener() {
		return chatCommandListener;
	}

	public EventListener getEventListener() {
		return eventListener;
	}
}
