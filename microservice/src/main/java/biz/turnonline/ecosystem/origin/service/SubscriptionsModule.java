package biz.turnonline.ecosystem.origin.service;

import biz.turnonline.ecosystem.origin.account.AccountStewardChangesSubscription;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.ctoolkit.restapi.client.pubsub.PubsubMessageListener;
import org.ctoolkit.restapi.client.pubsub.SubscriptionsListenerModule;

/**
 * Pub/Sub subscription configuration for following:
 * <ul>
 * <li>account.changes</li>
 * </ul>
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class SubscriptionsModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new SubscriptionsListenerModule() );

        MapBinder<String, PubsubMessageListener> map;
        map = MapBinder.newMapBinder( binder(), String.class, PubsubMessageListener.class );
        map.addBinding( "account.changes" ).to( AccountStewardChangesSubscription.class );

    }
}
