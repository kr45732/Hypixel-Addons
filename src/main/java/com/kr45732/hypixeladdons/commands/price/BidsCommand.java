package com.kr45732.hypixeladdons.commands.price;

import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.usernameUuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.structs.UsernameUuidStruct;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class BidsCommand extends CommandBase {

	public static BidsCommand INSTANCE = new BidsCommand();

	public ChatComponentText getBidsString(String[] args) {
		if (args.length != 1) {
			return getUsage(INSTANCE);
		}

		UsernameUuidStruct usernameUuidStruct = usernameUuid(getUsername(args, 0));
		if (usernameUuidStruct.isNotValid()) {
			return getFailCause(usernameUuidStruct);
		}

		JsonArray bids = getBidsFromPlayer(usernameUuidStruct.getUuid());
		if (bids == null || bids.size() == 0) {
			return getFailCause("Player has no bids");
		}

		StringBuilder bidsStr = new StringBuilder();
		for (JsonElement bid : bids) {
			String auctionDesc;
			String itemName;
			boolean isPet = higherDepth(bid, "item_id").getAsString().equals("PET");

			Instant endingAt = Instant.ofEpochMilli(higherDepth(bid, "end_t").getAsLong());
			Duration duration = Duration.between(Instant.now(), endingAt);
			String timeUntil = instantToDHM(duration);

			itemName =
				(isPet ? capitalizeString(higherDepth(bid, "tier").getAsString()) + " " : "") + higherDepth(bid, "item_name").getAsString();

			JsonArray bidsArr = higherDepth(bid, "bids").getAsJsonArray();
			long highestBid = higherDepth(bidsArr, "[" + (bidsArr.size() - 1) + "].amount").getAsLong();
			if (duration.toMillis() > 0) {
				auctionDesc = desc("Current bid: " + simplifyNumber(highestBid) + " | Ending in " + timeUntil);
			} else {
				auctionDesc = desc("Auction sold for " + simplifyNumber(highestBid));
			}

			bidsStr.append("\n").append(arrow()).append(labelWithDesc(itemName, auctionDesc));
		}

		return wrapText(
			labelWithDesc("Player", usernameUuidStruct.getUsername()) +
			"\n" +
			(bidsStr.length() > 0 ? bidsStr.toString() : "Player has no bids")
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bids";
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
		executor.submit(() -> sender.addChatMessage(getBidsString(args)));
	}
}
