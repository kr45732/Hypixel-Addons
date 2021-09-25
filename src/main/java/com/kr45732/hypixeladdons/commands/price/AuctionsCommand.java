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

package com.kr45732.hypixeladdons.commands.price;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.getAuctionFromPlayer;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.usernameUuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.api.HypixelResponse;
import com.kr45732.hypixeladdons.utils.chat.C;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import com.kr45732.hypixeladdons.utils.config.ConfigUtils;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class AuctionsCommand extends CommandBase {

	public static AuctionsCommand INSTANCE = new AuctionsCommand();

	public static IChatComponent getAuctionsString(String[] args) {
		if (args.length != 1) {
			return getUsage(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKey();
		}

		UsernameUuidStruct usernameUuidStruct = usernameUuid(args[0]);
		if (usernameUuidStruct.isNotValid()) {
			return getFailCause(usernameUuidStruct);
		}

		HypixelResponse response = getAuctionFromPlayer(usernameUuidStruct.playerUuid);
		if (response.isNotValid()) {
			return getFailCause(response);
		}

		JsonArray playerAuctions = response.response.getAsJsonArray();
		StringBuilder auctionsStr = new StringBuilder();
		for (JsonElement currentAuction : playerAuctions) {
			if (!higherDepth(currentAuction, "claimed").getAsBoolean()) {
				String auction;
				boolean isPet = higherDepth(currentAuction, "item_lore").getAsString().toLowerCase().contains("pet");
				boolean bin = false;
				try {
					bin = higherDepth(currentAuction, "bin").getAsBoolean();
				} catch (NullPointerException ignored) {}

				if (higherDepth(currentAuction, "item_name").getAsString().equals("Enchanted Book")) {
					auctionsStr
						.append("\n")
						.append(arrow())
						.append(
							label(
								StringUtils.stripControlCodes(higherDepth(currentAuction, "item_lore").getAsString().split("\n")[0]) + ": "
							)
						);
				} else {
					auctionsStr
						.append("\n")
						.append(arrow())
						.append(
							label(
								(isPet ? capitalizeString(higherDepth(currentAuction, "tier").getAsString().toLowerCase()) + " " : "") +
								higherDepth(currentAuction, "item_name").getAsString() +
								": "
							)
						);
				}

				long highestBid = higherDepth(currentAuction, "highest_bid_amount").getAsInt();
				long startingBid = higherDepth(currentAuction, "starting_bid").getAsInt();

				Instant endingAt = Instant.ofEpochMilli(higherDepth(currentAuction, "end").getAsLong());
				Duration duration = Duration.between(Instant.now(), endingAt);
				String timeUntil = instantToDHM(duration);
				if (duration.toMillis() > 0) {
					if (bin) {
						auction = "BIN: " + simplifyNumber(startingBid);
					} else {
						auction = "Current bid: " + simplifyNumber(highestBid);
					}
					auction += " | Ending in " + timeUntil;
				} else {
					if (highestBid >= startingBid) {
						auction = "Auction sold for " + simplifyNumber(highestBid);
					} else {
						auction = "Auction did not sell";
					}
				}
				auctionsStr.append(desc(auction));
			}
		}

		return wrapText(
			empty()
				.appendSibling(
					new ChatText(labelWithDesc("Player", C.UNDERLINE + usernameUuidStruct.playerUsername))
						.setClickEvent(ClickEvent.Action.OPEN_URL, skyblockStatsLink(usernameUuidStruct.playerUsername, null))
						.build()
				)
				.appendText("\n" + (auctionsStr.length() > 0 ? auctionsStr.toString() : "No active auctions"))
		);
	}

	public static String getAuctionsChat(String[] args) {
		if (args.length != 1) {
			return getUsageChat(INSTANCE);
		}

		if (ConfigUtils.getHypixelKey() == null) {
			return invalidKeyChat();
		}

		UsernameUuidStruct usernameUuidStruct = usernameUuid(args[0]);
		if (usernameUuidStruct.isNotValid()) {
			return getFailCauseChat(usernameUuidStruct);
		}

		HypixelResponse response = getAuctionFromPlayer(usernameUuidStruct.playerUuid);
		if (response.isNotValid()) {
			return getFailCauseChat(response);
		}

		int sold = 0;
		int running = 0;
		int didNotSell = 0;

		for (JsonElement currentAuction : response.response.getAsJsonArray()) {
			if (!higherDepth(currentAuction, "claimed").getAsBoolean()) {
				long highestBid = higherDepth(currentAuction, "highest_bid_amount").getAsInt();
				long startingBid = higherDepth(currentAuction, "starting_bid").getAsInt();
				Duration duration = Duration.between(Instant.now(), Instant.ofEpochMilli(higherDepth(currentAuction, "end").getAsLong()));

				if (duration.toMillis() > 0) {
					running++;
				} else {
					if (highestBid >= startingBid) {
						sold++;
					} else {
						didNotSell++;
					}
				}
			}
		}

		if (sold + running + didNotSell == 0) {
			return usernameUuidStruct.playerUsername + " has no active auctions";
		}

		String output =
			usernameUuidStruct.playerUsername +
			" has " +
			(running > 0 ? running + " running auctions, " : "") +
			(sold > 0 ? sold + " sold auctions, " : "") +
			(didNotSell > 0 ? didNotSell + " auctions that failed to sell" : "");
		output = output.endsWith(", ") ? output.substring(0, output.length() - 2) : output;

		return output;
	}

	@Override
	public String getCommandName() {
		return "hpa:auctions";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:auction");
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
		executor.submit(() -> sender.addChatMessage(getAuctionsString(args)));
	}
}
