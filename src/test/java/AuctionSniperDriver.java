import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
import com.objogate.wl.swing.gesture.GesturePerformer;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static java.lang.String.valueOf;

public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()), new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        var table = new JTableDriver(this);
        table.hasRow(matching(
                withLabelText(itemId),
                withLabelText(valueOf(lastPrice)),
                withLabelText(valueOf(lastBid)),
                withLabelText(valueOf(statusText))
        ));
    }

    public void hasColumnTitles() {
        var headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(
                withLabelText("Item"),
                withLabelText("Last Price"),
                withLabelText("Last Bid"),
                withLabelText("State")
        ));
    }

    public void startBiddingFor(String itemId) {
        var itemIdField = itemIdField();
        itemIdField.replaceAllText(itemId);

        var button = bidButton();
        button.click();
    }

    private JTextFieldDriver itemIdField() {
        var newItemId = new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, javax.swing.JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }
}
