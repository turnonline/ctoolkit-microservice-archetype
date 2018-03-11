package org.ctoolkit.microservice.origin.guice;

import com.google.api.server.spi.ServletInitializationParameters;
import com.google.api.server.spi.guice.EndpointsModule;
import com.google.api.server.spi.guice.GuiceEndpointsServlet;
import com.google.api.server.spi.guice.ServiceMap;
import com.googlecode.objectify.ObjectifyFilter;
import org.ctoolkit.microservice.origin.api.MessageEndpoint;
import org.ctoolkit.services.endpoints.EndpointsContextAwareServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static org.ctoolkit.services.endpoints.EndpointsMonitorConfig.ENDPOINTS_SERVLET_PATH;

/**
 * The endpoints service classes configuration.
 * As an alternative you can use context aware servlet {@link EndpointsContextAwareServlet}
 * instead of standard {@link GuiceEndpointsServlet}
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see EndpointsModule
 */
public class EndpointsInitialization
        extends EndpointsModule
{
    private static final Logger logger = LoggerFactory.getLogger( EndpointsInitialization.class );

    @Override
    protected void configureServlets()
    {
        ServletInitializationParameters params = ServletInitializationParameters.builder()
                // add your endpoint service implementation
                .addServiceClass( MessageEndpoint.class )
                .setRestricted( true )
                .setClientIdWhitelistEnabled( true ).build();

        configureEndpoints( ENDPOINTS_SERVLET_PATH, params );

        bind( ObjectifyFilter.class ).in( Singleton.class );
        filter( "/*" ).through( ObjectifyFilter.class );
    }

    protected void configureEndpoints( String urlPattern,
                                       ServletInitializationParameters initParameters,
                                       boolean useLegacyServlet )
    {
        bind( ServiceMap.class ).toInstance( ServiceMap.create( binder(), initParameters.getServiceClasses() ) );
        if ( useLegacyServlet )
        {
            logger.error( "the legacy servlet is no longer available." );
        }

        super.serve( urlPattern ).with( GuiceEndpointsServlet.class, initParameters.asMap() );
    }
}
