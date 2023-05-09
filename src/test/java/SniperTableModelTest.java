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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
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
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(aRowChangedEvent()));
        }});

        model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        assertColumnEquals(SnipersTableModel.Column.ITEM_IDENTIFIER, "item id");
        assertColumnEquals(SnipersTableModel.Column.LAST_PRICE, 555);
        assertColumnEquals(SnipersTableModel.Column.LAST_BID, 666);
        assertColumnEquals(SnipersTableModel.Column.SNIPER_STATE, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    private void assertColumnEquals(SnipersTableModel.Column column, Object expected) {
        int rowIndex = 0;
        int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}
