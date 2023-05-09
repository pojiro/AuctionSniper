import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static final SniperSnapshot STARTING_UP = SniperSnapshot.joining("");
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
        return Column.at(columnIndex).valueIn(sniperSnapshot);
    }

    private final static String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        sniperSnapshot = newSniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public enum Column {
        ITEM_IDENTIFIER("Item") {
            @Override
            public Object valueIn(SniperSnapshot sniperSnapshot) {
                return sniperSnapshot.itemId;
            }
        }, LAST_PRICE("Last Price") {
            @Override
            public Object valueIn(SniperSnapshot sniperSnapshot) {
                return sniperSnapshot.lastPrice;
            }
        }, LAST_BID("Last Bid") {
            @Override
            public Object valueIn(SniperSnapshot sniperSnapshot) {
                return sniperSnapshot.lastBid;
            }
        }, SNIPER_STATE("State") {
            @Override
            public Object valueIn(SniperSnapshot sniperSnapshot) {
                return textFor(sniperSnapshot.state);
            }
        };
        public final String name;

        private Column(String name) {
            this.name = name;
        }

        abstract public Object valueIn(SniperSnapshot sniperSnapshot);

        public static Column at(int offset) {
            return values()[offset];
        }
    }
}
