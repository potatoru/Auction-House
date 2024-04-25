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

package ca.tweetzy.auctionhouse.guis.confirmation;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.auction.AuctionPayment;
import ca.tweetzy.auctionhouse.auction.AuctionPlayer;
import ca.tweetzy.auctionhouse.auction.AuctionedItem;
import ca.tweetzy.auctionhouse.auction.enums.AuctionStackType;
import ca.tweetzy.auctionhouse.auction.enums.PaymentReason;
import ca.tweetzy.auctionhouse.guis.GUIActiveAuctions;
import ca.tweetzy.auctionhouse.guis.abstraction.AuctionBaseGUI;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 20 2021
 * Time Created: 11:28 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIConfirmCancel extends AuctionBaseGUI {

	final AuctionPlayer auctionPlayer;
	final AuctionedItem auctionItem;

	public GUIConfirmCancel(AuctionPlayer auctionPlayer, AuctionedItem auctionItem) {
		super(null, auctionPlayer.getPlayer(), Settings.GUI_CONFIRM_CANCEL_TITLE.getString(), 1);
		this.auctionPlayer = auctionPlayer;
		this.auctionItem = auctionItem;
		setAcceptsItems(false);
		draw();
	}


	@Override
	protected void draw() {
		for (int i = 0; i < 4; i++)
			drawYes(i);

		setItem(0, 4, this.auctionItem.getDisplayStack(AuctionStackType.ACTIVE_AUCTIONS_LIST));

		for (int i = 5; i < 9; i++)
			drawNo(i);
	}

	private void drawNo(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_CANCEL_NO_ITEM.getString())
				.name(Settings.GUI_CONFIRM_CANCEL_NO_NAME.getString())
				.lore(Settings.GUI_CONFIRM_CANCEL_NO_LORE.getStringList())
				.make(), click -> click.manager.showGUI(click.player, new GUIActiveAuctions(this.auctionPlayer)));
	}

	private void drawYes(int slot) {
		setButton(slot, QuickItem
				.of(Settings.GUI_CONFIRM_CANCEL_YES_ITEM.getString())
				.name(Settings.GUI_CONFIRM_CANCEL_YES_NAME.getString())
				.lore(Settings.GUI_CONFIRM_CANCEL_YES_LORE.getStringList())
				.make(), click -> {

			// Re-select the item to ensure that it's available
			AuctionedItem located = AuctionHouse.getInstance().getAuctionItemManager().getItem(this.auctionItem.getId());
			if (located == null) {
				click.manager.showGUI(click.player, new GUIActiveAuctions(this.auctionPlayer));
				return;
			}

			located.setExpired(true);

			if (Settings.BIDDING_TAKES_MONEY.getBoolean() && !located.getHighestBidder().equals(located.getOwner())) {
				final OfflinePlayer oldBidder = Bukkit.getOfflinePlayer(located.getHighestBidder());

				if (Settings.STORE_PAYMENTS_FOR_MANUAL_COLLECTION.getBoolean())
					AuctionHouse.getInstance().getDataManager().insertAuctionPayment(new AuctionPayment(
							oldBidder.getUniqueId(),
							located.getCurrentPrice(),
							auctionItem.getItem(),
							AuctionHouse.getInstance().getLocale().getMessage("general.prefix").getMessage(),
							PaymentReason.BID_RETURNED

					), null);
				else
					EconomyManager.deposit(oldBidder, located.getCurrentPrice());

				if (oldBidder.isOnline())
					AuctionHouse.getInstance().getLocale().getMessage("pricing.moneyadd").processPlaceholder("player_balance", AuctionAPI.getInstance().formatNumber(EconomyManager.getBalance(oldBidder))).processPlaceholder("price", AuctionAPI.getInstance().formatNumber(located.getCurrentPrice())).sendPrefixedMessage(oldBidder.getPlayer());

			}

			click.manager.showGUI(click.player, new GUIActiveAuctions(this.auctionPlayer));
		});
	}
}
