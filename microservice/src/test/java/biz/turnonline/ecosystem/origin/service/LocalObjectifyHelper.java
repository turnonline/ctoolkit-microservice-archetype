package biz.turnonline.ecosystem.origin.service;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import org.ctoolkit.services.storage.guice.GuicefiedOfyFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Local Objectify Google Datastore helper, a configuration to initialize local datastore emulator.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Singleton
class LocalObjectifyHelper
{
    private final GuicefiedOfyFactory ofyFactory;

    private final LocalDatastoreHelper lDatastoreHelper;

    private Closeable session;

    @Inject
    LocalObjectifyHelper( GuicefiedOfyFactory ofyFactory,
                          LocalDatastoreHelper lDatastoreHelper )
    {
        this.ofyFactory = ofyFactory;
        this.lDatastoreHelper = lDatastoreHelper;
    }

    void reset()
    {
        try
        {
            lDatastoreHelper.reset();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }

        ObjectifyFactory factory;
        if ( ofyFactory == null )
        {
            Datastore datastore = lDatastoreHelper.getOptions().getService();
            factory = new ObjectifyFactory( datastore );
        }
        else
        {
            factory = ofyFactory;

        }

        ObjectifyService.init( factory );
        session = ObjectifyService.begin();
    }

    void close()
    {
        try
        {
            session.close();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Starts the local Datastore emulator through {@code gcloud}.
     *
     * <p>Currently the emulator does not persist any state across runs.
     */
    void start() throws IOException, InterruptedException
    {
        lDatastoreHelper.start();
    }

    /**
     * Stops the Datastore emulator.
     */
    void stop() throws InterruptedException, TimeoutException, IOException
    {
        lDatastoreHelper.stop();
    }
}
