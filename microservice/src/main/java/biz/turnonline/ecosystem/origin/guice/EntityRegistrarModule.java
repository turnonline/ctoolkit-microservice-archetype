package biz.turnonline.ecosystem.origin.guice;

import biz.turnonline.ecosystem.origin.account.LocalAccount;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.ctoolkit.services.storage.guice.EntityRegistrar;
import org.ctoolkit.services.storage.guice.GuicefiedOfyFactory;

/**
 * Before using Objectify to load or save data, you must register all entity classes used in application.
 * In order to make sure all registration occurs in right time (either in production or in tests) use this
 * entity registrar to register all service entities by installing this module
 * <pre>
 * {@code install( new EntityRegistrarModule() );}
 * </pre>
 * To leverage dependency injection within model entities, turn it on by:
 * <pre>
 * {@code bind( GuicefiedOfyFactory.class ).asEagerSingleton();}
 * </pre>
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class EntityRegistrarModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        Multibinder<EntityRegistrar> registrar = Multibinder.newSetBinder( binder(), EntityRegistrar.class );
        registrar.addBinding().to( Entities.class );
    }

    private static class Entities
            implements EntityRegistrar
    {
        @Override
        public void register( GuicefiedOfyFactory factory )
        {
            factory.register( LocalAccount.class );
        }
    }
}
