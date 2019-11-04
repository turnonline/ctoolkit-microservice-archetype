package biz.turnonline.ecosystem.origin.service;

import biz.turnonline.ecosystem.origin.account.LocalAccount;
import com.google.common.base.MoreObjects;
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
     * Returns the associated local lightweight account entity instance. It might act as an owner of an entities.
     * <p>
     * If {@link LocalAccount} instance is being accessed for the first time, then it will be stored
     * in datastore with updated values taken from the remote account.
     *
     * @param builder mandatory properties are: email, identityId {@link Builder#email}, {@link Builder#identityId}
     * @return the local lightweight account
     * @throws NotFoundException if remote account has not been found for specified Identity ID or Account ID
     */
    LocalAccount initGet( @Nonnull Builder builder );

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

    class Builder
    {
        private Long accountId;

        private String identityId;

        private String email;

        /**
         * Returns the account unique identification within TurnOnline.biz Ecosystem.
         */
        public Long getAccountId()
        {
            return accountId;
        }

        /**
         * Returns the user account unique identification within login provider system.
         */
        public String getIdentityId()
        {
            return identityId;
        }

        /**
         * Returns the login email address of the account.
         */
        public String getEmail()
        {
            return email;
        }

        /**
         * Sets the account unique identification within TurnOnline.biz Ecosystem.
         */
        public Builder accountId( @Nonnull Long accountId )
        {
            this.accountId = accountId;
            return this;
        }

        /**
         * Sets the user account unique identification within login provider system.
         */
        public Builder identityId( @Nonnull String identityId )
        {
            this.identityId = identityId;
            return this;
        }

        /**
         * Sets the login email address of the account.
         */
        public Builder email( @Nonnull String email )
        {
            this.email = email;
            return this;
        }

        @Override
        public String toString()
        {
            MoreObjects.ToStringHelper string = MoreObjects.toStringHelper( "Builder" );
            string.add( "Account ID", accountId )
                    .add( "Email", email )
                    .add( "Identity ID", identityId );

            return string.toString();
        }
    }
}
