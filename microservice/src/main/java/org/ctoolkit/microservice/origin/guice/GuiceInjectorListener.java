package org.ctoolkit.microservice.origin.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.ctoolkit.microservice.origin.service.MicroserviceModule;
import org.ctoolkit.services.endpoints.EndpointsMonitorConfig;
import org.ctoolkit.services.guice.AppEngineEnvironmentContextListener;

/**
 * The main entry point to configure guice injection.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class GuiceInjectorListener
        extends AppEngineEnvironmentContextListener
{
    @Override
    protected Injector getDevelopmentInjector()
    {
        return Guice.createInjector( new MicroserviceModule(), new EndpointsInitialization() );
    }

    @Override
    protected Injector getProductionInjector()
    {
        return Guice.createInjector( new MicroserviceModule(),
                new EndpointsInitialization(),
                new EndpointsMonitorConfig() );
    }
}
