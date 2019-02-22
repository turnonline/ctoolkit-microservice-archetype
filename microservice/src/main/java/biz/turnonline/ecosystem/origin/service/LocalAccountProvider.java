package biz.turnonline.ecosystem.origin.service;

import biz.turnonline.ecosystem.origin.service.model.LocalAccount;
import org.ctoolkit.restapi.client.NotFoundException;

import javax.annotation.Nonnull;

/**
 * The dedicated provider to handle local account initialization and retrieval.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public interface LocalAccountProvider
{
    /**
     * Returns the local lightweight account entity instance.
     * <p>
     * If {@link LocalAccount} instance accessed for the first time will be stored in datastore.
     *
     * @param email the login email address of the account
     * @param id    the account unique identification within TurnOnline.biz Ecosystem
     * @return the local lightweight account
     * @throws NotFoundException if remote account has not been found for specified email and ID
     */
    LocalAccount initGet( @Nonnull String email, @Nonnull Long id );

    /**
     * Returns the local lightweight account entity instance identified by email account.
     *
     * @param email the login email address of the account
     * @return the local lightweight account or {@code null} if not found
     */
    LocalAccount get( @Nonnull String email );

    /**
     * Returns the local lightweight account entity instance identified by account ID.
     *
     * @param id the account unique identification
     * @return the local lightweight account or {@code null} if not found
     */
    LocalAccount get( @Nonnull Long id );
}
