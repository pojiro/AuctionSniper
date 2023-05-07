package auctionsniper;

import java.util.Objects;

public class SniperState {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;

    public SniperState(String itemId, int lastPrice, int lastBid) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        SniperState sniperState = (SniperState) obj;
        return Objects.equals(sniperState.itemId, this.itemId)
                && sniperState.lastPrice == this.lastPrice
                && sniperState.lastBid == this.lastBid;
    }
}
