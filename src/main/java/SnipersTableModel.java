import auctionsniper.SniperSnapshot;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);
    private String statusText = MainWindow.STATUS_JOINING;
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
            case SNIPER_STATUS:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void sniperStatusChanged(SniperSnapshot newSniperSnapshot, String newStatusText) {
        sniperSnapshot = newSniperSnapshot;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public enum Column {
        ITEM_IDENTIFIER, LAST_PRICE, LAST_BID, SNIPER_STATUS;

        public static Column at(int offset) {
            return values()[offset];
        }
    }
}
