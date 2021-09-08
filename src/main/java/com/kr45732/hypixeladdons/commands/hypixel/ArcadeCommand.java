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

package com.kr45732.hypixeladdons.commands.hypixel;

import com.kr45732.hypixeladdons.utils.Utils;
import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class ArcadeCommand extends CommandBase {

	public static ArcadeCommand INSTANCE = new ArcadeCommand();

	public static IChatComponent getArcadeString(String[] args) {
		if (args.length != 1) {
			return Utils.getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return Utils.invalidKey();
		}

		HypixelPlayer player = new HypixelPlayer(args[0]);
		if (!player.isValid()) {
			return Utils.getFailCause(player);
		}

		IChatComponent output = Utils.empty().appendSibling(player.getLink());
		output
			.appendText(
				"\n\n" +
				Utils.labelWithDesc(
					"Arcade coins",
					player.getArcadeStatIntFormatted("coins") +
					"\n" +
					Utils.labelWithDesc("Total wins", player.getPropertyFormatted("achievements.arcade_arcade_winner"))
				)
			)
			.appendSibling(
				getGame(
					"Blocking Dead",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_dayone")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("kills_dayone")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Headshots", player.getArcadeStatIntFormatted("headshots_dayone"))
				)
			)
			.appendSibling(
				getGame(
					"Bounty Hunters",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_oneinthequiver")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("kills_oneinthequiver")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Deaths", player.getArcadeStatIntFormatted("deaths_oneinthequiver")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Kill/death ratio",
						Utils.roundAndFormat(
							Utils.divide(player.getArcadeStatInt("kills_oneinthequiver"), player.getArcadeStatInt("deaths_oneinthequiver"))
						)
					)
				)
			)
			.appendSibling(
				getGame(
					"Capture The Wool",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getPropertyFormatted("achievements.arcade_ctw_slayer")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Wool captures", player.getPropertyFormatted("achievements.arcade_ctw_oh_sheep"))
				)
			)
			.appendSibling(
				getGame("Creeper Attack", Utils.arrow() + Utils.labelWithDesc("Best wave", player.getArcadeStatIntFormatted("max_wave")))
			)
			.appendSibling(
				getGame(
					"Dragon Wars",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_dragonwars2")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("kills_dragonwars2"))
				)
			)
			.appendSibling(
				getGame(
					"Easter Simulator",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_easter_simulator")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Eggs found", player.getArcadeStatIntFormatted("eggs_found_easter_simulator"))
				)
			)
			.appendSibling(
				getGame("Ender Spleef", Utils.arrow() + Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_ender")))
			)
			.appendSibling(
				getGame(
					"Farm Hunt",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_farm_hunt")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Poop collected", player.getArcadeStatIntFormatted("poop_collected"))
				)
			)
			.appendSibling(
				getGame(
					"Football",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_soccer")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Goals", player.getArcadeStatIntFormatted("goals_soccer")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kicks", player.getArcadeStatIntFormatted("kicks_soccer")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Powerkicks", player.getArcadeStatIntFormatted("powerkicks_soccer"))
				)
			)
			.appendSibling(
				getGame(
					"Galaxy Wars",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("sw_game_wins")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("sw_kills")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Empire kills", player.getArcadeStatIntFormatted("sw_empire_kills")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Rebel kills", player.getArcadeStatIntFormatted("sw_rebel_kills")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Rebel kills", player.getArcadeStatIntFormatted("sw_rebel_kills")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Deaths", player.getArcadeStatIntFormatted("sw_deaths")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Kill/death ratio",
						Utils.roundAndFormat(Utils.divide(player.getArcadeStatInt("sw_kills"), player.getArcadeStatInt("sw_deaths")))
					) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Shots fired", player.getArcadeStatIntFormatted("sw_shots_fired"))
				)
			)
			.appendSibling(
				getGame(
					"Grinch Simulator v2",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_grinch_simulator_v2")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Presents stolen", player.getArcadeStatIntFormatted("gifts_grinch_simulator_v2"))
				)
			)
			.appendSibling(
				getGame(
					"Halloween Simulator",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", "where is this located") +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Candy found", "where is this located")
				)
			)
			.appendSibling(
				getGame(
					"Hide and Seek",
					Utils.arrow() +
					Utils.labelWithDesc("Wins as seeker", "where is this located") +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Wins as hider", "where is this located")
				)
			)
			.appendSibling(
				getGame(
					"Hole in the Wall",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_hole_in_the_wall")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Highest score qualifications", player.getArcadeStatIntFormatted("hitw_record_q")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Highest score finals", player.getArcadeStatIntFormatted("hitw_record_f"))
				)
			)
			.appendSibling(
				getGame(
					"Hypixel Says",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_simon_says")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Rounds", player.getArcadeStatIntFormatted("rounds_simon_says"))
				)
			)
			.appendSibling(
				getGame(
					"Party Games",
					Utils.arrow() +
					Utils.labelWithDesc(
						"Wins",
						Utils.formatNumber(
							player.getArcadeStatInt("wins_party") +
							player.getArcadeStatInt("wins_party_2") +
							player.getArcadeStatInt("wins_party_3")
						)
					)
				)
			)
			.appendSibling(getGame("Pixel Painters", Utils.arrow() + Utils.labelWithDesc("Wins", "where is this located")))
			.appendSibling(
				getGame(
					"Santa Simulator",
					Utils.arrow() +
					Utils.labelWithDesc("Presents delivered", player.getArcadeStatIntFormatted("delivered_santa_simulator")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Times spotted", player.getArcadeStatIntFormatted("spotted_santa_simulator"))
				)
			)
			.appendSibling(
				getGame(
					"Scuba Simulator",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_scuba_simulator")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Items found", player.getArcadeStatIntFormatted("items_found_scuba_simulator")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Total points", player.getArcadeStatIntFormatted("total_points_scuba_simulator"))
				)
			)
			.appendSibling(
				getGame(
					"Throw Out",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_throw_out")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("kills_throw_out")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Deaths", player.getArcadeStatIntFormatted("deaths_throw_out")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Kill/death ratio",
						Utils.roundAndFormat(
							Utils.divide(player.getArcadeStatInt("kills_throw_out"), player.getArcadeStatInt("deaths_throw_out"))
						)
					)
				)
			)
			.appendSibling(
				getGame(
					"Mini Walls",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", player.getArcadeStatIntFormatted("wins_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kit", player.getArcadeStatStr("miniwalls_activeKit")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Withers Killed", player.getArcadeStatIntFormatted("wither_kills_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Kills", player.getArcadeStatIntFormatted("kills_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Final kills", player.getArcadeStatIntFormatted("final_kills_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Deaths", player.getArcadeStatIntFormatted("deaths_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Kill/death ratio",
						Utils.roundAndFormat(
							Utils.divide(player.getArcadeStatInt("kills_mini_walls"), player.getArcadeStatInt("deaths_mini_walls"))
						)
					) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Arrows hit", player.getArcadeStatIntFormatted("arrows_hit_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Arrows shot", player.getArcadeStatIntFormatted("arrows_shot_mini_walls")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Arrow hit accuracy",
						Utils.roundAndFormat(
							Utils.divide(
								player.getArcadeStatInt("arrows_hit_mini_walls"),
								player.getArcadeStatInt("arrows_shot_mini_walls")
							)
						)
					)
				)
			)
			.appendSibling(
				getGame(
					"Zombies",
					Utils.arrow() +
					Utils.labelWithDesc("Wins", "wins_zombies") +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Rounds survived", player.getArcadeStatStr("total_rounds_survived_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Best round", player.getArcadeStatIntFormatted("best_round_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Zombies killed", player.getArcadeStatIntFormatted("zombie_kills_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Bullets hit", player.getArcadeStatIntFormatted("bullets_hit_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Bullets shot", player.getArcadeStatIntFormatted("bullets_shot_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Bullets hit accuracy",
						Utils.roundAndFormat(
							Utils.divide(player.getArcadeStatInt("bullets_hit_zombies"), player.getArcadeStatInt("bullets_shot_zombies"))
						)
					) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Headshots", player.getArcadeStatIntFormatted("headshots_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc(
						"Headshot accuracy",
						Utils.roundAndFormat(
							Utils.divide(player.getArcadeStatInt("headshots_zombies"), player.getArcadeStatInt("bullets_hit_zombies"))
						)
					) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Players revived", player.getArcadeStatIntFormatted("players_revived_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Times knocked down", player.getArcadeStatIntFormatted("times_knocked_down_zombies")) +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Doors opened", "doors_opened_zombies") +
					"\n" +
					Utils.arrow() +
					Utils.labelWithDesc("Windows repaired", player.getArcadeStatIntFormatted("windows_repaired_zombies"))
				)
			);

		return Utils.wrapText(output);
	}

	public static IChatComponent getGame(String name, String desc) {
		return new ChatText("\n" + Utils.arrow() + Utils.label(name)).setHoverEvent(name, desc).build();
	}

	@Override
	public String getCommandName() {
		return "hpa:arcade";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <player>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		Utils.executor.submit(() -> sender.addChatMessage(getArcadeString(args)));
	}
}
