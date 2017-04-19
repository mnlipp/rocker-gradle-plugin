package de.mnl.gradle.rocker;

/**
 * Named RuntimeException
 */
@SuppressWarnings("serial")
public class RockerGradleException extends RuntimeException {
    public RockerGradleException(String msg) {
        super(msg);
    }

    public RockerGradleException(String msg, Exception e) {
        super(msg, e);
    }
}
