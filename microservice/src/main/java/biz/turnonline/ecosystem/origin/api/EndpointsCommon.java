package biz.turnonline.ecosystem.origin.api;

import biz.turnonline.ecosystem.origin.account.LocalAccount;
import biz.turnonline.ecosystem.origin.service.LocalAccountProvider;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import org.ctoolkit.restapi.client.RestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * The common services to handle REST API endpoints requests.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Singleton
class EndpointsCommon
{
    private static final Logger LOGGER = LoggerFactory.getLogger( EndpointsCommon.class );

    private final LocalAccountProvider lap;

    private final RestFacade facade;

    @Inject
    EndpointsCommon( LocalAccountProvider lap, RestFacade facade )
    {
        this.lap = lap;
        this.facade = facade;
    }

    static String tryAgainLaterMessage()
    {
        return "Try again later";
    }

    /**
     * Returns the local lightweight TurnOnline.biz Ecosystem account representation identified by email account.
     *
     * @param authUser the authenticated user
     * @throws UnauthorizedException if there is no authenticated user
     * @throws NotFoundException     if authenticated user does not have account
     */
    LocalAccount checkLocalAccount( User authUser )
            throws UnauthorizedException, NotFoundException, InternalServerErrorException
    {
        if ( authUser == null )
        {
            throw new UnauthorizedException( "User is unauthorized." );
        }

        LocalAccount localAccount;
        LocalAccountProvider.Builder builder = new LocalAccountProvider.Builder()
                .email( authUser.getEmail() )
                .identityId( authUser.getId() );

        try
        {
            localAccount = lap.initGet( builder );
        }
        catch ( org.ctoolkit.restapi.client.NotFoundException e )
        {
            throw new NotFoundException( "TurnOnline.biz Ecosystem account not found for: " + builder );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Account retrieval for '" + builder + "' has failed", e );
            throw new InternalServerErrorException( tryAgainLaterMessage() );
        }

        return localAccount;
    }
}
