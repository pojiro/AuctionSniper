package test.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.jxmpp.jid.EntityBareJid;

public class AuctionMessageTranslatorTest {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);
    public static final Chat UNUSED_CHAT = null;
    public static final EntityBareJid UNUSED_ENTITY_BARE_JID = null;

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        var message = StanzaBuilder.buildMessage()
                .setBody("SOLVersion: 1.1; Event: CLOSE;")
                .build();

        translator.newIncomingMessage(UNUSED_ENTITY_BARE_JID, message, UNUSED_CHAT);
    }
}
