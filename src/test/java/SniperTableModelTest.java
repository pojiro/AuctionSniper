import auctionsniper.SniperSnapshot;
import auctionsniper.SniperSnapshot.SniperState;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SniperTableModelTest {
    @RegisterExtension
    public final JUnit5Mockery context = new JUnit5Mockery();
    private final TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @BeforeEach
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(SnipersTableModel.Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        var joining = SniperSnapshot.joining("item id");
        var bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));

            one(listener).tableChanged(with(aChangeInRow(0)));
        }});

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (var column : SnipersTableModel.Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingASniper() {
        var joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, SnipersTableModel.Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, SnipersTableModel.Column.ITEM_IDENTIFIER));
    }

    private Object cellValue(int rowIndex, SnipersTableModel.Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private void assertColumnEquals(SnipersTableModel.Column column, Object expected) {
        int rowIndex = 0;
        int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot sniperSnapshot) {
        for (var column : SnipersTableModel.Column.values()) {
            assertEquals(model.getValueAt(rowIndex, column.ordinal()), SnipersTableModel.Column.at(column.ordinal()).valueIn(sniperSnapshot));
        }
    }

    private Matcher<TableModelEvent> aChangeInRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
}
