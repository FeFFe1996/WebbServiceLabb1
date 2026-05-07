package com.example.webbservicelabb1.exception;

public class RateLimitExceededException extends RuntimeException {
    private final long waitSeconds;
    public RateLimitExceededException(long waitSeconds, String message) {
        super(message);
        this.waitSeconds = waitSeconds;
    }
    public long getWaitSeconds() { return waitSeconds; }
}
