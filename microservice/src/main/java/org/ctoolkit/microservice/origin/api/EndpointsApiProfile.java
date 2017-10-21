package org.ctoolkit.microservice.origin.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * The endpoint profile, the base class as a configuration of the REST API and generated client.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Api( name = "myApiName",
        canonicalName = "API Name",
        title = "Example REST API",
        version = "v1",
        description = "Example REST API",
        documentationLink = "https://ecosystem.turnonline.biz/docs",
        namespace = @ApiNamespace( ownerDomain = "ecosystem.turnonline.biz", ownerName = "Example, Ltd." )
)
class EndpointsApiProfile
{
}
