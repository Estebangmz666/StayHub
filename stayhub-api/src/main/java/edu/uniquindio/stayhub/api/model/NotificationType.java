package edu.uniquindio.stayhub.api.model;

/**
 * Enum to represent the different types of notifications that can be sent to a user.
 */
public enum NotificationType {
    /**
     * A notification indicating that a reservation has been confirmed.
     */
    RESERVATION_CONFIRMED,
    /**
     * A notification indicating that a reservation's details have been updated.
     */
    RESERVATION_UPDATED,
    /**
     * A notification for a newly requested reservation.
     */
    RESERVATION_REQUESTED,
    /**
     * A notification for a newly created reservation.
     */
    RESERVATION_CREATED,
    /**
     * A notification indicating that a reservation has been cancelled.
     */
    RESERVATION_CANCELLED,
    /**
     * A notification indicating that an accommodation's details have been updated.
     */
    ACCOMMODATION_UPDATED,
    /**
     * A notification indicating that accommodation has been deleted.
     */
    ACCOMMODATION_DELETED,
    /**
     * A notification for a newly created accommodation.
     */
    ACCOMMODATION_CREATED,
    /**
     * A welcome notification for new users or new host properties.
     */
    WELCOME,
    /**
     * A general message notification.
     */
    MESSAGE,
    /**
     * A reminder notification for an upcoming event or task.
     */
    REMINDER,
}