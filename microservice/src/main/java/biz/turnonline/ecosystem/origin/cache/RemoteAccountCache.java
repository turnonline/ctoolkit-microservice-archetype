package biz.turnonline.ecosystem.origin.cache;

import biz.turnonline.ecosystem.steward.model.Account;

import javax.inject.Singleton;

/**
 * The remote {@link Account} provider implementation that keeps cached instance
 * for specified time in memcache.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
@Singleton
public class RemoteAccountCache
        extends AbstractLocalResourceProvider<Account>
{
    @Override
    protected Class<Account> type()
    {
        return Account.class;
    }
}
