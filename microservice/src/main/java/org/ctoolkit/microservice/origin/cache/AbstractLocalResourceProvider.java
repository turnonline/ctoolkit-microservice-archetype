package org.ctoolkit.microservice.origin.cache;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Stopwatch;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ctoolkit.microservice.origin.api.EndpointsApiProfile.API_NAME;

/**
 * The local resource provider implementation that keeps cached instance
 * for specified time in memcache (default 3600 seconds). Implementation expects, the {@link #type()}
 * of the resource is serializable by <strong>Jackson 2</strong> {@link com.fasterxml.jackson.core.JsonFactory}.
 * <p>
 * In order to bind a concrete type of the resource to be memcached, implement your own
 * {@link AbstractLocalResourceProvider} and declare following in Guice module, for example:
 * <pre>
 * {@code
 *     bind( new TypeLiteral<LocalResourceProvider<Account>>()
 *     {
 *     } ).to( MyLocalAccountProvider.class );
 * }
 * </pre>
 * Once declared and rest-facade configured, an Account retrieval will be cached via
 * {@link org.ctoolkit.restapi.client.RestFacade} call, for example:
 * <pre>
 * {@code
 *     RestFacade.get( Account.class )
 *                     .identifiedBy( "my.account@example.com" )
 *                     .finish();
 * }
 * </pre>
 *
 * @param <T> the type of the resource to be cached
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public abstract class AbstractLocalResourceProvider<T>
        implements LocalResourceProvider<T>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AbstractLocalResourceProvider.class );

    private final MemcacheService syncCache;

    public AbstractLocalResourceProvider()
    {
        syncCache = MemcacheServiceFactory.getMemcacheService();
        syncCache.setErrorHandler( ErrorHandlers.getConsistentLogAndContinue( Level.INFO ) );
    }

    @Override
    public T get( @Nonnull Identifier identifier,
                  @Nullable Map<String, Object> parameters,
                  @Nullable Locale locale )
    {
        String key = fullKey( identifier );
        String json = ( String ) syncCache.get( key );
        if ( json == null )
        {
            return null;
        }

        try
        {
            return JacksonFactory.getDefaultInstance().fromString( json, type() );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Parsing of a JSON string for '" + key + "' has failed", e );
            LOGGER.error( json );
            return null;
        }
    }

    @Override
    public void persist( @Nonnull T instance,
                         @Nonnull Identifier identifier,
                         @Nullable Map<String, Object> parameters,
                         @Nullable Locale locale,
                         @Nullable Long lastFor )
    {
        checkNotNull( instance );

        Stopwatch started = Stopwatch.createStarted();
        String key = fullKey( identifier );

        try
        {
            Expiration expiration;

            if ( lastFor == null )
            {
                expiration = Expiration.byDeltaSeconds( 3600 );
            }
            else
            {
                expiration = Expiration.byDeltaMillis( lastFor.intValue() );
            }

            String json = JacksonFactory.getDefaultInstance().toString( instance );
            syncCache.put( key, json, expiration );
            LOGGER.info( "Account JSON serialization and caching took: " + started.stop() );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Cache put has failed for key '" + key + "'. ", e );
        }
    }

    protected String fullKey( @Nonnull Identifier identifier )
    {
        Class<T> type = type();
        return API_NAME + "::" + type.getSimpleName() + "::" + identifier.key();
    }

    /**
     * Returns the type of the resource to be cached.
     *
     * @return type of the resource
     */
    protected abstract Class<T> type();
}
