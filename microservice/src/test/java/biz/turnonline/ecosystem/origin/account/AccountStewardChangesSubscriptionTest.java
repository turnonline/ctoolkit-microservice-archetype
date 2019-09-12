package biz.turnonline.ecosystem.origin.account;

import biz.turnonline.ecosystem.origin.service.LocalAccountProvider;
import biz.turnonline.ecosystem.steward.model.Account;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.io.ByteStreams;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.pubsub.PubsubCommand;
import org.ctoolkit.restapi.client.pubsub.TopicMessage;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_EMAIL;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_UNIQUE_ID;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.DATA_TYPE;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ENCODED_UNIQUE_KEY;

/**
 * {@link AccountStewardChangesSubscription} unit testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class AccountStewardChangesSubscriptionTest
{
    private static final String EMAIL = "my.account@turnonline.biz";

    private static final Long ACCOUNT_ID = 1233219L;

    @Tested
    private AccountStewardChangesSubscription tested;

    @Injectable
    private LocalAccountProvider lap;

    @Test
    public void onMessage_ValidPubsubMessage_NoChange() throws Exception
    {
        LocalAccount localAccount = new LocalAccount( EMAIL, ACCOUNT_ID );
        localAccount.setZoneId( "Europe/Paris" );

        new Expectations( localAccount )
        {
            {
                lap.initGet( EMAIL, ACCOUNT_ID );
                result = localAccount;

                localAccount.save();
                times = 0;
            }
        };

        PubsubMessage message = validPubsubMessage();
        tested.onMessage( message, "account.changes" );

        assertThat( localAccount.getEmail() ).isEqualTo( EMAIL );
        assertThat( localAccount.getZoneId() ).isEqualTo( ZoneId.of( "Europe/Paris" ) );
        assertThat( localAccount.getLocale() ).isEqualTo( Locale.ENGLISH );
    }

    @Test
    public void onMessage_ValidPubsubMessage_ZoneIdChanged() throws Exception
    {
        LocalAccount localAccount = new LocalAccount( EMAIL, ACCOUNT_ID );
        localAccount.setZoneId( "America/Chicago" );

        new Expectations( localAccount )
        {
            {
                lap.initGet( EMAIL, ACCOUNT_ID );
                result = localAccount;

                localAccount.save();
                times = 1;
            }
        };

        PubsubMessage message = validPubsubMessage();
        tested.onMessage( message, "account.changes" );

        assertThat( localAccount.getEmail() ).isEqualTo( EMAIL );
        assertThat( localAccount.getZoneId() ).isEqualTo( ZoneId.of( "Europe/Paris" ) );
        assertThat( localAccount.getLocale() ).isEqualTo( Locale.ENGLISH );
    }

    @Test
    public void onMessage_ValidPubsubMessage_EmailChanged() throws Exception
    {
        LocalAccount localAccount = new LocalAccount( EMAIL, ACCOUNT_ID );
        localAccount.setZoneId( "Europe/Paris" );
        localAccount.setEmail( "another.account@turnonline.biz" );

        new Expectations( localAccount )
        {
            {
                lap.initGet( EMAIL, ACCOUNT_ID );
                result = localAccount;

                localAccount.save();
                times = 1;
            }
        };

        PubsubMessage message = validPubsubMessage();
        tested.onMessage( message, "account.changes" );

        assertThat( localAccount.getEmail() ).isEqualTo( EMAIL );
        assertThat( localAccount.getZoneId() ).isEqualTo( ZoneId.of( "Europe/Paris" ) );
        assertThat( localAccount.getLocale() ).isEqualTo( Locale.ENGLISH );
    }

    @Test
    public void onMessage_ValidPubsubMessage_LocaleChanged() throws Exception
    {
        LocalAccount localAccount = new LocalAccount( EMAIL, ACCOUNT_ID );
        localAccount.setZoneId( "Europe/Paris" );
        localAccount.setLocale( "de" );

        new Expectations( localAccount )
        {
            {
                lap.initGet( EMAIL, ACCOUNT_ID );
                result = localAccount;

                localAccount.save();
                times = 1;
            }
        };

        PubsubMessage message = validPubsubMessage();
        tested.onMessage( message, "account.changes" );

        assertThat( localAccount.getEmail() ).isEqualTo( EMAIL );
        assertThat( localAccount.getZoneId() ).isEqualTo( ZoneId.of( "Europe/Paris" ) );
        assertThat( localAccount.getLocale() ).isEqualTo( Locale.ENGLISH );
    }

    @Test
    public void onMessage_ValidPubsubMessage_UninterestedDataType() throws Exception
    {
        PubsubMessage message = uninterestedPubsubMessage();
        tested.onMessage( message, "account.changes" );

        new Verifications()
        {
            {
                lap.initGet( anyString, anyLong );
                times = 0;
            }
        };
    }

    @Test
    public void onMessage_InvalidPubsubMessage() throws Exception
    {
        PubsubMessage message = invalidPubsubMessage();
        tested.onMessage( message, "account.changes" );

        new Verifications()
        {
            {
                lap.initGet( anyString, anyLong );
                times = 0;
            }
        };
    }

    private PubsubMessage uninterestedPubsubMessage() throws IOException
    {
        TopicMessage.Builder builder = incompletePubsubMessageBuilder( "Uninterested" );
        builder.addAttribute( ACCOUNT_EMAIL, EMAIL );
        return builder.build().getMessages().get( 0 );
    }

    private PubsubMessage invalidPubsubMessage() throws IOException
    {
        return incompletePubsubMessageBuilder( Account.class.getSimpleName() ).build().getMessages().get( 0 );
    }

    private PubsubMessage validPubsubMessage() throws IOException
    {
        TopicMessage.Builder builder = incompletePubsubMessageBuilder( Account.class.getSimpleName() );
        builder.addAttribute( ACCOUNT_EMAIL, EMAIL );
        return builder.build().getMessages().get( 0 );
    }

    /**
     * {@link PubsubCommand#ACCOUNT_EMAIL} is missing to be valid {@link Account} Pub/Sub message.
     */
    private TopicMessage.Builder incompletePubsubMessageBuilder( String dataType ) throws IOException
    {
        InputStream stream = getClass().getResourceAsStream( "account.json" );
        byte[] bytes = ByteStreams.toByteArray( stream );

        TopicMessage.Builder builder = TopicMessage.newBuilder();
        String id = String.valueOf( ACCOUNT_ID );
        builder.setProjectId( "projectId-135" ).setTopicId( "a-topic" )
                .addMessage( bytes, ACCOUNT_UNIQUE_ID, id )
                .addAttribute( DATA_TYPE, dataType )
                .addAttribute( ENCODED_UNIQUE_KEY, id );

        return builder;
    }
}