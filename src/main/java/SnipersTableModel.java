import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private SniperSnapshot sniperSnapshot = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return sniperSnapshot.itemId;
            case LAST_PRICE:
                return sniperSnapshot.lastPrice;
            case LAST_BID:
                return sniperSnapshot.lastBid;
            case SNIPER_STATE:
                return textFor(sniperSnapshot.state);
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    private final static String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    public void sniperStatusChanged(SniperSnapshot newSniperSnapshot) {
        sniperSnapshot = newSniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }

    public enum Column {
        ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATE;

        public static Column at(int offset) {
            return values()[offset];
        }
    }
}
