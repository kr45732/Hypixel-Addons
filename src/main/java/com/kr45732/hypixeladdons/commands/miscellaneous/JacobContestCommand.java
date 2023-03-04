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
import java.util.concurrent.ScheduledFuture;
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

	public static JacobContestCommand INSTANCE = new JacobContestCommand();

	private boolean enable;
	private JsonArray contestsJson;
	private List<String> missingSeasons;
	private boolean keyPressed;
	private int year = 0;
	public ScheduledFuture<?> future;

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
								"Started processing. Open the full calender GUI, go to the first page, press 'h', and cycle through all pages (when you reach the end, go one page back to finish). Use hpa:jacob reset to force stop & reset"
							)
						);
						return;
					case "send":
						sender.addChatMessage(wrapText("Sending data..."));
						JsonObject out = new JsonObject();
						out.addProperty("year", year);
						out.add("contests", contestsJson);
						JsonElement responseJson = postJson(
							ConfigUtils.jacobUrl,
							out,
							new BasicHeader("key", ConfigUtils.jacobKey)
						);
						ConfigUtils.setJacobLastYear(year);
						if (higherDepth(responseJson, "success", false)) {
							sender.addChatMessage(wrapText("Successfully sent data & reset tracker"));
							reset();
						} else {
							sender.addChatMessage(
								wrapText(
									"Failed to send jacob data with reason: " +
									higherDepth(responseJson, "cause", "No response from server")
								)
							);
						}
						return;
					case "reset":
						reset();
						sender.addChatMessage(wrapText("Reset jacob tracker"));
						return;
				}
			}

			sender.addChatMessage(getFailCause("Error, no subcommand from 'start', 'send', or 'rest' provided"));
		});
	}

	public void processOpenGui() {
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
				.thePlayer.addChatMessage(wrapText("Finished processing the entire year. Run hpa:jacob send to send the data"));
			enable = false;
		}
	}

	public void keyHPressed() {
		if (enable && !keyPressed) {
			keyPressed = true;
			Minecraft.getMinecraft().thePlayer.addChatMessage(wrapText("H key press detected - proceed to cycle through the pages"));
			processOpenGui();
		}
	}

	private long parseTime(String timeStr) {
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

	private void reset() {
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

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}
}
