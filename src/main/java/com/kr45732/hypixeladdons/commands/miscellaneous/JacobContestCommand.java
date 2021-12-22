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

package com.kr45732.hypixeladdons.commands.miscellaneous;

import static com.kr45732.hypixeladdons.utils.Utils.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.apache.http.message.BasicHeader;

public class JacobContestCommand extends CommandBase {

	private static boolean enable;
	private static JsonArray contestsJson;
	private static List<String> missingSeasons;
	private static boolean keyPressed;
	private static int year = 0;

	public JacobContestCommand() {
		reset();
	}

	@Override
	public String getCommandName() {
		return "hpa:jacob";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> {
			if (args.length >= 1) {
				switch (args[0]) {
					case "start":
						reset();
						enable = true;
						sender.addChatMessage(
							wrapText(
								"Started processing. To start, open the full calender GUI, go to the first page, press 'h', and cycle through all pages. Use hpa:jacob reset to force stop and reset"
							)
						);
						return;
					case "send":
						sender.addChatMessage(wrapText("POSTing data..."));
						JsonObject out = new JsonObject();
						out.addProperty("year", year);
						out.add("contests", contestsJson);
						JsonElement responseJson = postJson(
							"https://skyblock-plus.ml/api/public/post/jacob",
							out,
							new BasicHeader("key", ConfigUtils.jacobKey)
						);
						if (higherDepth(responseJson, "success", false)) {
							sender.addChatMessage(wrapText("Successfully POSTed jacob data & reset jacob tracker"));
							reset();
						} else {
							sender.addChatMessage(wrapText("Failed to POST jacob data"));
						}
						return;
					case "reset":
						reset();
						sender.addChatMessage(wrapText("Reset jacob tracking"));
						return;
				}
			}

			sender.addChatMessage(getFailCause("No subcommand from 'start', 'send', or 'rest' provided"));
		});
	}

	public static void processGui() {
		if (!enable) {
			return;
		}

		if (!isOnSkyblock() || !(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) {
			return;
		}

		if (!keyPressed) {
			return;
		}

		String containerName =
			((ContainerChest) ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots).getLowerChestInventory()
				.getDisplayName()
				.getUnformattedText();

		String[] seasonSplit = containerName.split(", Year ");
		if (seasonSplit.length == 2 && missingSeasons.contains(seasonSplit[0])) {
			missingSeasons.remove(seasonSplit[0]);
			year = Integer.parseInt(seasonSplit[1]);

			for (Slot slot : ((GuiChest) Minecraft.getMinecraft().currentScreen).inventorySlots.inventorySlots) {
				NBTTagList lore;
				try {
					lore = slot.getStack().getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
				} catch (Exception e) {
					continue;
				}

				for (int i = 0; i < lore.tagCount(); i++) {
					Pattern pattern = Pattern.compile("Jacob's Farming Contest \\((.+)\\)");
					Matcher match = pattern.matcher(StringUtils.stripControlCodes(lore.getStringTagAt(i)));
					if (match.find()) {
						long time;
						if (contestsJson.size() > 0) {
							time = higherDepth(contestsJson.get(contestsJson.size() - 1), "time", 0L) + 3600000L;
						} else {
							System.out.println(parseTime(match.group(1)));
							time = Instant.now().plusSeconds(parseTime(match.group(1))).toEpochMilli();
						}

						JsonObject contestJson = new JsonObject();
						contestJson.addProperty("time", time);
						JsonArray crops = new JsonArray();
						for (int j = i + 1; j < i + 4; j++) {
							crops.add(new JsonPrimitive(StringUtils.stripControlCodes(lore.getStringTagAt(j)).substring(2)));
						}
						contestJson.add("crops", crops);
						contestsJson.add(contestJson);
						break;
					}
				}
			}
		}

		if (missingSeasons.size() == 0) {
			Minecraft
				.getMinecraft()
				.thePlayer.addChatMessage(wrapText("Finished processing all seasons. Run hpa:jacob send to send the data"));
			enable = false;
		}
	}

	public static void keyHPressed() {
		if (enable) {
			keyPressed = true;
			Minecraft
				.getMinecraft()
				.thePlayer.addChatMessage(wrapText("H key press detected... you may now start scrolling through the pages"));
			processGui();
		}
	}

	private static long parseTime(String timeStr) {
		long seconds = 0;
		for (String s : timeStr.split(" ")) {
			long num = Integer.parseInt(s.substring(0, s.length() - 1));
			switch (s.charAt(s.length() - 1)) {
				case 'h':
					num *= 60;
				case 'm':
					num *= 60;
			}
			seconds += num;
		}
		return seconds;
	}

	private static void reset() {
		contestsJson = new JsonArray();
		missingSeasons =
			new ArrayList<>(
				Arrays.asList(
					"Early Spring",
					"Spring",
					"Late Spring",
					"Early Summer",
					"Summer",
					"Late Summer",
					"Early Autumn",
					"Autumn",
					"Late Autumn",
					"Early Winter",
					"Winter",
					"Late Winter"
				)
			);
		keyPressed = false;
		enable = false;
	}
}
