package biz.turnonline.ecosystem.origin.api;

import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.GoogleOAuth2Authenticator;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiIssuerAudience;
import com.google.api.server.spi.config.ApiNamespace;

import static biz.turnonline.ecosystem.origin.api.EndpointsApiProfile.PROJECT_ID;

/**
 * The endpoint profile, the base class as a configuration of the REST API and generated client.
 * <p>
 * <strong>Preconfigured for Firebase Authentication (OAuth 2.0)</strong>
 * <p>
 * It provides backend services, easy-to-use SDKs, and ready-made libraries to authenticate
 * users to your app.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 * @see <a href="https://cloud.google.com/endpoints/docs/frameworks/java/authenticating-users">Authenticating Users</a>
 * @see <a href="https://cloud.google.com/endpoints/docs/openapi/when-why-api-key">Why and When to Use API Keys</a>
 */
@Api( name = "myApiName",
        canonicalName = "Example Name",
        title = "TurnOnline.biz Ecosystem Example REST API",
        version = "v1",
        description = "Example REST API",
        documentationLink = "https://developers.turnonline.biz/docs",
        namespace = @ApiNamespace( ownerDomain = "turnonline.biz", ownerName = "TurnOnline.biz, Ltd." ),
        authenticators = {GoogleOAuth2Authenticator.class, EspAuthenticator.class},
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/" + PROJECT_ID,
                        jwksUri = "https://www.googleapis.com/service_accounts/v1/metadata/x509/securetoken@system.gserviceaccount.com" )
        },
        issuerAudiences = {
                @ApiIssuerAudience( name = "firebase", audiences = PROJECT_ID )
        }
)
public class EndpointsApiProfile
{
    /**
     * The API short and stable name, might be used publicly.
     * Keep same as in {@code @Api( name = "myApiName" )}
     */
    public static final String API_NAME = "myApiName";

    static final String PROJECT_ID = "Replace-ProjectId";
}
