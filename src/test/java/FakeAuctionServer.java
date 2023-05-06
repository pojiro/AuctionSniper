import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FakeAuctionServer {
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPTCPConnection connection;
    private Chat currentChat;
    private final SingleMessageListener messageListener = new SingleMessageListener();

    public FakeAuctionServer(String itemId) throws XmppStringprepException {
        this.itemId = itemId;
        this.connection = new XMPPTCPConnection(
                XMPPTCPConnectionConfiguration.builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(XMPP_HOSTNAME)
                        .setHost(XMPP_HOSTNAME)
                        .setPort(5222)
                        .build()
        );
    }

    public void startSellingItem() throws XMPPException, SmackException, IOException, InterruptedException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, Resourcepart.from(AUCTION_RESOURCE));
        var chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addIncomingListener(messageListener);
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void announceClosed() throws SmackException.NotConnectedException, InterruptedException {
        currentChat.send("");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    public class SingleMessageListener implements IncomingChatMessageListener {
        private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

        @Override
        public void newIncomingMessage(EntityBareJid entityBareJid, Message message, Chat chat) {
            currentChat = chat;
            messages.add(message);
        }

        public void receivesAMessage() throws InterruptedException {
            assertThat("Message", messages.poll(5, SECONDS), is(notNullValue()));
        }
    }
}