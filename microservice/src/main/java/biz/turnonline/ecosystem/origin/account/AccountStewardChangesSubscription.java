package biz.turnonline.ecosystem.origin.account;

import biz.turnonline.ecosystem.origin.service.LocalAccountProvider;
import biz.turnonline.ecosystem.steward.model.Account;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.base.Strings;
import org.apache.commons.lang3.LocaleUtils;
import org.ctoolkit.restapi.client.pubsub.PubsubCommand;
import org.ctoolkit.restapi.client.pubsub.PubsubMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;

import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_EMAIL;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_IDENTITY_ID;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_UNIQUE_ID;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.DATA_TYPE;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ENCODED_UNIQUE_KEY;

/**
 * The 'account.changes' subscription listener implementation.
 * Updates following property values from {@link Account} if any of those values
 * has changed comparing to {@link LocalAccount}.
 * <ul>
 *     <li>{@link LocalAccount#setEmail(String)}</li>
 *     <li>{@link LocalAccount#setZoneId(String)}</li>
 *     <li>{@link LocalAccount#setLocale(String)}</li>
 * </ul>
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Singleton
public class AccountStewardChangesSubscription
        implements PubsubMessageListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountStewardChangesSubscription.class );

    private static final long serialVersionUID = -6791606962382054854L;

    private final LocalAccountProvider lap;

    @Inject
    AccountStewardChangesSubscription( LocalAccountProvider lap )
    {
        this.lap = lap;
    }

    @Override
    public void onMessage( @Nonnull PubsubMessage message, @Nonnull String subscription ) throws Exception
    {
        PubsubCommand command = new PubsubCommand( message );
        String[] mandatory = {DATA_TYPE, ENCODED_UNIQUE_KEY, ACCOUNT_UNIQUE_ID, ACCOUNT_EMAIL, ACCOUNT_IDENTITY_ID};
        if ( !command.validate( mandatory ) )
        {
            LOGGER.error( "Some of the mandatory attributes "
                    + Arrays.toString( mandatory )
                    + " are missing, incoming attributes: "
                    + message.getAttributes() );
            return;
        }

        String dataType = command.getDataType();
        if ( !Account.class.getSimpleName().equals( dataType ) )
        {
            LOGGER.info( "Uninterested data type '" + dataType + "'" );
            return;
        }

        Long accountId = command.getAccountId();
        String data = message.getData();
        boolean signUp = command.isAccountSignUp();

        LOGGER.info( "[" + subscription + "] " + dataType + " has been received at publish time "
                + message.getPublishTime()
                + " with length: "
                + data.length()
                + " and unique ID: '"
                + accountId
                + "'. Is new account sign-up: "
                + signUp );

        Account account = fromString( data, Account.class );
        LocalAccount localAccount = lap.initGet( new LocalAccountProvider.Builder()
                .accountId( accountId )
                .email( command.getAccountEmail() )
                .identityId( command.getAccountIdentityId() ) );

        if ( Account.class.getSimpleName().equals( dataType ) )
        {
            process( localAccount, account );
        }
    }

    private void process( @Nonnull LocalAccount localAccount, @Nonnull Account account )
    {
        boolean updateAccount = false;

        // Current, the most up to date Zone ID, taken from the remote account
        ZoneId remoteZoneId = Strings.isNullOrEmpty( account.getZoneId() ) ? null : ZoneId.of( account.getZoneId() );
        ZoneId zoneId = localAccount.getZoneId();

        if ( remoteZoneId != null && !remoteZoneId.equals( zoneId ) )
        {
            LOGGER.info( "Zone ID has changed from '" + localAccount.getZoneId() + "' to '" + remoteZoneId + "'" );
            localAccount.setZoneId( remoteZoneId.getId() );
            updateAccount = true;
        }

        // Current, the most up to date login email, taken from the remote account
        String remoteLoginEmail = account.getEmail();
        if ( !remoteLoginEmail.equalsIgnoreCase( localAccount.getEmail() ) )
        {
            LOGGER.info( "Login Email has changed from '" + localAccount.getEmail() + "' to '" + remoteLoginEmail + "'" );
            localAccount.setEmail( remoteLoginEmail );
            updateAccount = true;
        }

        // Current, the most up to date locale, taken from the remote account
        Locale remoteLocale = account.getLocale() == null ? null : LocaleUtils.toLocale( account.getLocale() );
        if ( remoteLocale != null && !remoteLocale.equals( localAccount.getLocale() ) )
        {
            LOGGER.info( "Account locale has changed from '" + localAccount.getLocale() + "' to '" + remoteLocale + "'" );
            localAccount.setLocale( account.getLocale() );
            updateAccount = true;
        }

        if ( updateAccount )
        {
            localAccount.save();
        }
    }
}
