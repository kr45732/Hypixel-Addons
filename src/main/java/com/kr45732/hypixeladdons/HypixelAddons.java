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

package com.kr45732.hypixeladdons;

import com.kr45732.hypixeladdons.chatcommand.ChatCommand;
import com.kr45732.hypixeladdons.chatcommand.ChatCommandHandler;
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
	public static final String VERSION = "0.0.1";

	@Mod.Instance(MOD_ID)
	public static HypixelAddons INSTANCE;

	public final Logger logger = LogManager.getLogger("HypixelAddons");
	public ChatCommandHandler chatCommandHandler;
	public EventListener eventListener;

	@Mod.EventHandler
	public void onFMLPreInitializationEvent(FMLPreInitializationEvent event) {
		registerCommands(
			// Mod settings
			new SetKeyCommand(),
			new SettingsCommand(),
			// Slayer
			SlayerCommand.INSTANCE,
			// Skills
			SkillsCommand.INSTANCE,
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
			//	BinCommand.INSTANCE, TODO: fix?
			//	BidsCommand.INSTANCE,
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
			new TodoListCommand(),
			// Don't run this command, ok?
			new DevCommand()
		);
	}

	@Mod.EventHandler
	public void onFMLInitializationEvent(FMLInitializationEvent ev) {
		this.chatCommandHandler =
			new ChatCommandHandler()
				.addChatCommands(
					new ChatCommand(SlayerCommand.INSTANCE, event -> SlayerCommand.getSlayerChat(event.getArgs())),
					new ChatCommand(SkillsCommand.INSTANCE, event -> SkillsCommand.getSkillsChat(event.getArgs())),
					new ChatCommand(AuctionsCommand.INSTANCE, event -> AuctionsCommand.getAuctionsChat(event.getArgs())),
					new ChatCommand(BazaarCommand.INSTANCE, event -> BazaarCommand.getBazaarChat(event.getArgs())),
					//	new ChatCommand(BidsCommand.INSTANCE, event -> BidsCommand.getBidsChat(event.getArgs())),
					//	new ChatCommand(BinCommand.INSTANCE, event -> BinCommand.getBinChat(event.getArgs())),
					new ChatCommand(BitsCommand.INSTANCE, event -> BitsCommand.getBitsChat(event.getArgs())),
					new ChatCommand(BankCommand.INSTANCE, event -> BankCommand.getBankChat(event.getArgs())),
					new ChatCommand(WeightCommand.INSTANCE, event -> WeightCommand.getWeightChat(event.getArgs())),
					new ChatCommand(DungeonsCommand.INSTANCE, event -> DungeonsCommand.getDungeonsChat(event.getArgs())),
					new ChatCommand(PartyFinderCommand.INSTANCE, event -> PartyFinderCommand.getPartyFinderChat(event.getArgs())),
					new ChatCommand(GuildCommand.INSTANCE, event -> GuildCommand.getGuildChat(event.getArgs())),
					new ChatCommand(SkywarsCommand.INSTANCE, event -> SkywarsCommand.getSkywarsChat(event.getArgs())),
					new ChatCommand(BedwarsCommand.INSTANCE, event -> BedwarsCommand.getBedwarsChat(event.getArgs())),
					new ChatCommand(HypixelCommand.INSTANCE, event -> HypixelCommand.getHypixelChat(event.getArgs()))
				);
		this.eventListener = new EventListener();

		MinecraftForge.EVENT_BUS.register(chatCommandHandler);
		MinecraftForge.EVENT_BUS.register(eventListener);

		Constants.initialize();
		ConfigUtils.initialize();
	}

	private void registerCommands(CommandBase... commands) {
		for (CommandBase command : commands) {
			ClientCommandHandler.instance.registerCommand(command);
		}
	}
}
