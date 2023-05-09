package auctionsniper;

import java.util.Objects;

public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;

    public SniperSnapshot(String itemId, int lastPrice, int lastBid) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;
        SniperSnapshot sniperSnapshot = (SniperSnapshot) obj;
        return Objects.equals(sniperSnapshot.itemId, this.itemId)
                && sniperSnapshot.lastPrice == this.lastPrice
                && sniperSnapshot.lastBid == this.lastBid;
    }
}
