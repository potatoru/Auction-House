package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.api.events.AuctionEndEvent;
import ca.tweetzy.auctionhouse.api.events.AuctionStartEvent;
import ca.tweetzy.auctionhouse.auction.AuctionSaleType;
import ca.tweetzy.auctionhouse.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 27 2021
 * Time Created: 4:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class AuctionListeners implements Listener {

    @EventHandler
    public void onAuctionStart(AuctionStartEvent e) {
        if (Settings.DISCORD_ALERT_ON_AUCTION_START.getBoolean()) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
                Settings.DISCORD_WEBHOOKS.getStringList().forEach(hook -> AuctionAPI.getInstance().sendDiscordMessage(hook, e.getSeller(), e.getSeller(), e.getAuctionItem(), AuctionSaleType.USED_BIDDING_SYSTEM, true, e.getAuctionItem().getBidStartPrice() >= Settings.MIN_AUCTION_START_PRICE.getDouble()));
            }, 0L);
        }
    }

    @EventHandler
    public void onAuctionEnd(AuctionEndEvent e) {

    }

}
