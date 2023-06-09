package auctionsniper;

import org.jivesoftware.smack.chat2.Chat;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    private final Chat chat;

    public XMPPAuction(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(String message) {
        try {
            chat.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
