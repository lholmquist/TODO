package org.aerogear.todo.server.exception;

import org.jboss.aerogear.controller.spi.HttpStatusAwareException;
import org.jboss.aerogear.security.exception.HttpStatus;

/**
 * General exception thrown due to an error during the Authentication/Authorization process.
 */
public class HttpSecurityException extends RuntimeException implements HttpStatusAwareException {

    private int status;

    /**
     * Constructs a new <i>AeroGearSecurityException</i> with HTTP status response mapped from the supplied exception at {@link org.jboss.aerogear.security.exception.HttpExceptionMapper}.
     *
     * @param httpStatus
     */
    public HttpSecurityException(HttpStatus httpStatus) {
        super(httpStatus.getMessage());
        this.status = httpStatus.getCode();
    }

    /**
     * Current HTTP status
     *
     * @return HTTP response code
     */
    public int getStatus() {
        return status;
    }

    /**
     * HTTP response message
     *
     * @return response message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
