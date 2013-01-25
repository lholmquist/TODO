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

import javax.ws.rs.core.Response;

public enum ExceptionMessage {

    AUTHENTICATION_FAILED("\"User authentication failed\"", Response.Status.UNAUTHORIZED);

    private String message;

    private Response.Status status;

    ExceptionMessage(String message) {
        this.message = message;
    }

    ExceptionMessage(String message, Response.Status status) {
        this.message = message;
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("{\"message\": %s }", message);
    }

}
