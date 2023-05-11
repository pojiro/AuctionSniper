import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPERS_TABLE_NAME = "snipers table";
    public static final String NEW_ITEM_ID_NAME = "item id field";
    public static final String JOIN_BUTTON_NAME = "join button";

    public MainWindow(SnipersTableModel snipersTableModel) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(snipersTableModel), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SnipersTableModel snipersTableModel) {
        JTable snipersTable = new JTable(snipersTableModel);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        var controls = new JPanel(new FlowLayout());

        var itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        var joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);

        return controls;
    }
}