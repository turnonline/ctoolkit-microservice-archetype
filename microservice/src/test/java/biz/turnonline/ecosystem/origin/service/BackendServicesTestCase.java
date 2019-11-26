package biz.turnonline.ecosystem.origin.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.urlfetch.URLFetchServicePb;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalModulesServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.ObjectifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;

import javax.inject.Inject;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * The base class for App Engine backend services local testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Guice( modules = {
        MicroserviceModule.class,
        LocalStorageModule.class}
)
public class BackendServicesTestCase
{
    private final static Logger LOGGER = LoggerFactory.getLogger( BackendServicesTestCase.class );

    private LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch( 1 );

    private LocalObjectifyHelperTestConfig ofyHelper;

    private LocalServiceTestHelper helper;

    public static <T> T getFromFile( String json, Class<T> valueType )
    {
        return getFromFile( json, valueType, null );
    }

    public static <T> T getFromFile( String json, Class<T> valueType, T instance )
    {
        InputStream stream = valueType.getResourceAsStream( json );
        if ( stream == null )
        {
            String msg = json + " file has not been found in resource package " + valueType.getPackage() + ".";
            throw new IllegalArgumentException( msg );
        }

        T item = null;

        try
        {
            JsonFactory factory = new JsonFactory();
            factory.enable( JsonParser.Feature.ALLOW_COMMENTS );
            ObjectMapper mapper = new ObjectMapper( factory );

            if ( instance == null )
            {
                item = mapper.readValue( stream, valueType );
            }
            else
            {
                item = mapper.readerForUpdating( instance ).readValue( stream );
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "", e );
        }
        return item;
    }

    /**
     * To deserialize from JSON to {@link com.google.api.client.json.GenericJson}.
     */
    public static <T> T genericJsonFromFile( String json, Class<T> valueType )
    {
        InputStream stream = valueType.getResourceAsStream( json );
        if ( stream == null )
        {
            String msg = json + " file has not been found in resource package " + valueType.getPackage() + ".";
            throw new IllegalArgumentException( msg );
        }

        T item = null;

        try
        {
            com.google.api.client.json.JsonFactory factory = new JacksonFactory();
            item = factory.fromInputStream( stream, valueType );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        return item;
    }

    @Inject
    public void setLocalDatastoreHelper( LocalObjectifyHelper loh )
    {
        this.ofyHelper = new LocalObjectifyHelperTestConfig( loh );

        helper = new LocalServiceTestHelper( new LocalMemcacheServiceTestConfig(),
                new LocalModulesServiceTestConfig(),
                this.ofyHelper,
                new LocalTaskQueueTestConfig().setDisableAutoTaskExecution( disableAutoTaskExecution() )
                        .setTaskExecutionLatch( latch )
                        .setCallbackClass( ObjectifyAwareDeferredTaskCallback.class ) );
    }

    protected boolean awaitAndReset( long milliseconds )
    {
        try
        {
            return latch.awaitAndReset( milliseconds, TimeUnit.MILLISECONDS );
        }
        catch ( InterruptedException e )
        {
            LOGGER.error( "", e );
            return false;
        }
    }

    protected void reset( int count )
    {
        latch.reset( count );
    }

    protected boolean disableAutoTaskExecution()
    {
        return false;
    }

    @BeforeMethod
    public void beforeMethod()
    {
        helper.setUp();
    }

    @AfterMethod
    public void afterMethod()
    {
        latch.reset();
        helper.tearDown();
    }

    @BeforeSuite
    public void start()
    {
        SystemProperty.environment.set( "Development" );
        ofyHelper.start();
    }

    @AfterSuite
    public void stop()
    {
        ofyHelper.stop();
    }

    public static class ObjectifyAwareDeferredTaskCallback
            extends LocalTaskQueueTestConfig.DeferredTaskCallback
    {
        private static final long serialVersionUID = 2015389485050185487L;

        @Override
        public int execute( URLFetchServicePb.URLFetchRequest req )
        {
            Closeable session = ObjectifyService.begin();
            int statusCode = super.execute( req );

            try
            {
                session.close();
            }
            catch ( IOException e )
            {
                LOGGER.error( "", e );
            }

            return statusCode;
        }
    }
}
