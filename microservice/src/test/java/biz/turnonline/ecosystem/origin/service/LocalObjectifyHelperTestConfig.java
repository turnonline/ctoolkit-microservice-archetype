package biz.turnonline.ecosystem.origin.service;

import com.google.appengine.tools.development.testing.LocalServiceTestConfig;

/**
 * {@link LocalObjectifyHelper} wrapper aground {@link LocalServiceTestConfig}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class LocalObjectifyHelperTestConfig
        implements LocalServiceTestConfig
{
    private final LocalObjectifyHelper helper;

    LocalObjectifyHelperTestConfig( LocalObjectifyHelper helper )
    {
        this.helper = helper;
    }

    @Override
    public void setUp()
    {
        helper.reset();
    }

    @Override
    public void tearDown()
    {
        helper.close();
    }

    /**
     * Starts the local Datastore emulator.
     *
     * <p>Currently the emulator does not persist any state across runs.
     */
    void start()
    {
        helper.start();
    }

    /**
     * Stops the Datastore emulator.
     */
    void stop()
    {
        helper.stop();
    }
}
