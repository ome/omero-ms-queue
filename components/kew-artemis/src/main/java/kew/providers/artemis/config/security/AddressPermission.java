package kew.providers.artemis.config.security;

/**
 * Security permissions you can specify to control what a role can do to the
 * queues connected to an address.
 */
public enum AddressPermission {

    /**
     * Can send messages to any queue with a matching address.
     */
    Send,

    /**
     * Can consume messages from any queue with a matching address.
     */
    Consume,

    /**
     * Can create a durable queue under a matching address.
     */
    CreateDurableQueue,

    /**
     * Can delete a durable queue under a matching address.
     */
    DeleteDurableQueue,

    /**
     * Can create a non-durable queue having a matching address.
     */
    CreateNonDurableQueue,

    /**
     * Can delete a non-durable queue having a matching address.
     */
    DeleteNonDurableQueue,

    /**
     * Can manage a matching address.
     */
    Manage,

    /**
     * Can browse messages in any queue with a matching address.
     */
    Browse

}
