package biz.turnonline.ecosystem.origin.service;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

/**
 * Dedicated module for Google Cloud Datastore emulator configuration, intended for local development or unit testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class LocalStorageModule
        extends AbstractModule
{
    @Provides
    @Singleton
    Datastore providesDatastore( LocalDatastoreHelper helper )
    {
        return helper.getOptions().getService();
    }

    @Provides
    @Singleton
    LocalDatastoreHelper providesLocalDatastoreHelper()
    {
        return LocalDatastoreHelper.create( 1.0 );
    }
}
