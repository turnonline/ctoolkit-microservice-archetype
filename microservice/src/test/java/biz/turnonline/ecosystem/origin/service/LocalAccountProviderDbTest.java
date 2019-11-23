package biz.turnonline.ecosystem.origin.service;

import biz.turnonline.ecosystem.origin.account.LocalAccount;
import biz.turnonline.ecosystem.steward.model.Account;
import mockit.Mock;
import mockit.MockUp;
import org.ctoolkit.restapi.client.RestFacade;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * {@link LocalAccount} unit testing against local emulated datastore.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class LocalAccountProviderDbTest
        extends BackendServicesTestCase
{
    @Inject
    private LocalAccountProvider lap;

    @Test
    public void initGet_SaveOk()
    {
        long accountId = 985L;
        String email = "my.account@turnonline.biz";
        String identityId = "Wh23h9kl";

        new MockUp<LocalAccount>()
        {
            @Mock
            public Account getAccount( @Nonnull RestFacade facade )
            {
                return new Account()
                        .setId( accountId )
                        .setEmail( email )
                        .setIdentityId( identityId );
            }
        };

        LocalAccount localAccount = lap.initGet( new LocalAccountProvider.Builder()
                .email( email )
                .identityId( identityId ) );

        assertWithMessage( "LocalAccount" )
                .that( localAccount )
                .isNotNull();

        LocalAccount la = lap.get( localAccount.getId() );

        assertWithMessage( "LocalAccount ID" )
                .that( la.getId() )
                .isEqualTo( accountId );

        assertWithMessage( "LocalAccount Email" )
                .that( la.getEmail() )
                .isEqualTo( email );

        assertWithMessage( "LocalAccount Identity ID" )
                .that( la.getIdentityId() )
                .isEqualTo( identityId );
    }
}
