/*
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.todo.server.security.exception;

import org.apache.deltaspike.core.api.exception.control.event.ExceptionToCatchEvent;
import org.apache.deltaspike.security.api.authorization.AccessDeniedException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
public class HttpExceptionMapper implements ExceptionMapper<Throwable> {

    @Inject
    private Event<ExceptionToCatchEvent> event;

    @Override
    public Response toResponse(Throwable exception) {

        ExceptionToCatchEvent exceptionToCatchEvent = new ExceptionToCatchEvent(exception);
        try {
            event.fire(exceptionToCatchEvent);
        } catch (Throwable t) {
            Throwable exceptionCause = t.getCause();
            if (exceptionCause instanceof AccessDeniedException) {
                return Response.status(UNAUTHORIZED)
                        .entity(ExceptionMessage.AUTHENTICATION_FAILED.toString())
                        .build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

        }


        return Response.ok().build();
    }
}
