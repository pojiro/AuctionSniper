package auctionsniper;

import auctionsniper.SniperSnapshot.SniperState;

public class AuctionSniper implements AuctionEventListener {
    private boolean isWinning = false;
    private final Auction auction;
    private final SniperListener sniperListener;
    private final String itemId;
    private SniperSnapshot sniperSnapshot;

    public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.itemId = itemId;
        this.sniperSnapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;

        if (isWinning) {
            sniperSnapshot = sniperSnapshot.winning(price);
        } else {
            int bid = price + increment;
            auction.bid(bid);
            sniperSnapshot = sniperSnapshot.bidding(price, bid);
        }
        sniperListener.sniperStateChanged(sniperSnapshot);
    }
}
