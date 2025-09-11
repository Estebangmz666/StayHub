package edu.uniquindio.stayhub.api.model;

/**
 * Enum to represent the various statuses of a reservation.
 */
public enum ReservationStatus {
    /**
     * The reservation request is pending approval.
     */
    PENDING,
    /**
     * The reservation has been confirmed.
     */
    CONFIRMED,
    /**
     * The reservation has been cancelled.
     */
    CANCELLED,
    /**
     * The reservation period has been completed.
     */
    COMPLETED
}