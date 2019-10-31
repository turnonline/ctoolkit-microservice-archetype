package biz.turnonline.ecosystem.origin.guice;

import biz.turnonline.ecosystem.origin.api.MessageEndpoint;
import com.google.api.server.spi.ServletInitializationParameters;
import com.google.api.server.spi.guice.EndpointsModule;
import com.googlecode.objectify.ObjectifyFilter;

import javax.inject.Singleton;

import static org.ctoolkit.services.endpoints.EndpointsMonitorConfig.ENDPOINTS_SERVLET_PATH;

/**
 * The endpoints service classes configuration.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 * @see EndpointsModule
 */
public class EndpointsInitialization
        extends EndpointsModule
{
    @Override
    protected void configureServlets()
    {
        ServletInitializationParameters params = ServletInitializationParameters.builder()
                // add your endpoint service implementation
                .addServiceClass( MessageEndpoint.class )
                .setClientIdWhitelistEnabled( true ).build();

        configureEndpoints( ENDPOINTS_SERVLET_PATH, params );

        bind( ObjectifyFilter.class ).in( Singleton.class );
        filter( "/*" ).through( ObjectifyFilter.class );
    }
}
