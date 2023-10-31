/*
 * Auction House
 * Copyright 2023 Kiran Hart
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

package ca.tweetzy.auctionhouse.api.auction;

import ca.tweetzy.auctionhouse.api.Identifiable;
import ca.tweetzy.auctionhouse.api.sync.Storeable;

import java.util.UUID;

public interface Bid extends Identifiable<UUID>, Storeable<Bid> {

	UUID getAuctionId();

	UUID getBidderUUID();

	String getBidderName();

	double getAmount();

	String getBidWorld();

	String getServer();

	long getBidTime();
}
