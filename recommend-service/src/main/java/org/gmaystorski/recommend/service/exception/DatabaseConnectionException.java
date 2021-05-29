package org.gmaystorski.recommend.service.exception;

public class DatabaseConnectionException extends RuntimeException {

    public DatabaseConnectionException(Throwable e) {
        super(e);
    }

}
