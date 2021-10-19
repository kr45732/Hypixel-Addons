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

package com.kr45732.hypixeladdons.commands.hypixel;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.HypixelPlayer.ArcadeMode.*;

import com.kr45732.hypixeladdons.utils.api.HypixelPlayer;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public class ArcadeCommand extends CommandBase {

	public static final ArcadeCommand INSTANCE = new ArcadeCommand();

	public IChatComponent getArcadeString(String[] args) {
		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		HypixelPlayer player = newHypixelPlayer(args);
		if (!player.isValid()) {
			return getFailCause(player);
		}

		IChatComponent output = player
			.defaultComponent()
			.appendText(
				"\n\n" +
				labelWithDesc(
					"Arcade coins",
					player.getArcadeCoins() +
					"\n" +
					labelWithDesc("Total wins", player.getPropertyFormatted("achievements.arcade_arcade_winner")) +
					"\n\n" +
					label("Modes")
				)
			)
			.appendSibling(
				getGame(
					"Blocking Dead",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(BLOCKING_DEAD))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(BLOCKING_DEAD))) +
					"\n" +
					arrow() +
					labelWithDesc("Headshots", formatNumber(player.getArcadeInt("headshots_dayone", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Bounty Hunters",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(BOUNTY_HUNTERS))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(BOUNTY_HUNTERS))) +
					"\n" +
					arrow() +
					labelWithDesc("Deaths", formatNumber(player.getArcadeDeaths(BOUNTY_HUNTERS))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Kill/death ratio",
						roundAndFormat(divide(player.getArcadeKills(BOUNTY_HUNTERS), player.getArcadeDeaths(BOUNTY_HUNTERS)))
					)
				)
			)
			.appendSibling(
				getGame(
					"Capture The Wool",
					arrow() +
					labelWithDesc("Wins", player.getPropertyFormatted("achievements.arcade_ctw_slayer")) +
					"\n" +
					arrow() +
					labelWithDesc("Wool captures", player.getPropertyFormatted("achievements.arcade_ctw_oh_sheep"))
				)
			)
			.appendSibling(
				getGame("Creeper Attack", arrow() + labelWithDesc("Best wave", formatNumber(player.getArcadeInt("max_wave", NONE))))
			)
			.appendSibling(
				getGame(
					"Dragon Wars",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(DRAGON_WARS))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(DRAGON_WARS)))
				)
			)
			.appendSibling(
				getGame(
					"Easter Simulator",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(EASTER_SIMULATOR))) +
					"\n" +
					arrow() +
					labelWithDesc("Eggs found", formatNumber(player.getArcadeInt("eggs_found_easter_simulator", NONE)))
				)
			)
			.appendSibling(getGame("Ender Spleef", arrow() + labelWithDesc("Wins", formatNumber(player.getArcadeWins(ENDER_SPLEEF)))))
			.appendSibling(
				getGame(
					"Farm Hunt",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(FARM_HUNT))) +
					"\n" +
					arrow() +
					labelWithDesc("Poop collected", formatNumber(player.getArcadeInt("poop_collected", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Football",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(FOOTBALL))) +
					"\n" +
					arrow() +
					labelWithDesc("Goals", formatNumber(player.getArcadeInt("goals_soccer", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Kicks", formatNumber(player.getArcadeInt("kicks_soccer", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Powerkicks", formatNumber(player.getArcadeInt("powerkicks_soccer", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Galaxy Wars",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(GALAXY_WARS))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(GALAXY_WARS))) +
					"\n" +
					arrow() +
					labelWithDesc("Empire kills", formatNumber(player.getArcadeInt("sw_empire_kills", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Rebel kills", formatNumber(player.getArcadeInt("sw_rebel_kills", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Rebel kills", formatNumber(player.getArcadeInt("sw_rebel_kills", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Deaths", formatNumber(player.getArcadeDeaths(GALAXY_WARS))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Kill/death ratio",
						roundAndFormat(divide(player.getArcadeKills(GALAXY_WARS), player.getArcadeDeaths(GALAXY_WARS)))
					) +
					"\n" +
					arrow() +
					labelWithDesc("Shots fired", formatNumber(player.getBedwarsInt("sw_shots_fired", HypixelPlayer.BedwarsMode.NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Grinch Simulator v2",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(GRINCH_SIMULATOR_V2))) +
					"\n" +
					arrow() +
					labelWithDesc("Presents stolen", formatNumber(player.getArcadeInt("gifts_grinch_simulator_v2", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Halloween Simulator",
					arrow() +
					labelWithDesc("Wins", "where is this located") +
					"\n" +
					arrow() +
					labelWithDesc("Candy found", "where is this located")
				)
			)
			.appendSibling(
				getGame(
					"Hide and Seek",
					arrow() +
					labelWithDesc("Wins as seeker", "where is this located") +
					"\n" +
					arrow() +
					labelWithDesc("Wins as hider", "where is this located")
				)
			)
			.appendSibling(
				getGame(
					"Hole in the Wall",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(HOLE_IN_THE_WALL))) +
					"\n" +
					arrow() +
					labelWithDesc("Highest score qualifications", formatNumber(player.getArcadeInt("hitw_record_q", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Highest score finals", formatNumber(player.getArcadeInt("hitw_record_f", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Hypixel Says",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(SIMON_SAYS))) +
					"\n" +
					arrow() +
					labelWithDesc("Rounds", formatNumber(player.getArcadeInt("rounds_simon_says", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Party Games",
					arrow() +
					labelWithDesc(
						"Wins",
						formatNumber(
							player.getArcadeWins(PARTY_GAMES) + player.getArcadeWins(PARTY_GAMES_2) + player.getArcadeWins(PARTY_GAMES_3)
						)
					)
				)
			)
			.appendSibling(getGame("Pixel Painters", arrow() + labelWithDesc("Wins", "where is this located")))
			.appendSibling(
				getGame(
					"Santa Simulator",
					arrow() +
					labelWithDesc("Presents delivered", formatNumber(player.getArcadeInt("delivered_santa_simulator", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Times spotted", formatNumber(player.getArcadeInt("spotted_santa_simulator", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Scuba Simulator",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(SCUBA_SIMULATOR))) +
					"\n" +
					arrow() +
					labelWithDesc("Items found", formatNumber(player.getArcadeInt("items_found_scuba_simulator", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Total points", formatNumber(player.getArcadeInt("total_points_scuba_simulator", NONE)))
				)
			)
			.appendSibling(
				getGame(
					"Throw Out",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(THROW_OUT))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(THROW_OUT))) +
					"\n" +
					arrow() +
					labelWithDesc("Deaths", formatNumber(player.getArcadeDeaths(THROW_OUT))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Kill/death ratio",
						roundAndFormat(divide(player.getArcadeKills(THROW_OUT), player.getArcadeDeaths(THROW_OUT)))
					)
				)
			)
			.appendSibling(
				getGame(
					"Mini Walls",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(MINI_WALLS))) +
					"\n" +
					arrow() +
					labelWithDesc("Kit", player.getArcadeStr("miniwalls_activeKit")) +
					"\n" +
					arrow() +
					labelWithDesc("Withers Killed", formatNumber(player.getArcadeInt("wither_kills_mini_walls", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Kills", formatNumber(player.getArcadeKills(MINI_WALLS))) +
					"\n" +
					arrow() +
					labelWithDesc("Final kills", formatNumber(player.getArcadeInt("final_kills_mini_walls", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Deaths", formatNumber(player.getArcadeDeaths(MINI_WALLS))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Kill/death ratio",
						roundAndFormat(divide(player.getArcadeKills(MINI_WALLS), player.getArcadeDeaths(MINI_WALLS)))
					) +
					"\n" +
					arrow() +
					labelWithDesc("Arrows hit", formatNumber(player.getArcadeInt("arrows_hit_mini_walls", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Arrows shot", formatNumber(player.getArcadeInt("arrows_shot_mini_walls", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Arrow hit accuracy",
						roundAndFormat(
							divide(player.getArcadeInt("arrows_hit_mini_walls", NONE), player.getArcadeInt("arrows_shot_mini_walls", NONE))
						)
					)
				)
			)
			.appendSibling(
				getGame(
					"Zombies",
					arrow() +
					labelWithDesc("Wins", formatNumber(player.getArcadeWins(ZOMBIES))) +
					"\n" +
					arrow() +
					labelWithDesc("Rounds survived", formatNumber(player.getArcadeInt("total_rounds_survived_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Best round", formatNumber(player.getArcadeInt("best_round_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Zombies killed", formatNumber(player.getArcadeInt("zombie_kills_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Bullets hit", formatNumber(player.getArcadeInt("bullets_hit_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Bullets shot", formatNumber(player.getArcadeInt("bullets_shot_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Bullets hit accuracy",
						roundAndFormat(
							divide(player.getArcadeInt("bullets_hit_zombies", NONE), player.getArcadeInt("bullets_shot_zombies", NONE))
						)
					) +
					"\n" +
					arrow() +
					labelWithDesc("Headshots", formatNumber(player.getArcadeInt("headshots_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc(
						"Headshot accuracy",
						roundAndFormat(
							divide(player.getArcadeInt("headshots_zombies", NONE), player.getArcadeInt("bullets_hit_zombies", NONE))
						)
					) +
					"\n" +
					arrow() +
					labelWithDesc("Players revived", formatNumber(player.getArcadeInt("players_revived_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Times knocked down", formatNumber(player.getArcadeInt("times_knocked_down_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Doors opened", formatNumber(player.getArcadeInt("doors_opened_zombies", NONE))) +
					"\n" +
					arrow() +
					labelWithDesc("Windows repaired", formatNumber(player.getArcadeInt("windows_repaired_zombies", NONE)))
				)
			);

		return wrapText(output);
	}

	public IChatComponent getGame(String name, String desc) {
		return new ChatText("\n" + arrow() + label(name)).setHoverEvent(name, desc).build();
	}

	@Override
	public String getCommandName() {
		return "hpa:arcade";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [player]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getArcadeString(args)));
	}
}
