package org.ctoolkit.microservice.origin.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Endpoint REST API for {@link Message} content resource.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Api
@ApiReference( EndpointsApiProfile.class )
public class MessageEndpoint
{
    private final EndpointsCommon common;

    @Inject
    public MessageEndpoint( EndpointsCommon common )
    {
        this.common = common;
    }

    @ApiMethod( name = "message.update", path = "message/{id}", httpMethod = ApiMethod.HttpMethod.PUT )
    public void updateMessage( @Named( "id" ) Long id,
                               Message message,
                               HttpServletRequest request,
                               com.google.appengine.api.users.User authUser )
            throws Exception
    {
        common.authorize( authUser );
    }
}
