package biz.turnonline.ecosystem.origin.service.model;

import biz.turnonline.ecosystem.origin.service.LocalAccountProvider;
import com.google.common.base.Stopwatch;
import org.ctoolkit.restapi.client.RestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * The {@link LocalAccountProvider} implementation.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Singleton
public class LocalAccountProviderImpl
        implements LocalAccountProvider
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalAccountProviderImpl.class );

    private final RestFacade facade;

    @Inject
    LocalAccountProviderImpl( RestFacade facade )
    {
        this.facade = facade;
    }

    @Override
    public LocalAccount initGet( @Nonnull String email, @Nonnull Long id )
    {
        checkNotNull( email, "Account email cannot be null" );
        checkNotNull( id, "Account ID cannot be null" );

        LocalAccount localAccount = get( id );

        if ( localAccount == null )
        {
            Stopwatch stopwatch = Stopwatch.createStarted();
            localAccount = new LocalAccount( email, id );
            localAccount.init( facade );
            localAccount.save();
            stopwatch.stop();
            LOGGER.info( "Local account has been created (" + stopwatch + "): " + localAccount );
        }

        return localAccount;
    }

    @Override
    public LocalAccount get( @Nonnull String email )
    {
        return ofy().load().type( LocalAccount.class ).filter( "email", email ).first().now();
    }

    @Override
    public LocalAccount get( @Nonnull Long id )
    {
        return ofy().load().type( LocalAccount.class ).id( id ).now();
    }
}
