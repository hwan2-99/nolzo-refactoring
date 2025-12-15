package com.noljo.nolzo.global.error.exception;

public class SeatException extends RuntimeException {
    public SeatException(Long id) {
        super("Seat with id " + id + " is already reserved.");
    }

    public SeatException(String message) {
        super(message);
    }

    public SeatException(String message, Throwable cause) {
        super(message, cause);
    }
}
