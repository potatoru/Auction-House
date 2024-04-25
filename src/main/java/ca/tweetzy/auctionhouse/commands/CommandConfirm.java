/*
 * Auction House
 * Copyright 2018-2022 Kiran Hart
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

package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.flight.comp.Titles;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 15 2021
 * Time Created: 4:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandConfirm extends AbstractCommand {

	public CommandConfirm() {
		super(CommandType.PLAYER_ONLY, "confirm");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		final Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final AuctionHouse instance = AuctionHouse.getInstance();

		if (instance.getAuctionPlayerManager().getPlayer(player.getUniqueId()) == null) {
			instance.getLocale().newMessage(TextUtils.formatText("&cCould not find auction player instance for&f: &e" + player.getName() + "&c creating one now.")).sendPrefixedMessage(Bukkit.getConsoleSender());
			instance.getAuctionPlayerManager().addPlayer(new AuctionPlayer(player));
		}

		final AuctionPlayer auctionPlayer = instance.getAuctionPlayerManager().getPlayer(player.getUniqueId());

		if (auctionPlayer.getEndAllRequestTime() == -1) {
			instance.getLocale().getMessage("general.nothing to confirm").sendPrefixedMessage(player);
			return ReturnType.SUCCESS;
		}

		if (System.currentTimeMillis() > auctionPlayer.getEndAllRequestTime()) {
			instance.getLocale().getMessage("general.confirm time limit reached").sendPrefixedMessage(player);
			return ReturnType.SUCCESS;
		}

		if (System.currentTimeMillis() <= auctionPlayer.getEndAllRequestTime()) {
			instance.getLocale().getMessage("general.confirmed cancellation").sendPrefixedMessage(player);
			auctionPlayer.setEndAllRequestTime(-1);
			Titles.clearTitle(player);

			for (AuctionedItem item : auctionPlayer.getItems(false)) {
				if (Settings.SELLERS_MUST_WAIT_FOR_TIME_LIMIT_AFTER_BID.getBoolean() && item.containsValidBid())
					continue;

				if (item.isRequest()) {
					AuctionHouse.getInstance().getAuctionItemManager().sendToGarbage(item);
				} else {
					item.setExpired(true);
				}
			}
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.confirm";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.confirm").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.confirm").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
