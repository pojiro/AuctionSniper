import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;
import com.objogate.exception.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private final ArrayList<SniperSnapshot> sniperSnapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return sniperSnapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapshots.get(rowIndex));
    }

    private final static String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Lost", "Won"};

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = rowMatching(newSniperSnapshot);
        sniperSnapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot sniperSnapshot) {
        for (int i = 0; i < sniperSnapshots.size(); i++) {
            if (sniperSnapshot.isForSameItemAs(sniperSnapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot fine match for " + sniperSnapshot);
    }

    public static String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public void addSniper(SniperSnapshot joining) {
        sniperSnapshots.add(joining);
        int row = sniperSnapshots.size() - 1;
        fireTableRowsInserted(row, row);
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
