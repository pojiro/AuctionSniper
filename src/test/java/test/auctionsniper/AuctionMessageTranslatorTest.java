package test.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class AuctionMessageTranslatorTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator;
    public static final Chat UNUSED_CHAT = null;
    public final EntityBareJid AUCTION_BARE_JID;

    public AuctionMessageTranslatorTest() throws XmppStringprepException {
        this.AUCTION_BARE_JID = JidCreate.entityBareFrom("auction@localhost");
        this.translator = new AuctionMessageTranslator(JidCreate.entityBareFrom("sniper@localhost"), AUCTION_BARE_JID, listener);
    }

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        var message = StanzaBuilder.buildMessage()
                .setBody("SOLVersion: 1.1; Event: CLOSE;")
                .build();

        translator.newIncomingMessage(AUCTION_BARE_JID, message, UNUSED_CHAT);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
        }});

        var message = StanzaBuilder.buildMessage()
                .setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;")
                .build();

        translator.newIncomingMessage(AUCTION_BARE_JID, message, UNUSED_CHAT);
    }


}
