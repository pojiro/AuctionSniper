import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    @Override
                    public void joinAuction(String itemId) {
                        buttonProbe.setReceivedValue(itemId);
                    }
                }
        );

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
}
