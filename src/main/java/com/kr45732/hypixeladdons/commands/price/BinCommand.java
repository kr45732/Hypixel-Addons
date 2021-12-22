package com.kr45732.hypixeladdons.commands.price;

import static com.kr45732.hypixeladdons.utils.Constants.*;
import static com.kr45732.hypixeladdons.utils.Utils.*;
import static com.kr45732.hypixeladdons.utils.api.ApiHandler.usernameUuid;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kr45732.hypixeladdons.utils.chat.ChatText;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class BinCommand extends CommandBase {

	public static BinCommand INSTANCE = new BinCommand();

	public IChatComponent getBinString(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return getUsage(INSTANCE);
		}

		String query = args[0];
		JsonArray lowestBinArr = null;
		String tempName = null;
		for (String enchantId : ENCHANT_NAMES) {
			if (query.replace(" ", "_").toUpperCase().contains(enchantId)) {
				int enchantLevel;
				try {
					enchantLevel = Integer.parseInt(query.replaceAll("\\D+", "").trim());
				} catch (NumberFormatException e) {
					enchantLevel = 1;
				}

				lowestBinArr = queryLowestBinEnchant(enchantId, enchantLevel);
				if (lowestBinArr == null) {
					return getFailCause("Error fetching auctions data");
				}
				tempName = idToName(enchantId + ";" + enchantLevel);
				break;
			}
		}

		if (lowestBinArr == null) {
			for (String pet : PET_NAMES) {
				if (query.replace(" ", "_").toUpperCase().contains(pet)) {
					query = query.toLowerCase();

					String rarity = "ANY";
					for (String rarityName : RARITY_TO_NUMBER_MAP.keySet()) {
						if (query.contains(rarityName.toLowerCase())) {
							rarity = rarityName;
							query = query.replace(rarityName.toLowerCase(), "").trim().replaceAll("\\s+", " ");
							break;
						}
					}

					lowestBinArr = queryLowestBinPet(query, rarity);
					if (lowestBinArr == null) {
						return getFailCause("Error fetching auctions data");
					}
					break;
				}
			}
		}

		if (lowestBinArr == null) {
			lowestBinArr = queryLowestBin(query);
			if (lowestBinArr == null) {
				return getFailCause("Error fetching auctions data");
			}
		}

		if (lowestBinArr.size() == 0) {
			return getFailCause("No bins matching '" + query + "' found");
		}

		JsonElement lowestBinJson = lowestBinArr.get(0);
		Duration duration = Duration.between(Instant.now(), Instant.ofEpochMilli(higherDepth(lowestBinJson, "end").getAsLong()));
		IChatComponent lowestBinStr = new ChatComponentText(
			labelWithDesc("Name", (tempName == null ? higherDepth(lowestBinJson, "item_name").getAsString() : tempName)) +
			(
				higherDepth(lowestBinJson, "item_id").getAsString().equals("PET")
					? "\n" + labelWithDesc("Rarity", capitalizeString(higherDepth(lowestBinJson, "tier").getAsString()))
					: ""
			) +
			"\n" +
			labelWithDesc("Price", simplifyNumber(higherDepth(lowestBinJson, "starting_bid").getAsDouble())) +
			"\n" +
			labelWithDesc("Seller", usernameUuid(higherDepth(lowestBinJson, "auctioneer").getAsString()).getUsername())
		)
			.appendSibling(
				new ChatText("\n" + labelWithDesc("Auction", "/viewauction " + higherDepth(lowestBinJson, "uuid").getAsString()))
					.setClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewauction " + higherDepth(lowestBinJson, "uuid").getAsString())
					.build()
			)
			.appendText("\n" + labelWithDesc("Ends in", instantToDHM(duration)));
		return wrapText(lowestBinStr);
	}

	public String getBinChat(String[] args) {
		args = String.join(" ", args).split(" ", 1);
		if (args.length != 1 || args[0].length() == 0) {
			return getUsageChat(INSTANCE);
		}

		String query = args[0];
		JsonArray lowestBinArr = null;
		String tempName = null;
		for (String enchantId : ENCHANT_NAMES) {
			if (query.replace(" ", "_").toUpperCase().contains(enchantId)) {
				int enchantLevel;
				try {
					enchantLevel = Integer.parseInt(query.replaceAll("\\D+", "").trim());
				} catch (NumberFormatException e) {
					enchantLevel = 1;
				}

				lowestBinArr = queryLowestBinEnchant(enchantId, enchantLevel);
				if (lowestBinArr == null) {
					return getFailCauseChat("Error fetching auctions data");
				}
				tempName = idToName(enchantId + ";" + enchantLevel);
				break;
			}
		}

		if (lowestBinArr == null) {
			for (String pet : PET_NAMES) {
				if (query.replace(" ", "_").toUpperCase().contains(pet)) {
					query = query.toLowerCase();

					String rarity = "ANY";
					for (String rarityName : RARITY_TO_NUMBER_MAP.keySet()) {
						if (query.contains(rarityName.toLowerCase())) {
							rarity = rarityName;
							query = query.replace(rarityName.toLowerCase(), "").trim().replaceAll("\\s+", " ");
							break;
						}
					}

					lowestBinArr = queryLowestBinPet(query, rarity);
					if (lowestBinArr == null) {
						return getFailCauseChat("Error fetching auctions data");
					}
					break;
				}
			}
		}

		if (lowestBinArr == null) {
			lowestBinArr = queryLowestBin(query);
			if (lowestBinArr == null) {
				return getFailCauseChat("Error fetching auctions data");
			}
		}

		if (lowestBinArr.size() == 0) {
			return getFailCauseChat("No bins matching '" + query + "' found");
		}

		JsonElement lowestBinJson = lowestBinArr.get(0);
		return (
			(tempName == null ? higherDepth(lowestBinJson, "item_name").getAsString() : tempName) +
			"'s lowest bin costs " +
			simplifyNumber(higherDepth(lowestBinJson, "starting_bid").getAsDouble()) +
			" and is sold by " +
			usernameUuid(higherDepth(lowestBinJson, "auctioneer").getAsString()).getUsername()
		);
	}

	@Override
	public String getCommandName() {
		return "hpa:bin";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hpa:lbin");
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <item>";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		executor.submit(() -> sender.addChatMessage(getBinString(args)));
	}
}
