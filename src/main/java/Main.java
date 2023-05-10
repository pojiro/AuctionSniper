import auctionsniper.*;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        var connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);

        for (int i = ARG_ITEM_ID; i < args.length; i++) {
            main.joinAuction(connection, args[i]);
        }
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    private static XMPPTCPConnection connection(String hostname, String username, String password) throws IOException, SmackException, XMPPException, InterruptedException {
        var connection = new XMPPTCPConnection(
                XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(hostname)
                        .setHost(hostname)
                        .setPort(5222)
                        .build()
        );

        connection.connect();
        connection.login(username, password, Resourcepart.from(AUCTION_RESOURCE));

        return connection;
    }

    private static String auctionId(String itemId, XMPPTCPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getXMPPServiceDomain());
    }

    private void joinAuction(XMPPTCPConnection connection, String itemId) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        var chatManager = ChatManager.getInstanceFor(connection);
        var auctionJid = JidCreate.entityBareFrom(auctionId(itemId, connection));
        var chat = chatManager.chatWith(auctionJid);
        var auction = new XMPPAuction(chat);
        chatManager.addIncomingListener(
                new AuctionMessageTranslator(
                        connection.getUser().asEntityBareJid().toString(),
                        new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId)
                ));
        auction.join();
    }

    private void disconnectWhenUICloses(XMPPTCPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
                super.windowClosed(e);
            }
        });
    }

    public class SwingThreadSniperListener implements SniperListener {
        private final SnipersTableModel snipersTableModel;

        public SwingThreadSniperListener(SnipersTableModel snipersTableModel) {
            this.snipersTableModel = snipersTableModel;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
            snipersTableModel.sniperStateChanged(sniperSnapshot);
        }
    }
}